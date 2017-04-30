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
		if(fields.contain("{heure}")) {
			service.getTimeAt(response.result.parameters.ville, function(hour) {
				response.result.fulfillment.speech = fd.replaceField(response.result.fulfillment.speech, "{heure}",hour);
				core.prepareMessage(response.result.fulfillment.speech);
			});	
		}
	} else {
		core.prepareMessage(response.result.fulfillment.speech);
	}
}


exports.processingGrettings = processingGrettings;
exports.processingHour = processingHour;