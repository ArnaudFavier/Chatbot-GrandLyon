'use strict';

const core = require('./core.js');
const fd = require('./field.js');

/*
*   Fonction qui traite les réponses de type bonjour
*/
function processingGrettings(response) {
	core.prepareMessage(response);
}

function processingHour(response) {
	core.prepareMessage(response);
}


exports.processingGrettings = processingGrettings;
exports.processingHour = processingHour;