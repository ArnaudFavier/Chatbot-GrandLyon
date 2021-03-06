'use strict';

const recastai = require('recastai')
const TOKEN = process.env.RECAST_TOKEN;
const request = new recastai.request(TOKEN);

function sendMessage(text, callback)  {
	request.converseText(text).then(function(res) {
		callback(res);
   	}).catch(function(err) {
    	console.err("Error received : ", err);
   	})
}

exports.sendMessage = sendMessage;