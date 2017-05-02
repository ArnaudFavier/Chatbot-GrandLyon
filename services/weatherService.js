'use strict';

var request = require("request");

var _PremiumApiBaseURL = 'https://api.worldweatheronline.com/premium/v1/';
var _PremiumApiKey = "0534869c3fd947dd85f70926170205";//process.env.WEATHER_API_KEY;

// -------------------------------------------
const format = "json";
const language = "fr"

// Le format de la date doit etre YYYY-MM-DD, genre 2017-05-02
function JSONP_LocalWeather(city, date, callback) {
    var url = _PremiumApiBaseURL + 'weather.ashx?q=' + city +
    		 '&format=' + format +
    		 '&date=' + date + '&key=' + _PremiumApiKey + '&lang=' + language;
    	callAPI(url, callback);
}

function callAPI(url,callback) {
	request({
	  uri: url,
	  method: "GET",
	  headers: { 'Content-Type': 'application/json' }
	}, function(error, response, body) {
	  	try {
            body = JSON.parse(body);
            console.log(body);
            callback(body);
        } catch (err){
        	console.log("ERROR");
        	callback(body);
        }
	});
}

/*JSONP_LocalWeather("Lyon", "2017-05-02", function(response) {
	console.log(response.data);
});*/
exports.JSONP_LocalWeather = JSONP_LocalWeather;
/*
 info à récupérer :
	la température 		-> data.current_condition['0'].lang_fr
	le temps 			-> data.current_condition['0'].temp_C
	un icone de temps 	-> data.current_condition['0'].weatherIconUrl['0'].value
*/
