'use strict';
const request = require('request');
const messenger = require('../channels/aslan-messenger.js');
const db = require('./aslan-db.js');

/*
*	Fonction qui crée un utilisateur en base de données
*/
function signIn(req, res) {
	var data = req.body;
    var email = data.email;
    var password = data.password;
    if(data.email != undefined && data.password != undefined) {
    	db.userExist(email, function(error, results) {
    		if(results.length == 0) {
    			console.log("Error : user doesn't exist");
			    res.status(403).send(JSON.stringify({error: "Unauthorized account"}));
    		} else if(results.length == 1) {
    			db.getUser(email, password, results[0].salt, function(error, results) {
			    	if(error) {
			    		res.status(500).send(JSON.stringify({error: error.toString()}));
			    	} else {
			    		console.log(results[0]._id.toString());
			    		console.log(JSON.stringify({_id: results[0]._id.toString(), email: results[0].email, firstname: results[0].firstname, 
			    			name: results[0].name, token : results[0].token}));
			    		res.status(200).send(JSON.stringify({_id: results[0]._id.toString(), email: results[0].email, firstname: results[0].firstname, 
			    			name: results[0].name, token : results[0].token}));
			    	}
			    }); 
    		} else {
    			res.status(500).send("Contactez un administrateur");
    			console.log("Error : multiple user");
    		}
    	});
    } else {
		res.status(422).send(JSON.stringify({error: "JSON Invalid"}));
	}
}

/*
*	Fonction qui vérifie si un utilisateur existe en base de données
*/
function register(req, res) {
	var data = req.body;
	if(data.email != undefined && data.password != undefined
		&& data.firstname != undefined && data.name != undefined && fieldIsValid(data.password) && 
		fieldIsValid(data.firstname) && fieldIsValid(data.name) &&  emailIsValid(data.email)) {
		var email = data.email;
	    var password = data.password;
	    var firstname = data.firstname;
	    var name = data.name;
	    db.userExist(email, function(error, results) {
    		if(results.length > 0) {
    			console.log("Error : user already exist");
			    res.status(403).send(JSON.stringify({error: "Unauthorized account"}));
    		} else if(results.length == 0) {
    			db.createUser(email, firstname, name, password, function(data) {
    				console.log(data)
			    	if(data.length == 0) {
			    		res.status(500).send(JSON.stringify({error: error.toString()}));
			    	} else {
			    		res.status(200).send(JSON.stringify({_id: data[0]._id.toString() , name: name, firstname: firstname, email: data[0].email, token : data[0].token}));
			    	}
			    });
    		}
    	});
	} else {
		res.status(422).send(JSON.stringify({error: "JSON Invalid"}));
	}
}

/*
*	Fonction qui renvoie les messages à un utilisateurs
*	Si lastMessageId = -1, alors on renvoie tout
*	Sinon on renvoie les messages après lastMessageId
*/
function message(req, res) {
	var data = req.params;
    var user_id = data.user_id;
    var token = data.token;
    var message_id = data.message_id;
     if(user_id != undefined && token != undefined) {
    	console.log(JSON.stringify(data));
    	db.getUserById(user_id, function(error, results) {
    		console.log(JSON.stringify(results));
    		if(results.length == 1) {
    			if(results[0].token == token) {
    				if(message_id == undefined || message_id == "-1") {
    					db.getAllMessage(user_id, function(error, results){
    						console.log("Cas 1")
    						console.log(results);
    						res.status(200).send(JSON.stringify({messages:results}));
    					});
    				} else {
	    				db.getMessage(user_id, message_id, function(error, results){
		    				console.log("Cas 2")
		    				console.log(results)
					    	res.status(200).send(JSON.stringify({messages:results}));
		    			});
    				}
	    		} else {
	    			res.status(403).send(JSON.stringify({error: "Token invalid"}));
	    			console.log("Error : token invalid");
	    		}
    		} else {
    			console.log("Error : user not found");
			    res.status(404).send(JSON.stringify({error: "User not found"}));
    		} 
    	});
    } else {
		res.status(422).send(JSON.stringify({error: "JSON Invalid"}));
	}
}

/*
*	Fonction qui enregistre un message
*/
function receive(req, res) {
	var data = req.body;
    var user_id = data.user_id;
    var token = data.token;
    var message = data.message;
    if(user_id != undefined && token != undefined && message != undefined) {
    	console.log(JSON.stringify(data));
    	db.getUserById(user_id, function(error, results) {
    		console.log(JSON.stringify(results));
    		if(results.length == 1) {
    			if(results[0].token == token) {
	    			db.createMessage(user_id, message, false, function(data){
	    				console.log(data)
				    	if(data.length == 0) {
				    		res.status(500).send(JSON.stringify({error: error.toString()}));
				    	} else {
				    		messenger.receivedMessage(message, results[0]);
				    		res.status(200).send(JSON.stringify(data[0]));
				    	}
	    			});
	    		} else {
	    			res.status(403).send(JSON.stringify({error: "Token invalid"}));
	    			console.log("Error : token invalid");
	    		}
    		} else {
    			console.log("Error : user not found");
			    res.status(404).send(JSON.stringify({error: "User not found"}));
    		} 
    	});
    } else {
		res.status(422).send(JSON.stringify({error: "JSON Invalid"}));
	}
}


function emailIsValid(email) {
	var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function fieldIsValid(content) {
	return content.length > 0 &&
		content.replace(" ", "").length	 > 0;
}

exports.signIn = signIn;
exports.register = register;
exports.message = message;
exports.receive = receive;