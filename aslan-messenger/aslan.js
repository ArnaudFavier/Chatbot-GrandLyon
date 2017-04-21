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
    	if(db.getUser(username, password) != null) {
            res.writeHead(200, {'Content-Type': 'text/html'});
	    	res.write(data.toString());
	    	return;
	    }
    }
    res.sendStatus(500);
	return;
}

/*
*	Fonction qui vérifie si un utilisateur existe en base de données
*/
function register(req, res) {
	var data = req.body;
	if(data.username != undefined && data.password != undefined) {
		var username = data.username;
	    var password = data.password;
	    if(db.getUser(username, password) != null) {
            res.writeHead(200, {'Content-Type': 'text/html'});
	    	res.write(data.toString());
	    	return true;
	    }
	}
	res.sendStatus(500);
	return;
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