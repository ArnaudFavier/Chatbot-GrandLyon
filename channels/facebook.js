'use strict';
const request = require('request');
const core = require('./../core.js');

/**
* CREDENTIALS
**/
const VALIDATION_TOKEN = "58eb669e-9a3c-4940-ba2a-45833ed28ff1";
const PAGE_ACCESS_TOKEN = "EAAbiTcER2bkBAConk7qPEvqaIRT0MucHZBCDVxZBqZB14qUofVZCnryAbWxXn9atzofmG5jm98y1iZB5RIMxagxCL6ar0QGS8JzqHEoGTlX9vYTiHMfyyUwFDz7ORZCsSzjzQZCsOOyvCGdgTJva9RvHMNmAP0H7eQ4C2eTmOohGAZDZD";


module.exports = {
    webhook: function(req, res) {
        return req.query['hub.mode'] === 'subscribe' && req.query['hub.verify_token'] === VALIDATION_TOKEN;
    },

    receivedMessage: function(req, res) {
        var data = req.body;
        if (data.object === 'page') {
            data.entry.forEach(function(entry) {
                var pageID = entry.id;
                var timeOfEvent = entry.time;
                entry.messaging.forEach(function(event) {
                    if (event.message) {
                      receivedMessage(event);
                    } else {
                      console.log("Webhook received unknown event: ", event);
                    }
                });
            });
        }
    },

    sendTextMessage: function(message) {
        sendTextMessage(message);
    }
};

/*
* Fonction qui permet de traiter un message reçu
*/
function receivedMessage(event) {
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
* Fonction qui appel l'API messages de Facebook
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