# API-ASLAN

POST /aslan-messenger/signin  

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

POST /aslan-messenger/register 

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

POST /aslan-messenger/message 

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