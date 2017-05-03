'use strict';

const core = require('./core.js');
const fd = require('./field.js');
const serv = require('./../services/services.js');
const servWeather = require('./../services/weatherService.js');
const db = require('./../AslanDBConnector.js');

/*
*   Fonction qui traite les réponses de type Bonjour
*/
function processingLocation(intent, response, location) {
	if(response != undefined && response.result != undefined && response.result.parameters != undefined) {
		var fields = fd.extractFields(response.result.fulfillment.speech);
		if(fields.indexOf("{\"location\":[]}") != -1) {
			response.result.fulfillment.speech = fd.removeFields(response.result.fulfillment.speech);
			db.insertData("conversation", {sessionId: response.sessionId, metadata: response.result.metadata, fulfillment: response.result.fulfillment}, function(err, data) {
				console.log(err);
			});
			core.askLocation();
		} else {
			switch(intent) {
			case "hotel" :
				processingHotel(response, location);
				break;
	        case "lieu-culte" :
	        	processingLieuCulte(response, location);
	        	break;
	        case "patrimoine-culturel" :
	        	processingPatrimoineCulturel(response, location);
	        	break;
	        case "piscine" :
	        	processingPiscine(response, location);
	        	break;
	        case "toilette" :
	        	processingToilette(response, location);
	        	break;
	        case "velov" :
	        	processingVelov(response, location);
	        	break;
	        case "fontaine":
	        	processingFountain(response, location);
	        	break;
	        case "restaurant":
	        	processingRestaurant(response, location);
	        	break;
			}
		}
	} else {
		core.prepareMessage(response.result.fulfillment.speech);
	}
}


/*
*   Fonction qui traite les réponses de type Bonjour
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


/*
*   Fonction qui traite les réponses de type Heure
*/
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


/*
*   Fonction qui traite les réponses de type Habitant
*/
function processingCitizen(response) {
	core.prepareMessage(response.result.fulfillment.speech);
}


/*
*   Fonction qui traite les réponses de type Date
*/
function processingDate(response) {
	core.prepareMessage(response.result.fulfillment.speech);
}


/*
*   Fonction qui traite les réponses de type Méteo
*/
function processingWeather(message) {
	if(message != undefined && message.result != undefined && message.result.parameters != undefined) {
		var fields = fd.extractFields(message.result.fulfillment.speech);
		console.log(fields);
		if(fields.indexOf("{\"location\":[]}") != -1) {
			message.result.fulfillment.speech = fd.removeFields(message.result.fulfillment.speech);
			db.insertData("conversation", {sessionId: message.sessionId, metadata: message.result.metadata, fulfillment: message.result.fulfillment}, function(err, data) {
				console.log(err);
			});
			core.askLocation();
		} else if(fields.indexOf("{meteo}") != -1) {
			var coord = message.result.parameters["geo-city"];
			var date = message.result.parameters["date"];
			if(date != undefined) {
				date = formattedDate();
			}
			servWeather.JSONP_LocalWeather(coord, date, function(response) {
				if(response.data != null && response.data.current_condition != null) {
					console.log(response.data);
					var weather = "";
					if(response.data.current_condition['0'].lang_fr != null)
						weather = response.data.current_condition['0'].lang_fr['0'].value + ", ";
					weather +=  response.data.current_condition['0'].temp_C + "°";
					var answer = fd.replaceField(message.result.fulfillment.speech, "{meteo}", weather);
					core.prepareMessage(answer);
				}
			});	
		} else {
			core.prepareMessage(message.result.fulfillment.speech);
		}
	} else {
		core.prepareMessage(message.result.fulfillment.speech);
	}
}


/*
*   Fonction qui traite les réponses de type Fontaine
*/
function processingFountain(response, location) {
	if(response != undefined && response.result != undefined && response.result.parameters != undefined) {
		var fields = fd.extractFields(response.result.fulfillment.speech);
		if(fields.indexOf("{fontaines}") != -1 && location != null){
			serv.nearestRestaurantsWithKeywords(location, keyword, function(result) {
				var data = [];
				for(var i=0;i<result.length;i++) {
					var d = {
						title: "Fontaine d'eau potable",
                		image_url: "",
                		subtitle: "À " + result[0].dist.toFixed(2) + "Km environ",
                		url:result[i].trajet_url,
                		button_url:result[i].trajet_url,
                		button_title:"Y Aller"
					}
					data.push(d);
            	}
				response.result.fulfillment.speech = fd.removeFields(response.result.fulfillment.speech);
				core.prepareMessage({text: response.result.fulfillment.speech, data: data});
			});
		} else {
			core.prepareMessage(response.result.fulfillment.speech);
		}
	} else {
		core.prepareMessage(response.result.fulfillment.speech);
	}
}


/*
*   Fonction qui traite les réponses de type Restaurant
*/
function processingRestaurant(response, location) {
	if(response != undefined && response.result != undefined && response.result.parameters != undefined) {
		var fields = fd.extractFields(response.result.fulfillment.speech);
		if(fields.indexOf("{restaurants}") != -1 && location != null){
			var keyword = [];
			if(response.result.parameters["type-restaurant"] != undefined) {
				keyword.push(response.result.parameters["type-restaurant"]);
			}
			serv.nearestRestaurantsWithKeywords(location, keyword, function(result) {
				var data = [];
				for(var i=0;i<result.length;i++) {
					var d = {
						title: result[i].name,
                		image_url: result[i].photo_url,
                		subtitle: result[i].vicinity + " - " + result[i].rating + "/5",
                		url:result[i].trajet_url,
                		button_url:result[i].trajet_url,
                		button_title:"Y Aller"
					}
					data.push(d);
            	}
				response.result.fulfillment.speech = fd.removeFields(response.result.fulfillment.speech);
				core.prepareMessage({text: response.result.fulfillment.speech, data: data});
			});
		} else {
			core.prepareMessage(response.result.fulfillment.speech);
		}
	} else {
		core.prepareMessage(response.result.fulfillment.speech);
	}
}

/*
*   Fonction qui traite les réponses de type Velov
*/
function processingVelov(response, location) {

}


/*
*   Fonction qui traite les réponses de type Piscine
*/
function processingPiscine(response, location) {

}


/*
*   Fonction qui traite les réponses de type Hotel
*/
function processingHotel(response, location) {

}


/*
*   Fonction qui traite les réponses de type Patrimoine culturel
*/
function processingPatrimoineCulturel(response, location) {

}


/*
*   Fonction qui traite les réponses de type Toilette
*/
function processingToilette(response, location) {

}


/*
*   Fonction qui traite les réponses de type Lieu Culte
*/
function processingLieuCulte(response, location) {

}


function formattedDate(d = new Date) {
  let month = String(d.getMonth() + 1);
  let day = String(d.getDate());
  const year = String(d.getFullYear());

  if (month.length < 2) month = '0' + month;
  if (day.length < 2) day = '0' + day;

  return `${year}-${month}-${day}`;
}


exports.processingGrettings = processingGrettings;
exports.processingHour = processingHour;
exports.processingFountain = processingFountain;
exports.processingCitizen = processingCitizen;
exports.processingDate = processingDate;
exports.processingWeather = processingWeather;
exports.processingPiscine = processingPiscine;
exports.processingVelov = processingVelov;
exports.processingLieuCulte = processingLieuCulte;
exports.processingHotel = processingHotel;
exports.processingPatrimoineCulturel = processingPatrimoineCulturel;
exports.processingToilette = processingToilette;
exports.processingRestaurant = processingRestaurant;