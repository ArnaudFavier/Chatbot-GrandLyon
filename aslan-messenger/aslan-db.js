'use strict';

const crypto = require('crypto');
const randtoken = require('rand-token');
const MongoObjectID = require("mongodb").ObjectID;
const db = require('./aslan-db-connector.js');

/*
*	Fonction qui crée un utilisateur en base de données
*/
function createUser(email, firstname, name, password, callback) {
	var salt = genRandomString(16);
	password = passwordHash(password, salt);
	var token = randtoken.generate(64);
	db.insertData("users", { email: email, firstname: firstname, name: name, password: password, salt: salt, token: token }, callback);
}

/*
*	Fonction qui renvoie true si l'utilisateur existe en base de données
*/
function userExist(email, callback) {
	db.getData("users", {email : email}, callback);
}

/*
*	Fonction qui renvoie l'utilisateur en base de données s'il existe
*/
function getUser(email, password, salt , callback) {
	password = passwordHash(password, salt);
	db.getData("users", {email : email, password: password, salt : salt}, callback);
}

/*
*	Fonction qui renvoie l'utilisateur associé à l'id
*/
function getUserById(id , callback) {
	db.getData("users", {_id : new MongoObjectID(id)}, callback);
}

/*
*	Fonction qui renvoie les messages à un utilisateurs
*	Si lastMessageId = -1, alors on renvoie tout
*	Sinon on renvoie les messages après lastMessageId
*/
function getMessage(userId, lastMessageId, callback) {
	var oid = new MongoObjectID(lastMessageId);
	db.getData("messages", {user_id: userId, _id: {$gt: oid}}, callback);
}

/*
*	Fonction qui enregistre un message
*/
function getAllMessage(userId, callback) {
	console.log("USERID : " + userId);
	db.getData("messages", {user_id: userId}, callback);
}

/*
*	Fonction qui enregistre un message
*/
function createMessage(userId, message, isAslan, callback) {
	db.insertData("messages", { user_id: userId, message: message, date: new Date(), isAslan: isAslan }, callback);
}

/*
*	Hash le mot de passe avec sha512
*/
function passwordHash(password, salt) {
	var hash = crypto.createHmac('sha512', salt); /** Hashing algorithm sha512 */
    hash.update(password);
    
    return hash.digest('hex');
}

/**
 * generates random string of length characters i.e salt
 */
function genRandomString(length) {
    return crypto.randomBytes(Math.ceil(length/2))
            .toString('hex') /** convert to hexadecimal format */
            .slice(0,length);   /** return required number of characters */
};

exports.createUser = createUser;
exports.getUser = getUser;
exports.userExist = userExist;
exports.getUserById = getUserById;
exports.getMessage = getMessage;
exports.getAllMessage = getAllMessage;
exports.createMessage = createMessage;