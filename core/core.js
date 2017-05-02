'use strict';

//const recast = require('./botlogic/recast.js');
const apiai = require('./../botlogic/apiai.js');
const facebook = require('./../channels/facebook.js');
const telegram = require('./../channels/telegram.js');
const aslan = require('./../channels/aslan-messenger.js');
const pr = require('./processing.js');
const fd = require('./field.js');
const db = require('./../AslanDBConnector.js');
var messageReceived;

/*
*   Fonction appelée par les channels lorsque l'on reçoit un message
*/
function receivedMessage(message) {
    messageReceived = message;
    console.log(message);
    if(message.location != undefined) {
        getLastIntent(message);
    } else {
        runLogicLayer(message);
    }
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
    console.log("APIAI sended : ", JSON.stringify(response));
    var intent = response.result.metadata.intentName;
    processing(intent, response);
}

/*
*   Fonction qui traite le message en fonction du contexte du message
*   La fonction renvoie la réponse à la channel 
*/
function processing(intent, response) {
    switch(intent) {
        case "bonjour" :
            pr.processingGrettings(messageReceived, response);
            break;
        case "heure" :
            pr.processingHour(response);
            break;
        case "fontaine" :
            pr.processingFountain(response);
            break;
        case "habitant":
            pr.processingCitizen(response);
            break;
        case "jour":
            pr.processingDate(response);
            break;
        case "meteo":
            pr.processingWeather(response);
            break;
        case "restaurant":
            pr.processingRestaurant(response, null);
            break;
        default :
            prepareMessage(response.result.fulfillment.speech);
    }
}

/*
*   Fonction qui prépare le message
*   Recherche de champs à completer
*   Voir la documentation pour le formalisme
*    {{qr:Oui}} {{qr:Non}}
*/
function prepareMessage(response) {
    var messages = [];
    var replie = response;
    var fields = fd.extractFields(replie);
    var quickreplies = extractQuickReplies(fields);
    replie = fd.removeFields(replie);
    if(quickreplies.length > 0) {
        prepareMessageWithQuickReply(replie, quickreplies, messages);
    } else if(replie != undefined ) {
        prepareMessageWithText(replie, messages);
    }
    sendMessages(messages);
}


/*
*   Fonction qui extrait les quick replies
*/
function extractQuickReplies(fields) {
    var quickreplies = [];
    for(var i=0;i<fields.length;i++) 
    {
        var json = undefined;
        try {
            json = JSON.parse(fields[i]);
        } catch (err){
        }
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

function askLocation() {
    var message = {
        type: "location",
        senderID: messageReceived.senderID,
        channel: messageReceived.channel,
        text: "J'ai besoin de votre localisation"
    }
    sendMessages([message]);
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
        case "Aslan":
            aslan.sendMessages(messages);
            break;
        default:
            return;
        }
    }
}

function getLastIntent(message) {
    db.getData("conversation", {sessionId: message.senderId}, function(data) {
        if(data.length == 0) {
            console.log("Aucun intent trouvé");
        } else {
            switch(data[0].result.metadata.intentName) {
            case "restaurant":
                apiai.sendMessage(message.senderID, "[localisation:success]", callbackLogicLayer)
                pr.processingRestaurant(message.location);
                break;
            default:
               break;
            }
        }
    });
}

exports.askLocation = askLocation;
exports.receivedMessage = receivedMessage;
exports.prepareMessage = prepareMessage;