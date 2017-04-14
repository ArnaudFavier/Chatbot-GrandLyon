'use strict';

const recast = require('./botlogic/recast.js');
const facebook = require('./channels/facebook.js');
const telegram = require('./channels/telegram.js');

var messageReceived;

/*
*   Fonction appelée par les channels lorsque l'on reçoit un message
*/
function receivedMessage(message) {
    messageReceived = message;
    runLogicLayer(message);
}

/*
*   Fonction qu lance la couche logique avec NLP
*/
function runLogicLayer(message) {
    /*botlogic.defineCallback(callbackLogicLayer);
    if(message.senderID != undefined && message.text != undefined) {
        botlogic.sendMessage(message.text, `session-${message.senderID}`, {});
    }*/
    recast.sendMessage(message.text, callbackLogicLayer);
}

/*
*   Fonction de callback appelée par la couche logique
*/
function callbackLogicLayer(request, response) {
    /*
    *   Traitement
    */
    console.log('Custom callback Wit : ', JSON.stringify(response));
    /*
    *   Préparation du message
    */
    prepareMessage(response);
}

/*
*   Fonction qui prépare le message
*/
function prepareMessage(response) {
    var messages = [];
    if(response.quickreplies != undefined) {
        prepareMessageWithQuickReply(response.text, response.quickreplies, messages);
    } else if(response.text != undefined ) {
        prepareMessageWithText(response.text, messages);
    }
    sendMessages(messages);
}

/*
*   Fonction qui prépare un message de type text
*/
function prepareMessageWithText(text, messages) {
    var message = {
        type: "text",
        senderID: messageReceived.senderID,
        channel: messageReceived.channel,
        text: text
    };
    messages.push(message);
}

/*
*   Fonction qui prépare un message de type quickreply
*/
function prepareMessageWithQuickReply(text, quickreply, messages) {
    var message = {
        type: "quickreply",
        senderID: messageReceived.senderID,
        channel: messageReceived.channel,
        text: text,
        choices:[]
    }
    if(Array.isArray(quickreply)) {
        for(var i=0;i<quickreply.length;i++) {
            message.choices.push(quickreply[i]);
        }
    } else {
        message.choices.push(quickreply);
    }
    messages.push(message);
}

/*
*   Fonction qui appelle la fonction d'envoie de message
*/
function sendMessages(messages) {
    if(messages.length > 0) {
        switch(messages[0].channel) {
            case "Facebook":
            facebook.sendMessages(messages);
            break;
            case "Telegram":
            telegram.sendMessages(messages);
            break;
            default:
            return;
        }
    }
}

exports.receivedMessage = receivedMessage;