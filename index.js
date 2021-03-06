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
const request = require('request');
const util = require('util');
const fs = require('fs');
const url = require('url');
const facebook = require('./channels/facebook.js');
const telegram = require('./channels/telegram.js');
const aslan = require('./aslan-messenger/aslan.js');
const app = express();

app.set('port', process.env.PORT || 5555);
app.use(bodyParser.json());
app.use(express.static('public')); // To make files in 'public' reachable

/*
 * GET '/'
 */
app.get('/', function(req, res) {
    let pathname = 'public';
    if (url.parse(req.url).pathname === '/') {
        pathname += '/index.html';
    } else {
        pathname += url.parse(req.url).pathname;
    }

    fs.readFile(pathname, function (err, data) {
        if (err) {
            console.log(err);
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
 * URL pour Aslan Messenger
 * URL utilisé pour connecter un utilisateur
 */
app.post('/aslan-messenger/signin', function(req, res) {
    aslan.signIn(req, res);
});

/*
 * URL pour Aslan Messenger
 * URL utilisé pour enregistrer un utilisateur
 */
app.post('/aslan-messenger/register', function(req, res) {
    aslan.register(req, res);
});

/*
 * URL pour Aslan Messenger
 * URL utilisé pour enregistrer un nouveau message
 */
app.post('/aslan-messenger/message', function(req, res) {
    aslan.receive(req, res);
});

/*
 * URL pour Aslan Messenger
 * URL utilisé pour recuperer les messages d'un utilisateur
 */
app.get('/aslan-messenger/message/:token/:user_id/:message_id', function(req, res) {
    aslan.message(req, res);
});

/*
 * URL pour Facebook
 * Facebook check si on est bien le serveur associé au Bot
 * On renvoie 200 et le challenge (code donné par Facebook)
 */
app.get('/facebook/webhook', function(req, res) {
    if(facebook.webhook(req, res)) {
        console.log("Validating webhook");
        res.status(200).send(req.query['hub.challenge']);
    } else {
        console.error("Failed validation. Make sure the validation tokens match.");
        res.sendStatus(403);
    }
});

/*
 * URL pour Facebook
 * Facebook l'utilise pour nous envoyer un message
 * 20 secondes pour répondre à la requete
 */
app.post('/facebook/webhook', function (req, res) {
    facebook.receivedMessage(req, res);
    res.sendStatus(200);
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
 * Run du serveur
 */
app.listen(app.get('port'), function() {
  console.log('Bot is running on port ', app.get('port'));
});
