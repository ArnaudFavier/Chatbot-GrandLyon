'use strict';

const core = require('./core.js');
const fd = require('./field.js');
const service = require('./../services/services.js');

/*
*   Fonction qui traite les réponses de type bonjour
*/
function processingGrettings(response) {
	core.prepareMessage(response);
}

function processingHour(response) {
	if(response != undefined && response.result != undefined && response.result.parameters != undefined 
		&& response.result.parameters.ville != undefined) {
		var fields = fd.extractFields(response.result.fulfillment.speech);
		console.log(fields);
		if(fields.indexOf("{heure}") != -1) {
			console.log("je passe là");
			service.getTimeAt(response.result.parameters.ville, function(hour) {
				console.log(hour);
				response.result.fulfillment.speech = fd.replaceField(response.result.fulfillment.speech, "{heure}",hour);
				response.result.fulfillment.speech = fd.replaceField(response.result.fulfillment.speech, 
					"{\"ville\":[\"" + response.result.parameters.ville + "\"]}", response.result.parameters.ville);
				core.prepareMessage(response.result.fulfillment.speech);
			});	
		}
	} else {
		core.prepareMessage(response.result.fulfillment.speech);
	}
}


exports.processingGrettings = processingGrettings;
exports.processingHour = processingHour;