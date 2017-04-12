'use strict';
const request = require('request');
const core = require('./../core.js');

/**
 * CREDENTIALS
 **/
const VALIDATION_TOKEN = process.env.VALIDATION_TOKEN;
const PAGE_ACCESS_TOKEN = process.env.PAGE_ACCESS_TOKEN;

/*
* Fonction appelé par Facbook pour checker le serveur
*/
function webhook(req, res) {
    return req.query['hub.mode'] === 'subscribe' && req.query['hub.verify_token'] === VALIDATION_TOKEN;
}

/*
* Fonction appelé par les routes lors de la reception d'un message
*/
function receivedMessage(req, res) {
    var data = req.body;
    console.log(data);
    console.log(data);
    console.log(data);
    if (data.object != undefined && data.object === 'page') {
        //Message reçu de Facebook
        data.entry.forEach(function(entry) {
            var pageID = entry.id;
            var timeOfEvent = entry.time;
            entry.messaging.forEach(function(event) {
                if (event.message) {
                    console.log("Event received : ", JSON.stringify(event));
                    receivedMessage(event);
                }
            });
        });
    } else {
        //Pour tester via CURL
        receivedMessage(req.body);
    }
}
/*
* Fonction qui permet de traiter un message reçu
*/
function extractMessage(event) {

    var senderID = event.sender.id;
    var recipientID = event.recipient.id;
    var timeOfMessage = event.timestamp;
    var message = event.message;

    console.log("Received message from Facebook for user %d at %d with message:", 
      senderID, timeOfMessage);
    console.log(JSON.stringify(message));

    var messageId = message.mid;

    var messageText = message.text;
    var messageAttachments = message.attachments;

    var message = {
        channel: "Facebook",
        sender: senderID,
        timestamp: timeOfMessage,
        text: messageText
    };
    core.receivedMessage(message);
}

/*
* Fonction appelé par le core pour envoyer un message sur la channel Facebook
*/
function sendMessage(message) {
        console.error("We will send a message.");
        sendTextMessage(message);
}

/*
* Fonction qui envoie un message de type text
*/
function sendTextMessage(message) {
    if(message.sender != undefined && message.text != undefined) {
        var messageData = {
            recipient: {
                id: message.sender
            },
            message: {
                text: message.text
            }
        };
        callSendAPI(messageData);
    }
}

/*
* Fonction qui appel l'API messages de Facebook
*/
function callSendAPI(messageData) {
  request({
      uri: 'https://graph.facebook.com/v2.6/me/messages',
      qs: { access_token: PAGE_ACCESS_TOKEN },
      method: 'POST',
      json: messageData

  }, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var recipientId = body.recipient_id;
            var messageId = body.message_id;

            console.log("Successfully sent generic message with id %s to recipient %s", 
                messageId, recipientId);
        } else {
            console.error("Unable to send message.");
            console.error(response);
            console.error(error);
        }
    });
}

exports.webhook = webhook
exports.receivedMessage = receivedMessage
exports.sendMessage = sendMessage