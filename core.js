'use strict';
module.exports = {
    receivedMessage: function(message) {
        receivedMessage(message);
    }
};

// Import Wit.Ai
const botlogic = require('./botlogic/botlogic.js');
var facebook = require('./channels/facebook.js');

var messageReceived;



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
    console.log('Message to send : ', JSON.stringify(messageReceived));
    sendMessage(messageReceived);
}

/*
*   Fonction qui appelle la fonction d'envoie de message
*/
function sendMessage(message) {
    var facebook = require('./channels/facebook.js');
    try {
        facebook.sendMessage(message);
    } catch (err) {
        console.log(facebook);
    }
    /*if(message.channel == "Facebook") {
       
    }*/
}