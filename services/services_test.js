///// Ne pas oublier le nmp install /////

// Environment file
var env = require('node-env-file');
env(__dirname + '/../.env');

// Import Services
var Services = require('./services.js');

// Callback example
function callback(result) {
    if (result == undefined || result == null) {
        console.log('Error');
    }
    else {
        console.log(result);
    }
}

// Appel de test pour obtenir l'heure dans une ville (à décommenter pour tester)
// Services.getTimeAt("Paris", callback);

// Appel de test pour obtenir les 10 restaurants les plus proches d'une position,
// en passant les coordonnées de Place Bellecour (à décommenter pour tester)
// Services.nearestRestaurants({lat: 45.756715, lon: 4.831561}, 10, callback);

// Appel de test pour obtenir les 3 fontaines les plus proches d'une position,
// en passant les coordonnées de Place Bellecour
Services.nearestFontaines({lat: 45.756715, lon: 4.831561}, 3, callback);