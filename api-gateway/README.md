# API Gateway - Spécification des routes

## Base URL
- `http://localhost:8080`

## Rôle
Le gateway NGINX expose une API unifiée et proxy les requêtes vers:
- `auth-service`
- `competition-service`
- `notification-service`

## Routes exposées

### Santé
- `GET /health`
  - `200 OK`
  - Body: `Gateway OK`

### Auth Service (proxy vers `auth-service`)
- `/api/auth`
- `/api/admin`
- `/api/auth/hello`

Note: les routes réelles du service auth incluent aussi `/api/admin/users`.

### Competition Service (proxy vers `competition-service`)
- `/api/competitions`
- `/api/competitions/public/*` (réécrit vers `/api/public/*`)
- `/api/public`
- `/api/epreuves`
- `/api/manches`

### User Service (proxy vers `user-service`)
- `/api/users/{path}` -> `/{path}` sur le service user

### Notification Service (proxy vers `notification-service`)
- `/api/notification/{path}` -> `/{path}` sur le service notification

### Localisation Service (proxy vers `geolocalisation-service)
- `/api/localisation/{path}` -> `/{path}` sur le service geolocalisation

Exemples:
- `GET /api/notification/group` -> `GET /group`
- `POST /api/notification/subscription` -> `POST /subscription`

## CORS
Le gateway gère les requêtes `OPTIONS` (preflight) sur plusieurs routes et renvoie `204` avec les headers CORS.
