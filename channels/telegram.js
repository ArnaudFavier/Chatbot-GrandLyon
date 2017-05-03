'use strict';

const request = require('request');
const core = require('./../core/core.js');
const TelegramBot = require('node-telegram-bot-api');

/**
 * CREDENTIALS
 **/
const TELEGRAM_ACCESS_TOKEN = process.env.TELEGRAM_ACCESS_TOKEN;

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
    var message = {
        channel: "Telegram",
        senderID: senderID,
        timestamp: message.date,
        text: message.text,
        first_name: message.from.first_name,
        last_name: message.from.last_name     
    };
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
        case "location":
            sendLocationMessage(message);
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
        telegram.sendMessage(message.senderID, message.text);
    }
}

/*
* Fonction qui envoie un message de type quickreply
*/
function sendQuickReplyMessage(message) {
    console.log("Messages sended : ", JSON.stringify(message));
    if(message.senderID != undefined && message.text != undefined) {
        var replykeyboard = {keyboard:[]};
        for(var i=0;i<message.choices.length;i++) {
            var button = [{
                text: message.choices[i]
            }];
            replykeyboard.keyboard.push(button);
        }
        //console.log(JSON.stringify({reply_markup: {keyboard:[[{text:"coucou"}]]}}));
        telegram.sendMessage(message.senderID, message.text, {reply_markup: replykeyboard});
    }
}

/*
* Fonction qui envoie un message de type location
*/
function sendLocationMessage(message) {
    console.log("Messages sended : ", JSON.stringify(message));
    if(message.senderID != undefined && message.text != undefined) {
        var replykeyboard = {keyboard:[], one_time_keyboard:true};
        var button = [{
            text: message.text,  
            request_location : true
        }];
        replykeyboard.keyboard.push(button);
        //telegram.sendMessage(message.senderID, message.text, {reply_markup: replykeyboard});
        telegram.sendMessage(message.senderID, message.text, {reply_markup: {keyboard:[[{text:"coucou"}]]}});
    }
}

/*
* Fonction qui envoie un message audio/file/image/video
*/
function sendFileMessage(message) {
    /*console.log("Messages sended : ", JSON.stringify(message));
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
    }*/
}

exports.sendMessage = sendMessage
exports.sendMessages = sendMessages