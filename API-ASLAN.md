# API-ASLAN

**Connexion d'un utilisateur**

**POST** */aslan-messenger/signin* 

**Input**

```json
{
	"email": string,
	"password": string
}
```

**Output**

200 // OK  

```json
{
	"email": string,
	"firstname": string,
	"name": string,
	"token": string
}
```
403 // Compte inexistant  

422 // JSON Input mal formé

500 // Erreur interne

-----------------

**Inscription d'un utilisateur**

**POST** */aslan-messenger/register*

**Input**

```json
{
	"email": string,
	"password": string,
	"firstname": string,
	"name": string
}
```

**Output**

200 // OK  

```json
{
	"email": string,
	"firstname": string,
	"name": string,
	"token": string
}
```
403 // Compte existant  

422 // JSON Input mal formé

500 // Erreur interne

-----------------

**Envoie d'un message**

**POST** */aslan-messenger/message*

**Input**

```json
{
	"user_id": string,
	"token": string,
	"message": json
}
```

**Output**

200 // OK  

```json
{
	"success": "success"
}
```
403 // Token invalide

404 // Utilisateur inexistant 

422 // JSON Input mal formé

500 // Erreur interne

-----------------

**Récupération de messages**

**GET** */aslan-messenger/message/:token/:user_id/:message_id*

**Input**
Mettre les paramètres dans l'URL, si on veut tous les messages mettre message_id à -1

**Output**

200 // OK  

```json
{
	"messages": [
		{
			"_id" : string,
			"message" : JSON,
			"user_id" : string,
			"date" : string 
		}, 
		{
			"_id" : string,
			"message" : JSON,
			"user_id" : string,
			"date" : string,
			"isAslan": true #est optionnel
		},...
	]
}
```
403 // Token invalide

404 // Utilisateur inexistant 

422 // JSON Input mal formé

500 // Erreur interne