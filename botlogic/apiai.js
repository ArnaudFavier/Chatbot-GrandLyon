'use strict';

// Environment file 
/*var env = require('node-env-file');
env(__dirname + '/../.env');*/

const apiai = require('apiai');
const TOKEN = process.env.APIAI_TOKEN;
const app = new apiai(TOKEN);

function sendMessage(sessionid, text, callback)  {
	app.textRequest(text, {
    	sessionId: sessionid
	}).on('response', function(response) {
    	callback(response);
	}).on('error', function(error) {
    	console.log(error);
	}).end();
}

exports.sendMessage = sendMessage;