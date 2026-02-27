# Notification Service - API

## Base URL
- Via gateway: `http://localhost:8080/api/notification`
- Direct service (conteneur): `http://localhost:8080`

Exemples:
- Gateway `GET /api/notification/group`
- Service direct `GET /group`

## Endpoints

### `GET /hello`
Health check simple.

Responses:
- `200 OK`
```json
"Hello from notifications"
```

### `GET /group`
Liste les groupes de notifications.

Responses:
- `200 OK`
```json
[
  {
    "groupId": "1",
    "groupName": "Athletisme"
  }
]
```
- `500 Internal Server Error`

### `POST /group`
Crée un groupe.

Request body:
```json
{
  "name": "Athletisme"
}
```

Responses:
- `200 OK` (corps vide)
- `400 Bad Request`: JSON invalide ou `name` manquant
- `500 Internal Server Error`

### `GET /subscription?userId={userId}`
Liste les abonnements d'un utilisateur.

Important: `userId` est un query param (pas un body JSON).

Responses:
- `200 OK`
```json
[
  {
    "groupId": "1",
    "userId": "42",
    "dateInscription": "Thu Feb 27 10:00:00 UTC 2026",
    "groupName": "Athletisme"
  }
]
```
- `400 Bad Request`: `userId` absent ou invalide
- `500 Internal Server Error`

### `POST /subscription`
Abonne un utilisateur à un groupe.

Request body:
```json
{
  "userId": 42,
  "groupId": 1
}
```

Responses:
- `200 OK` (corps vide)
- `400 Bad Request`: JSON invalide/champs manquants
- `500 Internal Server Error`

### `DELETE /subscription`
Désabonne un utilisateur d'un groupe.

Request body:
```json
{
  "userId": 42,
  "groupId": 1
}
```

Responses:
- `200 OK`
```json
"OK"
```
- `400 Bad Request`: JSON invalide/champs manquants
- `500 Internal Server Error`

### `GET /notification?userId={userId}`
Consomme les messages Kafka du topic utilisateur (`userId`) et retourne les notifications reçues.

Important: `userId` est un query param.

Responses:
- `200 OK`
```json
[
  "Nouvelle notification"
]
```
- `400 Bad Request`: `userId` absent ou invalide

### `POST /notification`
Envoie une notification aux abonnés d'un groupe et sauvegarde l'historique.

Request body:
```json
{
  "groupId": 1,
  "label": "Début de l'épreuve",
  "impactLevel": "INFO"
}
```

Responses:
- `200 OK`
```json
"OK"
```
- `400 Bad Request`: JSON invalide/champs manquants
- `500 Internal Server Error`

## Notes
- Les réponses texte (`OK`, erreurs) sont renvoyées avec `Content-Type: application/json`, même quand le corps n'est pas un objet JSON.
- Les IDs dans les payloads de réponse sont sérialisés sous forme de chaînes dans plusieurs endpoints (`groupId`, `userId`).
