'use strict';

const dateFormat = require('dateformat');
const fs = require("fs");

const googleMapsClient = require('@google/maps').createClient({
    key: process.env.GOOGLE_API_KEY
});

function convertDateToUTC(date) {
    return new Date(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds());
}

function nomVillePourCoordonnees(coords, callback) {
    googleMapsClient.reverseGeocode({
        latlng: [coords.lat, coords.lon]
    }, function (err, response) {
        let data = response.json.results[0];
        for (let i = 0; i < data.address_components.length; ++i) {
            let component = data.address_components[i];
            if (component.types.indexOf('locality') !== -1
                || component.types.indexOf('administrative_area_level_1') !== -1) {
                callback(component.long_name);
                return;
            }
        }
        callback('undefined');
    });
}

function getTimeAt(city, callback) {
    // Geocode an address.
    googleMapsClient.geocode({
        address: city
    }, function (err, response) {
        if (!err) {
            let coordinates = response.json.results[0].geometry.location;
            // Timezone the geocode
            googleMapsClient.timezone({
                location: [coordinates.lat, coordinates.lng],
            }, function (err2, response2) {
                if (!err2) {
                    let offsetInSecond = response2.json.rawOffset + response2.json.dstOffset;
                    let timestamp = Date.now() + offsetInSecond * 1000;
                    let date = convertDateToUTC(new Date(timestamp));
                    let dateString = dateFormat(date, 'HH:MM');
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
    let timestamp = Date.now() + 1000 * 3600 * 2;
    let date = convertDateToUTC(new Date(timestamp));
    let dateString = weekday[date.getDay()] + " " + date.getDate() + " " + month[date.getMonth()] + " " + date.getFullYear();
    callback(dateString);
}

function compareRestaurantDist(rest1, rest2) {
    return rest1.dist - rest2.dist;
}

function getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2) {
    let R = 6371; // Radius of the earth in km
    let dLat = deg2rad(lat2 - lat1);  // deg2rad below
    let dLon = deg2rad(lon2 - lon1);
    let a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2)
    ;
    let c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    let d = R * c; // Distance in km
    return d;
}

function deg2rad(deg) {
    return deg * (Math.PI / 180)
}

function nearestPointCulturel(coordinates, count, callback) {
    let restaurants = new Array(3000);

    const PATRIMOINE_CULTUREL = 'PATRIMOINE_CULTUREL';
    let url = 'https://download.data.grandlyon.com/wfs/rdata?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&request=GetFeature&typename=sit_sitra.sittourisme&SRSNAME=urn:ogc:def:crs:EPSG::4171';

    let avant = Date.now();

    const request = require('request');

    request(url, (error, response, body) => {
        if (!error && response.statusCode === 200) {
            const data = JSON.parse(body);
            let elem;
            let restId = 0;
            for (let i = 0; i < data.features.length; ++i) {
                elem = data.features[i];
                // Si le lieu est un restaurant
                if (elem.properties.type === PATRIMOINE_CULTUREL) {
                    let restCoord = elem.geometry.coordinates;
                    elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.long, restCoord[1], restCoord[0]);

                    restaurants[restId] = elem;
                    ++restId;
                }
            }

            restaurants.sort(compareRestaurantDist);

            restaurants = restaurants.slice(0, count);

            let apres = Date.now();
            console.log('temps ecoule : ' + (apres - avant) + 'ms');

            callback(restaurants);
        } else {
            console.log("Got an error: ", error, ", status code: ", response.statusCode)

            callback(null);
        }
    });
}

function nearestHotels(coordinates, count, callback) {
    let restaurants = new Array(3000);

    const HOTELLERIE = 'HOTELLERIE';
    let url = 'https://download.data.grandlyon.com/wfs/rdata?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&request=GetFeature&typename=sit_sitra.sittourisme&SRSNAME=urn:ogc:def:crs:EPSG::4171';

    let avant = Date.now();

    const request = require('request');

    request(url, (error, response, body) => {
        if (!error && response.statusCode === 200) {
            const data = JSON.parse(body);
            let elem;
            let restId = 0;
            for (let i = 0; i < data.features.length; ++i) {
                elem = data.features[i];
                // Si le lieu est un restaurant
                if (elem.properties.type === HOTELLERIE) {
                    let restCoord = elem.geometry.coordinates;
                    elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.long, restCoord[1], restCoord[0]);

                    restaurants[restId] = elem;
                    ++restId;
                }
            }

            restaurants.sort(compareRestaurantDist);

            restaurants = restaurants.slice(0, count);

            let apres = Date.now();
            console.log('temps ecoule : ' + (apres - avant) + 'ms');

            callback(restaurants);
        } else {
            console.log("Got an error: ", error, ", status code: ", response.statusCode);

            callback(null);
        }
    });
}

function nearestRestaurantsWithKeywords(coordinates, keywords, callback) {
    let keywordsFormat = '';
    if (keywords.length !== 0) {
        keywordsFormat += '(' + keywords[0] + ')';
    }
    for (let i = 1; i < keywords.length; ++i) {
        keywordsFormat += ' AND (' + keywords[i] + ')';
    }

    let url;
    if (keywords.length !== 0) {
        url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=' + coordinates.lat + ',' + coordinates.long + '&rankby=distance&type=restaurant&keyword=' + keywordsFormat + '&key=' + process.env.GOOGLE_PLACE_API_KEY;
    } else {
        url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=' + coordinates.lat + ',' + coordinates.long + '&radius=2000&type=restaurant&keyword=' + keywordsFormat + '&key=' + process.env.GOOGLE_PLACE_API_KEY;
    }

    const request = require('request');

    request(url, (error, response, body) => {
        if (!error && response.statusCode === 200) {
            let data = JSON.parse(body).results;

            const dataFiltered = [];
            for (let restaurant of data) {
                if(restaurant.types.indexOf('lodging') === -1){
                    dataFiltered.push(restaurant);
                }
            }
            data = dataFiltered;

            for (let restaurant of data) {
                restaurant.details_url = 'https://maps.googleapis.com/maps/api/place/details/json?key=' + process.env.GOOGLE_PLACE_API_KEY + '&placeid=' + restaurant.place_id;
                if (typeof restaurant.photos === 'object' && restaurant.length !== 0) {
                    restaurant.photo_url = 'https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=' + restaurant.photos[0].photo_reference + '&key=' + process.env.GOOGLE_PLACE_API_KEY;
                }

                restaurant.trajet_url = 'https://maps.google.com?saddr=' + coordinates.lat + ',' + coordinates.long + '&daddr=' + encodeURI(restaurant.vicinity);

                restaurant.loadDetails = function (callback) {
                    request(this.details_url, (error2, response2, body2) => {
                        if (!error2 && response2.statusCode === 200) {
                            restaurant.details = JSON.parse(body2).result;
                            callback(restaurant);
                        } else {
                            console.log("Got an error: ", error2, ", status code: ", response2.statusCode);
                        }
                    });
                }
            }

            callback(data);
        } else {
            console.log("Got an error: ", error, ", status code: ", response.statusCode);

            callback(null);
        }
    });
}

function nearestFontaines(coordinates, count, callback) {
    let fontaines = new Array(3000);

    let contents = fs.readFileSync("./data/donnees_filtrees/fontaines_filtered.json");

    let avant = Date.now();

    const data = JSON.parse(contents);
    let elem;
    let fontId = 0;
    for (let i = 0; i < data.features.length; ++i) {
        elem = data.features[i];

        let fontCoord = elem.geometry.coordinates;
        elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.long, fontCoord[0], fontCoord[1]);
        elem.trajet_url = 'https://maps.google.com?saddr=' + coordinates.lat + ',' + coordinates.long + '&daddr='  + fontCoord[0] + ',' + fontCoord[1];

        fontaines[fontId] = elem;
        ++fontId;
    }

    fontaines.sort(compareRestaurantDist);

    fontaines = fontaines.slice(0, count);

    let apres = Date.now();
    console.log('temps ecoule : ' + (apres - avant) + 'ms');

    callback(fontaines);
}

function nearestPiscines(coordinates, count, callback) {
    let piscines = new Array(3000);

    let contents = fs.readFileSync("./data/donnees_filtrees/piscines_filtered.json");

    let avant = Date.now();

    const data = JSON.parse(contents);
    let elem;
    let piscId = 0;
    for (let i = 0; i < data.features.length; ++i) {
        elem = data.features[i];

        let piscCoord = elem.geometry.coordinates;
        elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.long, piscCoord[0], piscCoord[1]);
        elem.trajet_url = 'https://maps.google.com?saddr=' + coordinates.lat + ',' + coordinates.long + '&daddr='  + piscCoord[0] + ',' + piscCoord[1];

        piscines[piscId] = elem;
        ++piscId;
    }

    piscines.sort(compareRestaurantDist);

    piscines = piscines.slice(0, count);

    let apres = Date.now();
    console.log('temps ecoule : ' + (apres - avant) + 'ms');

    callback(piscines);
}

