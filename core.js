'use strict';

//const recast = require('./botlogic/recast.js');
const apiai = require('./botlogic/apiai.js');
const facebook = require('./channels/facebook.js');
const telegram = require('./channels/telegram.js');
const aslan = require('./channels/aslan-messenger.js');

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
    /*recast.sendMessage(message.text, callbackLogicLayer);*/
    apiai.sendMessage(message.senderID, message.text, callbackLogicLayer)
}

/*
*   Fonction de callback appelée par la couche logique
*/
function callbackLogicLayer(response) {
    /*
    *   Traitement
    */
    console.log("APIAI sended : ", JSON.stringify(response));
    /*
    *   Préparation du message
    */
    prepareMessage(response);
}

/*
*   Fonction qui prépare le message
*   Recherche de champs à completer
*   Voir la documentation pour le formalisme
*    {{qr:Oui}} {{qr:Non}}
*/
function prepareMessage(response) {
    var messages = [];
    var replie = response.result.fulfillment.speech;
    var fields = extractFields(replie);
    var quickreplies = extractQuickReplies(fields);
    replie = removeFields(replie);
    if(quickreplies.length > 0) {
        prepareMessageWithQuickReply(replie, quickreplies, messages);
    } else if(response.replies[i] != undefined ) {
        prepareMessageWithText(replie, messages);
    }
    sendMessages(messages);
}

/*
*   Fonction qui supprime les champs {{ }}
*/
function removeFields(response) {
    var string = response;
    var fields = extractFields(response);
    for(var i=0;i<fields.length;i++) 
    {
        string = string.replace('{' + fields[i] + '}', "");
    }
    return string;
}

/*
*   Fonction qui extrait les champs {{  }}
*   Fait un post traitement sur les champs pour les rendre conforme JSON
*/
function extractFields(response) {
    var fields = response.match(/(\{\{.*\}\})/g);
    if(fields == undefined) {
        fields = [];
    }
    for(var i=0;i<fields.length;i++) {
        fields[i] = fields[i].substr(1);
        fields[i] = fields[i].slice(0, -1);
    }
    return fields;
}

/*
*   Fonction qui extrait les quick replies
*/
function extractQuickReplies(fields) {
    var quickreplies = [];
    for(var i=0;i<fields.length;i++) 
    {
        var json = JSON.parse(fields[i]);
        if(json != undefined && json.qr != undefined && json.qr.length > 0) {
            for(var j=0;j<json.qr.length;j++) {
                quickreplies.push(json.qr[j]);
            }
        }
    }
    return quickreplies;
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