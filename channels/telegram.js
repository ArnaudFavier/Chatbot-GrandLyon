'use strict';
const request = require('request');
const core = require('./../core.js');
const TelegramBot = require('node-telegram-bot-api');

/**
 * CREDENTIALS
 **/
const TELEGRAM_ACCESS_TOKEN = "338652885:AAFkX3S6s2GcRbHOkZ5WHAllBhbrf40FPiI";//process.env.TELEGRAM_ACCESS_TOKEN;

var telegram = new TelegramBot(TELEGRAM_ACCESS_TOKEN, { polling: true });

telegram.on("text", (message) => {
    receivedMessage(message);
    //telegram.sendMessage(message.chat.id, "Hello world");
});

/*
* Fonction appelé par quand on recoit un message
*/
function receivedMessage(message) {
    console.log(JSON.stringify(message));
    var senderID = message.chat.id;
    /*var message = {
        channel: "Telegram",
        senderID: senderID,
        timestamp: timeOfMessage,
        text: messageText
    };
    core.receivedMessage(message);*/
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

exports.sendMessage = sendMessage
exports.sendMessages = sendMessages