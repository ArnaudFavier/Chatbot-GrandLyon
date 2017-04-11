/*
*	index.js
*	Point d'entrée de l'application
*	version 0.1.1
*	Tiré du tuto http://www.supinfo.com/articles/single/3246-realisez-bot-facebook-messenger-nodejs
*	https://developers.facebook.com/docs/messenger-platform/guides/quick-start/
*/


'use strict';
// Import Wit.Ai
require('./botlogic/botlogic.js');
const bodyParser = require('body-parser');
const config = require('config');
const express = require('express');
const http = require('http');
const request = require('request');

var app = express();

app.set('port', process.env.PORT || 5555);
app.use(bodyParser.json());

const VALIDATION_TOKEN = "58eb669e-9a3c-4940-ba2a-45833ed28ff1";
const PAGE_ACCESS_TOKEN = "EAAbiTcER2bkBAConk7qPEvqaIRT0MucHZBCDVxZBqZB14qUofVZCnryAbWxXn9atzofmG5jm98y1iZB5RIMxagxCL6ar0QGS8JzqHEoGTlX9vYTiHMfyyUwFDz7ORZCsSzjzQZCsOOyvCGdgTJva9RvHMNmAP0H7eQ4C2eTmOohGAZDZD";

app.post('/', (req, res) => {
  const conversation = req.body.message.conversation
  const messages = [{
    type: 'text',
    content: 'my first message',
  }]

  request.post(`https://api.recast.ai/connect/v1/conversations/${conversation}/messages`)
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