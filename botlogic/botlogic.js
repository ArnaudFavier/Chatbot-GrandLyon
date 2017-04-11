'use strict';

var Wit = require('node-wit').Wit;
var interactive = require('node-wit').interactive;

const accessToken = 'EFMKINUPTBBVIQXI45UP5TJDCB7ZIATU';

const firstEntityValue = (entities, entity) => {
	const val = entities && entities[entity] &&
		Array.isArray(entities[entity]) &&
		entities[entity].length > 0 &&
		entities[entity][0].value
		;
	if (!val) {
		return null;
	}
	return typeof val === 'object' ? val.value : val;
};

const actions = {
	send(request, response) {
		const { sessionId, context, entities } = request;
		const { text, quickreplies } = response;
		return new Promise(function (resolve, reject) {
			callback(request, response);
			return resolve();
		});
	},
	getForecast({ context, entities }) {
		return new Promise(function (resolve, reject) {
			var location = firstEntityValue(entities, "location")
			if (location) {
				context.forecast = 'beau à ' + location; // we should call a weather API here
				delete context.missingLocation;
			} else {
				context.missingLocation = true;
				delete context.forecast;
			}
			return resolve(context);
		});
	},
};

const client = new Wit({ accessToken, actions });

var callback = function(request, response){
	console.log('Réponse de Wit : ', JSON.stringify(response));
}

function activeDebugMode() {
	interactive(client);
}

function defineCallback(newCallback) {
	callback = newCallback;
}

function sendMessage(msg, userSession, context, callback) {
	 return client.runActions(userSession, msg, context);
}

exports.activeDebugMode = activeDebugMode;
exports.defineCallback = defineCallback;
exports.sendMessage = sendMessage;