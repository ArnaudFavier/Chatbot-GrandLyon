/*
*   index.js
*   Point d'entrée de l'application
*   version 0.1.1
*   Tiré du tuto http://www.supinfo.com/articles/single/3246-realisez-bot-facebook-messenger-nodejs
*   https://developers.facebook.com/docs/messenger-platform/guides/quick-start/
*/
'use strict';

const bodyParser = require('body-parser');
const config = require('config');
const express = require('express');
const http = require('http');
const request = require('superagent');
var fs = require('fs');

// Import Wit.Ai
require('./botlogic/botlogic.js');

var app = express();

app.set('port', process.env.PORT || 5555);
app.use(bodyParser.json());

/*
 *
 */
app.get('/', function(req, res) {
    fs.readFile('website/index.htm', function (err, data) {
      if (err) {
         // HTTP Status: 404 : NOT FOUND
         res.writeHead(404, {'Content-Type': 'text/html'});
      } else {   
         // HTTP Status: 200 : OK
         res.writeHead(200, {'Content-Type': 'text/html'});    
         res.write(data.toString());       
      }
      res.end();
   });   
});

/*
 *
 */
app.post('/', (req, res) => {
	const conversation = req.body.message.conversation
	console.log(req.body.message)
	const messages = [{
	    type: 'text',
	    content: 'my first message',
  	}]

	request.post('https://api.recast.ai/connect/v1/conversations/${conversation}/messages')
	    .set({ 'Authorization': '52b54f5a6378a44390395f8717402983' })
	    .send({ messages })
	    .end((err, res) => {
	      if (err) {
	        console.log(err)
	      } else {
	        console.log(res)
	      }
    })
})

/*
 *  URL que Facebook utilise pour nous envoyer un message
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

/*
 *
 */
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

/*
 *
 */
app.listen(app.get('port'), function() {
	console.log('Bot is running on port ', app.get('port'));
});