function nearestVelov(coordinates, count, callback) {

    let restaurants = new Array(3000);

    let url = 'https://download.data.grandlyon.com/wfs/rdata?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&request=GetFeature&typename=jcd_jcdecaux.jcdvelov&SRSNAME=urn:ogc:def:crs:EPSG::4171';

    let avant = Date.now();

    const request = require('request');

    request(url, (error, response, body) => {
        if (!error && response.statusCode === 200) {
            const data = JSON.parse(body);
            let elem;
            let restId = 0;
            for (let i = 0; i < data.features.length; ++i) {
                elem = data.features[i];

                let restCoord = elem.geometry.coordinates;
                elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.long, restCoord[1], restCoord[0]);

                restaurants[restId] = elem;
                ++restId;
            }

            restaurants.sort(compareRestaurantDist);

            restaurants = restaurants.slice(0, count);

            let apres = Date.now();
            console.log('temps ecoule : ' + (apres - avant) + 'ms');

            callback(restaurants);
        } else {
            console.log("Got an error: ", error, ", status code: ", response.statusCode);

            callback(null);
        }
    });
}

function nearestLieuCulte(coordinates, count, callback) {


    let restaurants = new Array(3000);

    let url = 'https://download.data.grandlyon.com/wfs/grandlyon?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&request=GetFeature&typename=adr_voie_lieu.adrlieuculte&SRSNAME=urn:ogc:def:crs:EPSG::4171';

    let avant = Date.now();

    const request = require('request');

    request(url, (error, response, body) => {
        if (!error && response.statusCode === 200) {
            const data = JSON.parse(body);
            let elem;
            let restId = 0;
            for (let i = 0; i < data.features.length; ++i) {
                elem = data.features[i];

                let restCoord = elem.geometry.coordinates[0][0];
                elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.long, restCoord[1], restCoord[0]);
                elem.trajet_url = 'https://maps.google.com?saddr=' + coordinates.lat + ',' + coordinates.long + '&daddr='  + restCoord[1] + ',' + restCoord[0];

                restaurants[restId] = elem;
                ++restId;
            }

            restaurants.sort(compareRestaurantDist);

            restaurants = restaurants.slice(0, count);

            let apres = Date.now();
            console.log('temps ecoule : ' + (apres - avant) + 'ms');

            callback(restaurants);
        } else {
            console.log("Got an error: ", error, ", status code: ", response.statusCode);

            callback(null);
        }
    });
}

