const mongoClient = require('mongodb').MongoClient;

var url = 'mongodb://H4411Admin:bullshit4life@aslandb-shard-00-01-yened.mongodb.net:27017/grandlyon?ssl=true&authSource=admin';

exports.getData = function(table, query, callback) {
  // Connexion au serveur avec la méthode connect
  mongoClient.connect(url, function (err, db) {
    try{
      if (err) {
          return console.log('Connection failed', err);
      }
      console.log('Connexion successful on', "aslandb-shard-00-01-yened.mongodb.net:27017");
      console.log('Connected on db:', db.databaseName);

      // Nous allons travailler ici ...
      db.collection(table).find(query).toArray(function(err, collections) {
          if (err) {
            console.log(err);
          } else {
            return collections;
          }
      });
      
    } catch(error) {
      console.log("error");
      console.log(error);
    }
  });
}


