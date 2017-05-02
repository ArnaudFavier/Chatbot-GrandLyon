'use strict';

var dateFormat = require('dateformat');
var fs = require("fs");

var googleMapsClient = require('@google/maps').createClient({
    key: process.env.GOOGLE_API_KEY
});

function convertDateToUTC(date) {
    return new Date(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds());
}

function getTimeAt(city, callback) {
    // Geocode an address.
    googleMapsClient.geocode({
        address: city
    }, function (err, response) {
        if (!err) {
            var coordinates = response.json.results[0].geometry.location;

            console.log("" + coordinates.lat + "," + coordinates.lng);
            // Timezone the geocode
            googleMapsClient.timezone({
                location: [coordinates.lat, coordinates.lng],
            }, function (err2, response2) {
                if (!err2) {
                    var offsetInSecond = response2.json.rawOffset + response2.json.dstOffset;
                    var timestamp = Date.now() + offsetInSecond * 1000;
                    var date = convertDateToUTC(new Date(timestamp));
                    var dateString = dateFormat(date, 'HH:MM');
                    dateString = dateString.replace(':', 'h');
                    callback(dateString);
                } else {
                    callback(null);
                }
            });
        } else {
            callback(null);
        }
    });
}

const weekday = ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
const month = ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Aout', 'Septembre', 'Octobre', 'Novembre', 'Decembre'];

function getDate(callback) {
    var timestamp = Date.now() + 1000 * 3600 * 2;
    var date = convertDateToUTC(new Date(timestamp));
    var dateString = weekday[date.getDay()] + " " + date.getDate() + " " + month[date.getMonth()] + " " + date.getFullYear();
    callback(dateString);
}

function compareRestaurantDist(rest1, rest2) {
    return rest1.dist - rest2.dist;
}

function getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2) {
    var R = 6371; // Radius of the earth in km
    var dLat = deg2rad(lat2 - lat1);  // deg2rad below
    var dLon = deg2rad(lon2 - lon1);
    var a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2)
        ;
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    var d = R * c; // Distance in km
    return d;
}

function deg2rad(deg) {
    return deg * (Math.PI / 180)
}

function nearestPointCulturel(coordinates, count, callback) {
    var restaurants = new Array(3000);

    const PATRIMOINE_CULTUREL = 'PATRIMOINE_CULTUREL';
    var url = 'https://download.data.grandlyon.com/wfs/rdata?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&request=GetFeature&typename=sit_sitra.sittourisme&SRSNAME=urn:ogc:def:crs:EPSG::4171';

    var avant = Date.now();

    const request = require('request');

    request(url, (error, response, body) => {
        if (!error && response.statusCode === 200) {
            const data = JSON.parse(body);
            var elem;
            var restId = 0;
            for (let i = 0; i < data.features.length; ++i) {
                elem = data.features[i];
                // Si le lieu est un restaurant
                if (elem.properties.type == PATRIMOINE_CULTUREL) {
                    var restCoord = elem.geometry.coordinates;
                    elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.lon, restCoord[1], restCoord[0]);

                    restaurants[restId] = elem;
                    ++restId;
                }
            }

            restaurants.sort(compareRestaurantDist);

            restaurants = restaurants.slice(0, count);

            var apres = Date.now();
            console.log('temps ecoule : ' + (apres - avant) + 'ms');

            callback(restaurants);
        } else {
            console.log("Got an error: ", error, ", status code: ", response.statusCode)

            callback(null);
        }
    });
}

function nearestRestaurantsWithKeywords(coordinates, keywords, callback) {
    'use strict';

    var restaurants = new Array(3000);

    let keywordsFormat = '';
    if (keywords.length !== 0) {
        keywordsFormat += '(' + keywords[0] + ')';
    }
    for (let i = 1; i < keywords.length; ++i) {
        keywordsFormat += ' AND (' + keywords[i] + ')';
    }

    var url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=' + coordinates.lat + ',' + coordinates.lon + '&radius=2000&rankBy=distance&type=restaurant&keyword=' + keywordsFormat + '&key=' + process.env.GOOGLE_PLACE_API_KEY;

    var avant = Date.now();

    const request = require('request');

    request(url, (error, response, body) => {
        if (!error && response.statusCode === 200) {
            const data = JSON.parse(body).results;
            var elem;
            var restId = 0;

            for (let restaurant of data) {
                restaurant.details_url = 'https://maps.googleapis.com/maps/api/place/details/json?key=' + process.env.GOOGLE_PLACE_API_KEY + '&placeid=' + restaurant.place_id;
                if(typeof restaurant.photos === 'object' && restaurant.length !== 0){
                    restaurant.photo_url = 'https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference='+restaurant.photos[0].photo_reference+'&key='+process.env.GOOGLE_PLACE_API_KEY;
                }
            }

            callback(data);
        } else {
            console.log("Got an error: ", error, ", status code: ", response.statusCode)

            callback(null);
        }
    });
}

