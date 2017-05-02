const mongoClient = require('mongodb').MongoClient;
const fs = require('fs');

// Environment file 
/*var env = require('node-env-file');
env(__dirname + '/.env');*/

var url = 'mongodb://' + process.env.MONGO_DB_TOKEN + '@aslandb-shard-00-01-yened.mongodb.net:27017/grandlyon?ssl=true&authSource=admin';
const GrandLyonCollection = "grandlyonData";

exports.getData = function(table, query, callback) {
  // Connexion au serveur avec la méthode connect
  mongoClient.connect(url, function (err, db) {
    try {
      if (err) {
          return console.log('Connection failed', err);
      }
      console.log('Connexion successful on', "aslandb-shard-00-01-yened.mongodb.net:27017");
      console.log('Connected on db:', db.databaseName);

      // Nous allons travailler ici ...
      /*db.collection(table).find(query).toArray(function(err, collections) {
          if (err) {
            console.log(err);
          } else {
            return collections;
          }
      });*/
    db.collection(table).find(query).toArray(callback);
      
    } catch(error) {
      console.log("error");
      console.log(error);
    }
  });
}

exports.loadJSonData = function(filePath) {
  // Connexion au serveur avec la méthode connect
  mongoClient.connect(url, function (err, db) {
    try {
      if (err) {
          return console.log('Connection failed', err);
      }
        fs.readFile(filePath, 'utf8', function (err, data) {
          if (err) throw err;

          var json = JSON.parse(data);

          db.collection(GrandLyonCollection).insert(json, function(err, doc) {
              console.log("Load done");
          if(err) throw err;
          });
        });  
    } catch(error) {
      console.log(error);
    }
  });
}


exports.replaceDocument = function (filePath) {
    // Connexion au serveur avec la méthode connect
    mongoClient.connect(url, function (err, db) {
        try {
            if (err) {
                return console.log('Connection failed', err);
            }
            fs.readFile(filePath, 'utf8', function (err, data) {
                if (err) throw err;

                var json = JSON.parse(data);

                db.collection(GrandLyonCollection).findOneAndReplace({ "doc_id": json.doc_id}, json, function (err, doc) {
                    console.log("Load done");
                    if (err) throw err;
                });
            });
        } catch (error) {
            console.log(error);
        }
    });
}

exports.insertData = function(table, object, callback) {
    // Connexion au serveur avec la méthode connect
    mongoClient.connect(url, function (err, db) {
        try {
            if (err) {
                callback(err, null);
                return console.log('Connection failed', err);
            } else {
                console.log('Connexion successful on', "aslandb-shard-00-01-yened.mongodb.net:27017");
                console.log('Connected on db:', db.databaseName);
                console.log('Insert Data into ', table);
                db.collection(table).insert(object, function (err, results) {
                    console.log(err);
                    if (err) {
                        throw err;
                    } else {
                        callback(results.ops);
                    }
                }); 
            }         
        } catch(error) {
            console.log("error");
            console.log(error);
            callback(error, null);
        }
    });
}



/*var json = '{"id":"c1ca661d-9e23-4fe6-894c-c08147920af1","timestamp":"2017-05-02T20:27:04.895Z","lang":"fr","result":{"source":"agent","resolvedQuery":"J\'ai faim","action":"","actionIncomplete":true,"parameters":{"geo-city-fr":"","localisation":"","number":"","type-restaurant":"pizza"},"contexts":[{"name":"restaurant_dialog_params_localisation","parameters":{"number":"","type-restaurant.original":"Pizza","type-restaurant":"pizza","localisation.original":"","localisation":"","number.original":"","geo-city-fr":"","geo-city-fr.original":""},"lifespan":1},{"name":"de868a17-9d28-4257-85e6-0d07aad6ae2e_id_dialog_context","parameters":{"number":"","type-restaurant.original":"Pizza","type-restaurant":"pizza","localisation.original":"","localisation":"","number.original":"","geo-city-fr":"","geo-city-fr.original":""},"lifespan":2},{"name":"restaurant_dialog_context","parameters":{"number":"","type-restaurant.original":"Pizza","type-restaurant":"pizza","localisation.original":"","localisation":"","number.original":"","geo-city-fr":"","geo-city-fr.original":""},"lifespan":2}],"metadata":{"intentId":"de868a17-9d28-4257-85e6-0d07aad6ae2e","webhookUsed":"false","webhookForSlotFillingUsed":"false","intentName":"restaurant"},"fulfillment":{"speech":"Merci de partager votre localisation. {\"location\":[]}","messages":[{"type":0,"speech":"Merci de partager votre localisation. \"location\":[]"}]},"score":1},"status":{"code":200,"errorType":"success"},"sessionId":"1156380871157037"}'
json = JSON.parse(json);
insertData("conversation", json, function(err, results) {
    console.log(err);
    console.log(results);
});
*/