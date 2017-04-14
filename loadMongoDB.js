/*
*   index.js
*   Point d'entrée de l'application
*   version 0.1.1
*   Tiré du tuto http://www.supinfo.com/articles/single/3246-realisez-bot-facebook-messenger-nodejs
*   https://developers.facebook.com/docs/messenger-platform/guides/quick-start/
*/
'use strict';

const aslanDB = require('./AslanDBConnector');

process.argv.forEach(function (val, index, array) {
  
  if(index > 1)
  {
    aslanDB.LoadJSonData(array[index]);
  }

});
