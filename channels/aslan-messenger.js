'use strict';
const request = require('request');
const core = require('./../core.js');
const db = require('./../aslan-messenger/aslan-db.js');

/*
* Fonction appelé par les routes lors de la reception d'un message
*/
function receivedMessage(message, user) {
    var senderID = message.user_id;
    var timeOfMessage = message.date;
    var messageText = message.text;

    var m = {
        channel: "Aslan",
        senderID: senderID,
        timestamp: timeOfMessage,
        text: messageText,
        first_name: user.first_name,
        last_name: user.name
    };

    core.receivedMessage(message);
}

/*
* Fonction appelé par le core pour envoyer un message sur la channel Aslan Messenger
*/
function sendMessage(message) {
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
	    			db.createMessage(user_id, message, function(data){
	    				console.log(data)
				    	if(data.length == 0) {
				    		res.status(500).send(JSON.stringify({error: error.toString()}));
				    	} else {
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
    }
}

/*
* Fonction appelé par le core pour envoyer des messages sur la channel Aslan Messenger
*/
function sendMessages(messages) {

}

/*
* Fonction qui envoie un message de type text
*/
function sendTextMessage(message) {

}

/*
* Fonction qui envoie un message de type quickreply
*/
function sendQuickReplyMessage(message) {

}

/*
* Fonction qui envoie un message audio/file/image/video
*/
function sendFileMessage(message) {

}

exports.receivedMessage = receivedMessage;
exports.sendMessage = sendMessage;
exports.sendMessages = sendMessages;