function nearestLieuCulteType(coordinates, count, callback, type) {


    let restaurants = new Array(3000);

    let url = 'https://download.data.grandlyon.com/wfs/grandlyon?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&request=GetFeature&typename=adr_voie_lieu.adrlieuculte&SRSNAME=urn:ogc:def:crs:EPSG::4171';

    let avant = Date.now();

    const request = require('request');
    const typeLower = type.toLowerCase();

    request(url, (error, response, body) => {
        if (!error && response.statusCode === 200) {
            const data = JSON.parse(body);
            let elem;
            let restId = 0;
            for (let i = 0; i < data.features.length; ++i) {
                elem = data.features[i];

                // Si le lieu est un restaurant
                if (elem.properties.nom.toLowerCase().indexOf(typeLower) !== -1) {
                    let restCoord = elem.geometry.coordinates[0][0];
                    elem.dist = getDistanceFromLatLonInKm(coordinates.lat, coordinates.long, restCoord[1], restCoord[0]);
                    elem.trajet_url = 'https://maps.google.com?saddr=' + coordinates.lat + ',' + coordinates.long + '&daddr='  + restCoord[1] + ',' + restCoord[0];

                    restaurants[restId] = elem;
                    ++restId;
                }
            }

            restaurants.sort(compareRestaurantDist);

            restaurants = restaurants.slice(0, count);

            let apres = Date.now();
            console.log('temps ecoule : ' + (apres - avant) + 'ms');

            callback(restaurants);
        } else {
            console.log("Got an error: ", error, ", status code: ", response.statusCode);

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
exports.nearestHotels = nearestHotels;
exports.nomVillePourCoordonnees = nomVillePourCoordonnees;