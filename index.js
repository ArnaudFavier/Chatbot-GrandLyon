/*
*	index.js
*	Point d'entrée de l'application
*	version 0.1.1
*	Tiré du tuto http://www.supinfo.com/articles/single/3246-realisez-bot-facebook-messenger-nodejs
*	https://developers.facebook.com/docs/messenger-platform/guides/quick-start/
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
const PAGE_ACCESS_TOKEN = "2a603caa0e4d3181b85e5171cf85b56a";

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
app.post('/webhook', function (req, res) {
	var data = req.body;

  	// Make sure this is a page subscription
  	if (data.object === 'page') {

    // Iterate over each entry - there may be multiple if batched
    data.entry.forEach(function(entry) {
      	var pageID = entry.id;
      	var timeOfEvent = entry.time;

      // Iterate over each messaging event
      	entry.messaging.forEach(function(event) {
        	if (event.message) {
	          receivedMessage(event);
	        } else {
	          console.log("Webhook received unknown event: ", event);
	        }
      	});
    });

    // Assume all went well.
    //
    // You must send back a 200, within 20 seconds, to let us know
    // you've successfully received the callback. Otherwise, the request
    // will time out and we will keep trying to resend.
    res.sendStatus(200);
  }
});

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
function receivedMessage(event) {
	var senderID = event.sender.id;
  	var recipientID = event.recipient.id;
  	var timeOfMessage = event.timestamp;
  	var message = event.message;

  	console.log("Received message for user %d and page %d at %d with message:", 
    	senderID, recipientID, timeOfMessage);
  	console.log(JSON.stringify(message));

  	var messageId = message.mid;

  	var messageText = message.text;
  	var messageAttachments = message.attachments;

  	if (messageText) {

	    // If we receive a text message, check to see if it matches a keyword
	    // and send back the example. Otherwise, just echo the text we received.
	    switch (messageText) {
	      case 'generic':
	        sendGenericMessage(senderID);
	        break;

	      default:
	        sendTextMessage(senderID, messageText);
	    }
  	} else if (messageAttachments) {
    	sendTextMessage(senderID, "Message with attachment received");
  	}
}

function sendTextMessage(recipientId, messageText) {
	var messageData = {
	    recipient: {
	      id: recipientId
	    },
	    message: {
	      text: messageText
	    }
  	};

  	callSendAPI(messageData);
}

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

// Import Wit.Ai
require('./botlogic/botlogic.js');