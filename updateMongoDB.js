'use strict';

const aslanDB = require('./AslanDBConnector');

process.argv.forEach(function (val, index, array) {
  
  if(index > 1)
  {
      aslanDB.replaceDocument(array[index]);
  }

});
