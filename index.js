/*
*	index.js
*	Point d'entrée de l'application
*	version 0.1.1
*	Tiré du tuto http://www.supinfo.com/articles/single/3246-realisez-bot-facebook-messenger-nodejs
*/


'use strict';

const bodyParser = require('body-parser');
const config = require('config');
const express = require('express');
const http = require('http');
const request = require('request');

var app = express();

app.set('port', process.env.PORT || 5555);
app.use(bodyParser.json());

const VALIDATION_TOKEN = "58eb669e-9a3c-4940-ba2a-45833ed28ff1";

/*
*	URL pour Facebook
*	Facebook check si on est bien le serveur associé au Bot
*	On renvoie 200 et le challenge (code donné par Facebook)
*/
app.get('/webhook', function(req, res) {
  if (req.query['hub.mode'] === 'subscribe' &&
      req.query['hub.verify_token'] === VALIDATION_TOKEN) {
    console.log("Validating webhook");
    res.status(200).send(req.query['hub.challenge']);
  } else {
    console.error("Failed validation. Make sure the validation tokens match.");
    res.sendStatus(403);
  }
});

/*
*	URL que Facebook utilise pour nous envoyer un message
*/
app.post('/webhook/', function (req, res) {
    let message_events = req.body.entry[0].messaging
    for (message_event of message_events) {
        let sender = message_event.sender.id
        if (message_event.message && message_event.message.text) {
            let text = message_event.message.text
            sendTextMessage(sender, "J'ai recu : " + text.substring(0, 200))
        }
    }
    res.sendStatus(200)
})

app.get('/initdb', function(req, res) {
	/*const pg = require('pg');
	const connectionString = process.env.DATABASE_URL || 'postgres://localhost:5432/todo';

	const client = new pg.Client(connectionString);
	client.connect();
	const query = client.query(
  	'CREATE TABLE items(id SERIAL PRIMARY KEY, text VARCHAR(40) not null, complete BOOLEAN)');
	query.on('end', () => { client.end(); });*/
	res.status(404)
});

app.listen(app.get('port'), function() {
  console.log('Bot is running on port ', app.get('port'));
});

/*
*	Fonction qui permet d'envoyer un message
*/
function sendTextMessage(sender, text) {
    let data = { text:text }
    let access_token = "mon token de page";
    request({
        url: 'https://graph.facebook.com/v2.6/me/messages',
        qs: {access_token: access_token},
        method: 'POST',
        json: {
            recipient: {id:sender},
            message: data,
        }
    }, function(error, response, body) {
        if (error) {
            console.log('Error sending messages: ', error)
        } else if (response.body.error) {
            console.log('Error: ', response.body.error)
        }
    })
}