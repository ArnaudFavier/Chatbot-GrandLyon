'use strict';

const core = require('./core.js');
const fd = require('./field.js');
const serv = require('./../services/services.js');
const db = require('./../AslanDBConnector.js');
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
				core.prepareMessage(answer);
			});	
		}
	} else {
		core.prepareMessage(response.result.fulfillment.speech);
	}
}

function processingFountain(response) {
	if(response != undefined && response.result != undefined && response.result.parameters != undefined 
		&& response.result.parameters.location != undefined) {
		serv.nearestFontaines(response.result.parameters.location, 20, function(foutains) {
			message["attachment"] = foutains;
			core.prepareMessage(message);
		});
	} else {
		core.prepareMessage(response.result.fulfillment.speech);
	}
}

function processingCitizen(response) {
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

function processingDate(response) {

}

function processingWeather(response) {
	if(response != undefined && response.result != undefined && response.result.parameters != undefined 
		&& response.result.parameters.ville != undefined) {
		var fields = fd.extractFields(response.result.fulfillment.speech);
		console.log(fields);
		if(fields.indexOf("{meteo}") != -1) {
			serv.getWeatherAt(response.result.parameters.ville, function(weather) {
				var answer = fd.replaceField(response.result.fulfillment.speech, "{meteo}",weather);
				answer = fd.replaceField(answer, 
					"{\"ville\":[\"" + response.result.parameters.ville + "\"]}", response.result.parameters.ville);
				core.prepareMessage(answer);
			});	
		} else {
			core.prepareMessage(response.result.fulfillment.speech);
		}
	} else {
		core.prepareMessage(response.result.fulfillment.speech);
	}
}

function processingRestaurant(response, location) {
	if(response != undefined && response.result != undefined && response.result.parameters != undefined) {
		var fields = fd.extractFields(response.result.fulfillment.speech);
		console.log(fields);
		if(fields.indexOf("{location}") != -1) {
			db.insertData("conversation", object, function(err, data) {
				console.log(err);
			});
			core.prepareMessage(response.result.fulfillment.speech);
		} else if (fields.indexOf("{restaurants}") != -1 && location != null){
			/*serv.getTimeAt(response.result.parameters.ville, function(hour) {
				var answer = fd.replaceField(response.result.fulfillment.speech, "{heure}",hour);
				answer = fd.replaceField(answer, 
					"{\"ville\":[\"" + response.result.parameters.ville + "\"]}", response.result.parameters.ville);
				console.log(answer);
				core.prepareMessage(answer);
			});*/
		} else {
			core.prepareMessage(response.result.fulfillment.speech);
		}
	} else {
		core.prepareMessage(response.result.fulfillment.speech);
	}
}

exports.processingGrettings = processingGrettings;
exports.processingHour = processingHour;