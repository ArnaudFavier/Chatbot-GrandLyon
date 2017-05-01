'use strict';

const core = require('./core.js');
const fd = require('./field.js');
const serv = require('./../services/services.js');

/*
*   Fonction qui traite les réponses de type bonjour
*/
function processingGrettings(information, response) {
	if(information != undefined && response != undefined && information.first_name != undefined) {
		var fields = fd.extractFields(response.result.fulfillment.speech);
		console.log(fields);
		if(fields.indexOf("{prenom}") != -1) {
			var answer = fd.replaceField(response.result.fulfillment.speech, "{prenom}", information.first_name);
			core.prepareMessage(answer);
		}
	} else {
		core.prepareMessage(response.result.fulfillment.speech);
	}
}

function processingHour(response) {
	if(response != undefined && response.result != undefined && response.result.parameters != undefined 
		&& response.result.parameters.ville != undefined) {
		var fields = fd.extractFields(response.result.fulfillment.speech);
		console.log(fields);
		if(fields.indexOf("{heure}") != -1) {
			console.log(response.result.parameters.ville);
			serv.getTimeAt(response.result.parameters.ville, function(hour) {
				var answer = fd.replaceField(response.result.fulfillment.speech, "{heure}",hour);
				answer = fd.replaceField(answer, 
					"{\"ville\":[\"" + response.result.parameters.ville + "\"]}", response.result.parameters.ville);
				console.log(answer);
				core.prepareMessage(answer);
			});	
		}
	} else {
		core.prepareMessage(response.result.fulfillment.speech);
	}
}

exports.processingGrettings = processingGrettings;
exports.processingHour = processingHour;