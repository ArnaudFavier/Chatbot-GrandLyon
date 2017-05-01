'use strict';

/*
*   Fonction qui supprime les champs { }
*/
function removeFields(response) {
    var string = response;
    var fields = extractFields(response);
    for(var i=0;i<fields.length;i++) 
    {
        string = string.replace(fields[i], "");
    }
    return string;
}

/*
*   Fonction qui supprime les champs { }
*/
function replaceField(response, field, value) {
    var string = response;
    string.replace(field, value);
    return string;
}

/*
*   Fonction qui extrait les champs {  }
*   Fait un post traitement sur les champs pour les rendre conforme JSON
*/
function extractFields(response) {
    var fields = response.match(/(\{[^\{]*\})/g);
    if(fields == undefined) {
        fields = [];
    }
    return fields;
}


exports.removeFields = removeFields;
exports.replaceField = replaceField;
exports.extractFields = extractFields;