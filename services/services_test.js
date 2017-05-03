///// Ne pas oublier le nmp install /////

// Environment file
var env = require('node-env-file');
env(__dirname + '/../.env');

// Import Services
var Services = require('./services.js');

// Callback example
function callback(result) {
    if (typeof result === 'undefined' || result === null) {
        console.log('Error');
    }
    else {
        console.log(result);

        // A decommenter pour tester le chargement des details d'un restaurant
/*
        result[0].loadDetails(function (){
            console.log(JSON.stringify(result[0]));
        });
*/
    }
}

// Appel de test pour obtenir l'heure dans une ville (à décommenter pour tester)
// Services.getTimeAt("Paris", callback);

// Appel de test pour otenir la date (à décommenter pour tester)
// Services.getDate(callback);

// Appel de test pour obtenir les 50 restaurants les plus proches d'une position,
// en passant les coordonnées de Place Bellecour (à décommenter pour tester)
// Services.nearestPointCulturel({lat: 45.756715, lon: 4.831561}, 50, callback);

// Appel de test pour obtenir les 5 stations velov les plus proches d'une position,
// en passant les coordonnées de Place Bellecour (à décommenter pour tester)
// Services.nearestVelov({lat: 45.756715, lon: 4.831561}, 5, callback);

// Appel de test pour obtenir les restaurants les plus proches d'une position,
// en passant les coordonnées de Place Bellecour (à décommenter pour tester)
// Services.nearestRestaurantsWithKeywords({lat: 45.756715, lon: 4.831561}, ['sushi','asiatique'], callback);

// Appel de test pour obtenir les 3 fontaines les plus proches d'une position,
// en passant les coordonnées de Place Bellecour (à décommenter pour tester)
// Services.nearestFontaines({lat: 45.756715, lon: 4.831561}, 3, callback);

// Appel de test pour obtenir les 3 piscines les plus proches d'une position,
// en passant les coordonnées de Place Bellecour (à décommenter pour tester)
// Services.nearestPiscines({lat: 45.756715, lon: 4.831561}, 3, callback);

// Appel de test pour obtenir les 5 lieux de culte les plus proches d'une position,
// en passant les coordonnées de Place Bellecour (à décommenter pour tester)
// Services.nearestLieuCulte({lat: 45.756715, lon: 4.831561}, 100, callback);

// Appel de test pour obtenir les 5 lieux de culte d'un type donné, les plus proches d'une position,
// en passant les coordonnées de Place Bellecour (à décommenter pour tester)
// Services.nearestLieuCulteType({lat: 45.756715, lon: 4.831561}, 5, callback, 'Synagogue');

// Appel de test pour obtenir les 5 hotels les plus proches d'une position,
// en passant les coordonnées de Place Bellecour (à décommenter pour tester)
// Services.nearestHotels({lat: 45.756715, lon: 4.831561}, 5, callback);

// Appel de test pour obtenir le nom d'une ville en fonction de ses coordonnées,
// en passant les coordonnées de Place Bellecour (à décommenter pour tester)
// Services.nomVillePourCoordonnees({lat:  40.730610, lon: -73.935242}, callback); // New York
// Services.nomVillePourCoordonnees({lat: 45.756715, lon: 4.831561}, callback); // Place Bellecour
Services.nomVillePourCoordonnees({lat:  55.7522200, lon: 37.6155600}, callback); // New York