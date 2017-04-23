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
    			res.writeHead(403, {'Content-Type': 'application/json'});
			    res.write(JSON.stringify({error: "Unauthorized account"}));
    		} else if(results.length == 1) {
    			db.getUser(username, password, results[0].salt, function(error, results) {
			    	if(error) {
			    		res.writeHead(500, {'Content-Type': 'application/json'});
			    		res.write(JSON.stringify({error: error.toString()}));
			    	} else {
			    		res.writeHead(200, {'Content-Type': 'application/json'});
			    		res.write(JSON.stringify({username: username, email: results[0].email, token : results[0].token}));
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
	if(data.email != undefined && data.username != undefined && data.password != undefined) {
		var email = data.email;
		var username = data.username;
	    var password = data.password;
	    db.createUser(email, username, password, function(error, results) {
	    	if(error) {
	    		res.writeHead(500, {'Content-Type': 'application/json'});
	    		res.write(JSON.stringify({error: error.toString()}));
	    	} else {
	    		res.writeHead(200, {'Content-Type': 'application/json'});
	    		res.write(JSON.stringify({username: username, email: results[0].email, token : results[0].token}));
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