'use strict';
const db = require('../AslanDBConnector.js');

/*
*	Fonction qui crée un utilisateur en base de données
*/
function createUser(user, password) {
	return null;
}

/*
*	Fonction qui renvoie l'utilisateur en base de données s'il existe
*/
function getUser(user, password) {
	return null;
}

/*
*	Fonction qui renvoie les messages à un utilisateurs
*	Si lastMessageId = -1, alors on renvoie tout
*	Sinon on renvoie les messages après lastMessageId
*/
function getMessage(userId, lastMessageId) {
	return null;
}

/*
*	Fonction qui enregistre un message
*/
function getAllMessage(userId) {
	return null;
}

/*
*	Fonction qui enregistre un message
*/
function createMessage(userId, message) {
	return null;
}

exports.createUser = createUser;
exports.getUser = getUser;
exports.getMessage = getMessage;
exports.getAllMessage = getAllMessage;
exports.createMessage = createMessage;