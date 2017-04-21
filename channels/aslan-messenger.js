'use strict';
const request = require('request');
const core = require('./../core.js');

/**
 * CREDENTIALS
 **/
const VALIDATION_TOKEN = process.env.FACEBOOK_VALIDATION_TOKEN;
const PAGE_ACCESS_TOKEN = process.env.FACEBOOK_PAGE_ACCESS_TOKEN;

/*
* Fonction appelé par les routes lors de la reception d'un message
*/
function receivedMessage(req, res) {
    
}

/*
* Fonction appelé par le core pour envoyer un message sur la channel Facebook
*/
function sendMessage(message) {

}

/*
* Fonction appelé par le core pour envoyer des messages sur la channel Facebook
*/
function sendMessages(messages) {

}

/*
* Fonction qui envoie un message de type text
*/
function sendTextMessage(message) {

}

/*
* Fonction qui envoie un message de type quickreply
*/
function sendQuickReplyMessage(message) {

}

/*
* Fonction qui envoie un message audio/file/image/video
*/
function sendFileMessage(message) {

}

exports.receivedMessage = receivedMessage
exports.sendMessage = sendMessage
exports.sendMessages = sendMessages