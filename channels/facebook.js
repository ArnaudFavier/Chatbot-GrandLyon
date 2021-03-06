'use strict';

const request = require('request');
const core = require('./../core/core.js');

/**
 * CREDENTIALS
 **/
const VALIDATION_TOKEN = process.env.FACEBOOK_VALIDATION_TOKEN;
const PAGE_ACCESS_TOKEN = process.env.FACEBOOK_PAGE_ACCESS_TOKEN;

/*
* Fonction appelé par Facbook pour checker le serveur
*/
function webhook(req, res) {
    return req.query['hub.mode'] === 'subscribe' && req.query['hub.verify_token'] === VALIDATION_TOKEN;
}

/*
* Fonction appelé par les routes lors de la reception d'un message
*/
function receivedMessage(req, res) {
    var data = req.body;
    if (data.object === 'page') {
        //Message reçu de Facebook
        data.entry.forEach(function(entry) {
            var pageID = entry.id;
            var timeOfEvent = entry.time;
            entry.messaging.forEach(function(event) {
                if (event.message) {
                    console.log("Event received : ", JSON.stringify(event));
                    extractMessage(event);
                }
            });
        });
    } else {
        //Pour tester via CURL
        extractMessage(req.body);
    }
}
/*
* Fonction qui permet de traiter un message reçu
*/
function extractMessage(event) {
    var senderID = event.sender.id;
    request({
        uri: 'https://graph.facebook.com/' + senderID,
        qs: { access_token: PAGE_ACCESS_TOKEN },
        headers: { 'Content-Type': 'application/json' },
        method: 'GET'
    }, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var recipientID = event.recipient.id;
            var timeOfMessage = event.timestamp;
            var message = event.message;
            
            console.log(JSON.stringify(message));

            var messageText = message.text;
            var messageAttachments = message.attachments;

            try {
                body = JSON.parse(body);
            } catch (err){
            }
            var message = {
                channel: "Facebook",
                senderID: senderID,
                timestamp: timeOfMessage,
                text: messageText,
                first_name: body.first_name,
                last_name: body.last_name
            };
            /*
            *   Coordonnées
            */
            if(messageAttachments != undefined && messageAttachments[0].payload != undefined && messageAttachments[0].payload.coordinates != undefined) {
                message["location"] = messageAttachments[0].payload.coordinates;
            }
            beginWriting(message.senderID);
            core.receivedMessage(message);
        } else {
            console.error("Unable to get name of user.");
            console.error(error);
        }
    });
}

/*
* Fonction appelé par le core pour envoyer un message sur la channel Facebook
*/
function sendMessage(message) {
    switch(message.type) {
        case "text":
            sendTextMessage(message);
        break;
        case "quickreply":
            sendQuickReplyMessage(message);
            break;
        case "location":
            sendLocationMessage(message);
            break;
        case "template":
            sendTemplateMessage(message);
            break;
        break;
    }
}

/*
* Fonction appelé par le core pour envoyer des messages sur la channel Facebook
*/
function sendMessages(messages) {
    for(var i=0;i<messages.length;i++) {
        sendMessage(messages[i]);
    }
}

/*
* Fonction qui envoie un message de type text
*/
function sendTextMessage(message) {
    console.log("Messages sended : ", JSON.stringify(message));
    if(message.senderID != undefined && message.text != undefined) {
        var messageData = {
            recipient: {
                id: message.senderID
            },
            message: {
                text: message.text
            }
        };
        callSendAPI(messageData);
    }
}

/*
* Fonction qui envoie un message de type quickreply
*/
function sendQuickReplyMessage(message) {
    console.log("Messages sended : ", JSON.stringify(message));
    if(message.senderID != undefined && message.text != undefined) {
        var messageData = {
            recipient: {
                id: message.senderID
            },
            message: {
                text: message.text,
                quick_replies: []
            }
        };
        for(var i=0;i<message.choices.length;i++) {
            var quickreply = {
                content_type: "text",
                title: message.choices[i],
                payload: message.choices[i]
            };
            messageData.message.quick_replies.push(quickreply);
        }
        callSendAPI(messageData);
    }
}

