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
			console.log('Réponse de Wit : ', JSON.stringify(response));
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

function activeDebugMode() {
	interactive(client);
}

function sendMessage(msg, context) {
	client.message(msg, context)
		.then((data) => {
			console.log('Réponse de Wit-Ai (send): ' + JSON.stringify(data));
			return data;
		})
		.catch(console.error);
}

//sendMessage('Quel temps fait-il à Paris ?', {});

/*
console.log("Test de Wit : \"Quel temps fait-il à Saint-Marcel ?\"")
const session = 'my-user-session-42';
const context = {};
client.runActions(session, 'Quel temps fait-il à Saint-Marcel ?', context, (e, context1) => {
	if (e) {
		console.log('Erreur : ' + e);
		return;
	}
});
*/

exports.activeDebugMode = activeDebugMode;
exports.sendMessage = sendMessage;