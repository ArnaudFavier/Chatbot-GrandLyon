'use strict';
const request = require('request');
const core = require('./../core/core.js');

/**
 * CREDENTIALS
 **/
const VALIDATION_TOKEN = process.env.FACEBOOK_VALIDATION_TOKEN;
const PAGE_ACCESS_TOKEN = process.env.FACEBOOK_PAGE_ACCESS_TOKEN;

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
    if (data.object === 'page') {
        //Message reçu de Facebook
        data.entry.forEach(function(entry) {
            var pageID = entry.id;
            var timeOfEvent = entry.time;
            entry.messaging.forEach(function(event) {
                if (event.message) {
                    console.log("Event received : ", JSON.stringify(event));
                    extractMessage(event);
                }
            });
        });
    } else {
        //Pour tester via CURL
        extractMessage(req.body);
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
        senderID: senderID,
        timestamp: timeOfMessage,
        text: messageText,
        first_name: message.first_name,
        last_name: message.last_name
    };
    /*
    *   Coordonnées
    */
    if(messageAttachments != undefined && messageAttachments.payload != undefined && messageAttachments.payload.coordinates != undefined) {
        message["location"] = coordinates;
    }

    core.receivedMessage(message);
}

/*
* Fonction appelé par le core pour envoyer un message sur la channel Facebook
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
* Fonction appelé par le core pour envoyer des messages sur la channel Facebook
*/
function sendMessages(messages) {
    for(var i=0;i<messages.length;i++) {
        sendMessage(messages[i]);
    }
}

/*
* Fonction qui envoie un message de type text
*/
function sendTextMessage(message) {
    console.log("Messages sended : ", JSON.stringify(message));
    if(message.senderID != undefined && message.text != undefined) {
        var messageData = {
            recipient: {
                id: message.senderID
            },
            message: {
                text: message.text
            }
        };
        callSendAPI(messageData);
    }
}

/*
* Fonction qui envoie un message de type quickreply
*/
function sendQuickReplyMessage(message) {
    console.log("Messages sended : ", JSON.stringify(message));
    if(message.senderID != undefined && message.text != undefined) {
        var messageData = {
            recipient: {
                id: message.senderID
            },
            message: {
                text: message.text,
                quick_replies: []
            }
        };
        for(var i=0;i<message.choices.length;i++) {
            var quickreply = {
                content_type: "text",
                title: message.choices[i],
                payload: message.choices[i]
            };
            messageData.message.quick_replies.push(quickreply);
        }
        callSendAPI(messageData);
    }
}

/*
* Fonction qui envoie un message audio/file/image/video
*/
function sendFileMessage(message) {
    console.log("Messages sended : ", JSON.stringify(message));
    if(message.senderID != undefined && message.text != undefined) {
        var messageData = {
            recipient: {
                id: message.senderID
            },
            message: {
                attachment:{
                    type: message.type,
                    payload: {
                        url: message.text
                    }
                }
            }
        };
        callSendAPI(messageData);
    }
}

/*
* Fonction qui appel l'API messages de Facebook
*/
function callSendAPI(messageData) {
    console.log("Messages sended via API : ", JSON.stringify(messageData));
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
exports.sendMessages = sendMessages