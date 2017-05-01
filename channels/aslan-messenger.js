'use strict';
const request = require('request');
const core = require('./../core/core.js');
const db = require('./../aslan-messenger/aslan-db.js');

/*
* Fonction appelé par les routes lors de la reception d'un message
*/
function receivedMessage(message, user) {
    console.log("Aslan received a message");
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
	switch(message.type) {
        case "text":
            sendTextMessage(message);
        break;
        case "quickreply":
            sendQuickReplyMessage(message);
        break;
    }
}

/*
* Fonction appelé par le core pour envoyer des messages sur la channel Aslan Messenger
*/
function sendMessages(messages) {
    for(var i=0;i<messages.length;i++) {
        sendMessage(messages[i]);
    }
}

/*
* Fonction qui sauvegarde un message en DB
*/
function save(m) {
    var user_id = m.user_id;
    var message = m.message;
    if(user_id != undefined && message != undefined) {
        db.createMessage(user_id, message, true, function(data){
            console.log(data)
            if(data.length == 0) {
                console.log("Error create message");
            } else {
                console.log(JSON.stringify(data[0]));
            }
        });
    }
}

/*
* Fonction qui envoie un message de type text
*/
function sendTextMessage(message) {
    if(message.senderID != undefined && message.text != undefined) {
        message.message = {text : message.text, type: "text"};
        save(messageData);
    }
}

/*
* Fonction qui envoie un message de type quickreply
*/
function sendQuickReplyMessage(message) {
    if(message.senderID != undefined && message.text != undefined) {
        message.message = {text : message.text, type: "text"};
        save(messageData);
    }
}

/*
* Fonction qui envoie un message audio/file/image/video
*/
function sendFileMessage(message) {

}

exports.receivedMessage = receivedMessage;
exports.sendMessage = sendMessage;
exports.sendMessages = sendMessages;