/*
* Fonction qui envoie un message de type location
*/
function sendLocationMessage(message) {
    console.log("Messages sended : ", JSON.stringify(message));
    if(message.senderID != undefined && message.text != undefined) {
        var messageData = {
            recipient: {
                id: message.senderID
            },
            message: {
                text: message.text,
                quick_replies: [
                    {
                        "content_type":"location",
                    }
                ]
            }
        };
        callSendAPI(messageData);
    }
}

/*
* Fonction qui envoie un message audio/file/image/video
*/
function sendFileMessage(message) {
    console.log("Messages sended : ", JSON.stringify(message));
    if(message.senderID != undefined && message.text != undefined) {
        var messageData = {
            recipient: {
                id: message.senderID
            },
            message: {
                attachment:{
                    type: message.type,
                    payload: {
                        url: message.text
                    }
                }
            }
        };
        callSendAPI(messageData);
    }
}

/*
* Fonction qui envoie un message audio/file/image/video
*/
function sendTemplateMessage(message) {
    console.log("Messages sended : ", JSON.stringify(message));
    if(message.senderID != undefined && message.attachment != undefined) {
        var elements = [];
        /*
        *   Envoie du message avant le template
        */
        var messageData = {
            recipient: {
                id: message.senderID
            },
            message: {
                text: message.text
            }
        };
        callSendAPI(messageData);
        for(var i=0;i<message.attachment.length;i++) {
            var element = {
                title: message.attachment[i].title,
                image_url: message.attachment[i].image_url,
                subtitle: message.attachment[i].subtitle,
                default_action: {
                    type: "web_url",
                    url: message.attachment[i].url,
                    messenger_extensions: true,
                    webview_height_ratio: "tall"
                },
                buttons: [
                    {
                        title: message.attachment[i].button_title,
                        type: "web_url",
                        url: message.attachment[i].button_url,
                        messenger_extensions: true,
                        webview_height_ratio: "compact"                      
                    }
                ]
            }
            elements.push(element);
            if(i==3)break;
        }
        if(message.attachment.length > 4) {
            var buttons = [
                    {
                        title: "Voir plus",
                        type: "postback",
                        payload: "payload"                        
                    }
                ]
        }  
        var templateData = {
            recipient: {
                id: message.senderID
            },
            message: {
                attachment:{
                    type: message.type,
                    payload: {
                        template_type: "list",
                        top_element_style: "compact",
                        elements: elements,
                        buttons: buttons
                    }
                }
            }
        };
        callSendAPI(templateData);
    }
}

/*
* Fonction qui appel l'API messages de Facebook
*/
function callSendAPI(messageData) {
    console.log("Messages sended via API : ", JSON.stringify(messageData));
    finishWriting(messageData.recipient.id, function() {
            request({
            uri: 'https://graph.facebook.com/v2.6/me/messages',
            qs: { access_token: PAGE_ACCESS_TOKEN },
            method: 'POST',
            json: messageData
        }, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                var recipientId = body.recipient_id;
                var messageId = body.message_id;

                console.log("Successfully sent generic message with id %s to recipient %s", 
                    messageId, recipientId);
            } else {
                console.error("Unable to send message.");
                console.error(error);
                console.error(response);
            }
        });
    });
}

function beginWriting(id) {
    request({
        uri: 'https://graph.facebook.com/v2.6/me/messages',
        qs: { access_token: PAGE_ACCESS_TOKEN },
        method: 'POST',
        json: {recipient:{id: id}, sender_action:"typing_on"}
    }, function (error, response, body) {
        if (!error && response.statusCode == 200) {

        } else {
            console.error("Unable to send message.");
            console.error(error);
        }
    });
}

function finishWriting(id, callback) {
    request({
        uri: 'https://graph.facebook.com/v2.6/me/messages',
        qs: { access_token: PAGE_ACCESS_TOKEN },
        method: 'POST',
        json: {recipient:{id: id}, sender_action:"typing_off"}
    }, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            callback();
        } else {
            console.error("Unable to send message.");
            console.error(error);
        }
    });
}

exports.webhook = webhook
exports.receivedMessage = receivedMessage
exports.sendMessage = sendMessage
exports.sendMessages = sendMessages