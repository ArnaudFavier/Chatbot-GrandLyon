'use strict';

var dateFormat = require('dateformat');

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

function nearestRestaurants(coordinates, count, callback) {
    'use strict';

    var restaurants = new Array(3000);

    const RESTAURANT_TYPE = 'RESTAURATION';
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
                if (elem.properties.type == RESTAURANT_TYPE) {
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

exports.getTimeAt = getTimeAt;
exports.nearestRestaurants = nearestRestaurants;