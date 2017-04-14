'use strict';

var path = require('path');
var fs = require('fs');
var request = require('request');
var PythonShell = require('python-shell');
var HashMap = require('hashmap');

var showMessage = function(message) {
	console.log(message);
}

var download = function(url, dest, callback) {
    var file = fs.createWriteStream(dest);
    var sendReq = request.get(url);

    // verify response code
    sendReq.on('response', function(response) {
        if (response.statusCode !== 200) {
            showMessage('Response status was ' + response.statusCode);
        }
    });

    // check for request errors
    sendReq.on('error', function (err) {
        fs.unlink(dest);
        showMessage(err.message);
        return;
    });

    sendReq.pipe(file);

    file.on('finish', function() {
        if(callback != null) {
            file.close(callback(dest));  // close() is async, call cb after close completes.    
        } else {
            file.close(null);
        }
    });

    file.on('error', function(err) { // Handle errors
        fs.unlink(dest); // Delete the file async. (But we don't check the result)
        showMessage(err.message);
        return;
    });
};

var filter = function(jsonFile) {
    var jsonFilter = path.dirname(jsonFile) + '/filter_' + path.basename(jsonFile);

    fs.exists(jsonFilter, function(exists) { 
        var options;
        if (exists) { 
            options = {
                mode: 'text',
                pythonOptions: ['-u'],
                scriptPath: '.',
                args: [jsonFile]
            };
        } else {
            options = {
                mode: 'text',
                pythonOptions: ['-u'],
                scriptPath: '.',
                args: [jsonFile]
            };
        } 
        PythonShell.run('clean.py', options, function (err, results) {
            if(results != null) {
                console.log(results);
            } 
            fs.unlink('./velov.json');
        });
    }); 
}

var files = new HashMap();
files.set('./velov.json','https://download.data.grandlyon.com/wfs/rdata?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&maxfeatures=30&request=GetFeature&typename=jcd_jcdecaux.jcdvelov&SRSNAME=urn:ogc:def:crs:EPSG::4171'); 

files.forEach(function(value, key) {   
    download(value,key,filter);
});















