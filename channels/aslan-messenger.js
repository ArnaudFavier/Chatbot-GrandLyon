'use strict';

const request = require('request');
const core = require('./../core/core.js');
const db = require('./../aslan-messenger/aslan-db.js');

/*
* Fonction appelé par les routes lors de la reception d'un message
*/
function receivedMessage(message, user) {
    console.log("Aslan received a message");
    var m = {
        channel: "Aslan",
        senderID: user._id.toString(),
        timestamp: new Date(),
        text: message.text,
        first_name: user.firstname,
        last_name: user.name
    };
    if(message.lat != undefined && message.long != undefined) {
        m["location"] = {lat : message.lat, long: message.long};
    } 
    console.log(JSON.stringify(m));
    core.receivedMessage(m);
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
        case "location":
            sendLocationMessage(message);
            break;
        case "template":
            sendTemplateMessage(message);
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
    console.log(JSON.stringify(m));
    var user_id = m.senderID.toString();
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
        save(message);
    }
}

/*
* Fonction qui envoie un message de type quickreply
*/
function sendQuickReplyMessage(message) {
    if(message.senderID != undefined && message.text != undefined) {
        message.message = {text : message.text, type: "quickreplies", quickreplies: message.choices};
        save(message);
    }
}

/*
* Fonction qui envoie un message de type location
*/
function sendLocationMessage(message) {
    if(message.senderID != undefined && message.text != undefined) {
        message.message = {text : message.text, type: "location"};
        save(message);
    }
}

/*
* Fonction qui envoie un message de type template
*/
function sendTemplateMessage(message) {
    if(message.senderID != undefined && message.text != undefined && message.attachment != undefined) {
        message.message = {text : message.text, attachment: message.attachment, type: "template"};
        save(message);
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