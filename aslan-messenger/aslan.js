'use strict';
const request = require('request');
const messenger = require('../channels/aslan-messenger.js');
const db = require('./aslan-db.js');

/*
*	Fonction qui crée un utilisateur en base de données
*/
function signIn(req, res) {
	var data = req.body;
    var username = data.username;
    var password = data.password;
    if(data.username != undefined && data.password != undefined) {
    	db.userExist(username, function(error, results) {
    		if(results.length == 0) {
			    res.status(403).send(JSON.stringify({error: "Unauthorized account"}));
    		} else if(results.length == 1) {
    			db.getUser(username, password, results[0].salt, function(error, results) {
			    	if(error) {
			    		res.status(500).send(JSON.stringify({error: error.toString()}));
			    	} else {
			    		res.status(200).send(JSON.stringify({id: results[0]._id.toString(), username: username, email: results[0].email, firstname: results[0].firstname, 
			    			name: results[0].name, token : results[0].token}));
			    	}
			    }); 
    		} else {
    			res.writeHead(500, {'Content-Type': 'application/json'});
    			res.write(JSON.stringify({error: "Contactez un administrateur"}));
    			console.log("Error : multiple user");
    		}
    	});
    }
}

/*
*	Fonction qui vérifie si un utilisateur existe en base de données
*/
function register(req, res) {
	var data = req.body;
	if(data.email != undefined && data.username != undefined && data.password != undefined
		&& data.firstname != undefined && data.name != undefined) {
		var email = data.email;
		var username = data.username;
	    var password = data.password;
	    var firstname = data.firstname;
	    var name = data.name;
	    db.userExist(username, function(error, results) {
    		if(results.length > 0) {
			    res.status(403).send(JSON.stringify({error: "Unauthorized account"}));
    		} else if(results.length == 0) {
    			db.createUser(email, firstname, name, username, password, function(data) {
    				console.log(data)
			    	if(data.length == 0) {
			    		res.status(500).send(JSON.stringify({error: error.toString()}));
			    	} else {
			    		res.status(200).send(JSON.stringify({id: data[0]._id.toString() , name: name, firstname: firstname, username: username, email: data[0].email, token : data[0].token}));
			    	}
			    });
    		}
    	});
	} else {
		res.writeHead(422, {'Content-Type': 'application/json'});
		res.write(JSON.stringify({error: "JSON Invalid"}));
	}
}

/*
*	Fonction qui renvoie les messages à un utilisateurs
*	Si lastMessageId = -1, alors on renvoie tout
*	Sinon on renvoie les messages après lastMessageId
*/
function message(req, res) {
	var data = req.body;
    var user_id = data.user_id;
    var message_id = data.message_id;
}

/*
*	Fonction qui enregistre un message
*/
function receive(req, res) {
	var data = req.body;
    var user_id = data.user_id;
    var message = data.message;

    messenger.receivedMessage(req, res);
}

exports.signIn = signIn;
exports.register = register;
exports.message = message;
exports.receive = receive;