function nearestFontaines(coordinates, count, callback) {
    var fontaines = new Array(3000);

    var contents = fs.readFileSync("./data/donnees_filtrees/fontaines_filtered.json");

    var avant = Date.now();

    const data = JSON.parse(contents);
    var elem;
    var fontId = 0;
    for (let i = 0; i < data.features.length; ++i) {
        elem = data.features[i];

        var fontCoord = elem.geometry.coordinates;
        elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.lon, fontCoord[0], fontCoord[1]);

        fontaines[fontId] = elem;
        ++fontId;
    }

    fontaines.sort(compareRestaurantDist);

    fontaines = fontaines.slice(0, count);

    var apres = Date.now();
    console.log('temps ecoule : ' + (apres - avant) + 'ms');

    callback(fontaines);
}

function nearestPiscines(coordinates, count, callback) {
    var piscines = new Array(3000);

    var contents = fs.readFileSync("./data/donnees_filtrees/piscines_filtered.json");

    var avant = Date.now();

    const data = JSON.parse(contents);
    var elem;
    var piscId = 0;
    for (let i = 0; i < data.features.length; ++i) {
        elem = data.features[i];

        var piscCoord = elem.geometry.coordinates;
        elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.lon, piscCoord[0], piscCoord[1]);

        piscines[piscId] = elem;
        ++piscId;
    }

    piscines.sort(compareRestaurantDist);

    piscines = piscines.slice(0, count);

    var apres = Date.now();
    console.log('temps ecoule : ' + (apres - avant) + 'ms');

    callback(piscines);
}

function nearestVelov(coordinates, count, callback) {
    'use strict';

    var restaurants = new Array(3000);

    var url = 'https://download.data.grandlyon.com/wfs/rdata?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&request=GetFeature&typename=jcd_jcdecaux.jcdvelov&SRSNAME=urn:ogc:def:crs:EPSG::4171'

    var avant = Date.now();

    const request = require('request');

    request(url, (error, response, body) => {
        if (!error && response.statusCode === 200) {
            const data = JSON.parse(body);
            var elem;
            var restId = 0;
            for (let i = 0; i < data.features.length; ++i) {
                elem = data.features[i];

                var restCoord = elem.geometry.coordinates;
                elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.lon, restCoord[1], restCoord[0]);

                restaurants[restId] = elem;
                ++restId;
            }

            restaurants.sort(compareRestaurantDist);

            restaurants = restaurants.slice(0, count);

            var apres = Date.now();
            console.log('temps ecoule : ' + (apres - avant) + 'ms');

            callback(restaurants);
        } else {
            console.log("Got an error: ", error, ", status code: ", response.statusCode)

            callback(null);
        }
    });
}

function nearestLieuCulte(coordinates, count, callback) {
    'use strict';

    var restaurants = new Array(3000);

    var url = 'https://download.data.grandlyon.com/wfs/grandlyon?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&request=GetFeature&typename=adr_voie_lieu.adrlieuculte&SRSNAME=urn:ogc:def:crs:EPSG::4171'

    var avant = Date.now();

    const request = require('request');

    request(url, (error, response, body) => {
        if (!error && response.statusCode === 200) {
            const data = JSON.parse(body);
            var elem;
            var restId = 0;
            for (let i = 0; i < data.features.length; ++i) {
                elem = data.features[i];

                var restCoord = elem.geometry.coordinates[0][0];
                elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.lon, restCoord[1], restCoord[0]);

                restaurants[restId] = elem;
                ++restId;
            }

            restaurants.sort(compareRestaurantDist);

            restaurants = restaurants.slice(0, count);

            var apres = Date.now();
            console.log('temps ecoule : ' + (apres - avant) + 'ms');

            callback(restaurants);
        } else {
            console.log("Got an error: ", error, ", status code: ", response.statusCode)

            callback(null);
        }
    });
}

function nearestLieuCulteType(coordinates, count, callback, type) {
    'use strict';

    var restaurants = new Array(3000);

    var url = 'https://download.data.grandlyon.com/wfs/grandlyon?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&request=GetFeature&typename=adr_voie_lieu.adrlieuculte&SRSNAME=urn:ogc:def:crs:EPSG::4171'

    var avant = Date.now();

    const request = require('request');
    const typeLower = type.toLowerCase();

    request(url, (error, response, body) => {
        if (!error && response.statusCode === 200) {
            const data = JSON.parse(body);
            var elem;
            var restId = 0;
            for (let i = 0; i < data.features.length; ++i) {
                elem = data.features[i];

                // Si le lieu est un restaurant
                if (elem.properties.nom.toLowerCase().indexOf(typeLower) !== -1) {
                    var restCoord = elem.geometry.coordinates[0][0];
                    elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.lon, restCoord[1], restCoord[0]);

                    restaurants[restId] = elem;
                    ++restId;
                }
            }

            restaurants.sort(compareRestaurantDist);

            restaurants = restaurants.slice(0, count);

            var apres = Date.now();
            console.log('temps ecoule : ' + (apres - avant) + 'ms');

            callback(restaurants);
        } else {
            console.log("Got an error: ", error, ", status code: ", response.statusCode)

            callback(null);
        }
    });
}

exports.getTimeAt = getTimeAt;
exports.getDate = getDate;
exports.nearestPointCulturel = nearestPointCulturel;
exports.nearestRestaurantsWithKeywords = nearestRestaurantsWithKeywords;
exports.nearestFontaines = nearestFontaines;
exports.nearestPiscines = nearestPiscines;
exports.nearestVelov = nearestVelov;
exports.nearestLieuCulte = nearestLieuCulte;
exports.nearestLieuCulteType = nearestLieuCulteType;