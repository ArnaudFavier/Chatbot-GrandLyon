# API-ASLAN

POST /aslan-messenger/signin  

**Input**

```json
{
	"username": string,
	"password": string
}
```

**Output**

200 // OK  

```json
{
	"email": string,
	"username": string,
	"firstname": string,
	"name": string
}
```
403 // Compte inexistant  

500 // Erreur interne

-----------------

POST /aslan-messenger/register 

**Input**

```json
{
	"email": string,
	"username": string,
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
	"username": string,
	"firstname": string,
	"name": string
}
```
403 // Compte existant  

500 // Erreur interne