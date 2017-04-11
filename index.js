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
const util = require('util');
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
	const conversation = req.body.message.conversation;
	console.log("Message: %j", req.body.message);
	const message = req.body.message.conversation.messages[0];
	if (message.attachment.type === 'text') {
    	const messages = [{
		    type: 'text',
		    content: message.attachment.content,
	  	}];
    } else {
    	const messages = [{
		    type: 'text',
		    content: "Not text",
	  	}];
    }

	request.post('https://api.recast.ai/connect/v1/conversations/${conversation}/messages')
	    .set({ 'Authorization': '52b54f5a6378a44390395f8717402983' })
	    .send({ messages })
	    .end((err, res) => {
	      if (err) {
	        console.log(err)
	      } else {
	        console.log(res)
	      }
    });
})


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
