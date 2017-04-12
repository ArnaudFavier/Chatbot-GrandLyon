'use strict';
// Import Wit.Ai
const botlogic = require('./botlogic/botlogic.js');
const facebook = require('./channels/facebook.js');

var messageReceived;

module.exports = {
    receivedMessage: function(message) {
        receivedMessage(message);
    }
};

/*
*   Fonction appelée par les channels lorsque l'on reçoit un message
*/
function receivedMessage(message) {
    /*  Traitement 
    */
    messageReceived = message; // Variable globale
    runLogicLayer(message);
}

/*
*   Fonction qu lance la couche logique avec NLP
*/
function runLogicLayer(message) {
    botlogic.defineCallback(callbackLogicLayer);
    if(message.sender != undefined && message.text != undefined) {
        botlogic.sendMessage(message.text, `session-${message.sender}`, {});
    }
}

/*
*   Fonction de callback appelée par la couche logique
*/
function callbackLogicLayer(request, response){
    messageReceived.text = response.text
    console.log('Custom callback Wit : ', JSON.stringify(response));
    sendMessage(messageReceived);
}

/*
*   Fonction qui appelle la fonction d'envoie de message
*/
function sendMessage(message) {
    switch(message.channel) {
        case undefined:
            break;
        case "Facebook":
            facebook.sendMessage(message);
            break;
        case "Telegram":
            break;
    }
}