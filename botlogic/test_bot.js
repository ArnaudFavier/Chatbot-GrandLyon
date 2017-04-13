// Environment file
var env = require('node-env-file');
env(__dirname + '/../.env');

// Import Wit.Ai
var Botlogic = require('./botlogic.js');

//Botlogic.activeDebugMode();
function callback(request, response){
	console.log('Custom callback Wit : ', JSON.stringify(response));
}

Botlogic.defineCallback(callback);
//Botlogic.sendMessage('Quel temps fait-il à Tokyo ?', `session-${Date.now()}`, {});

Botlogic.activeDebugMode();