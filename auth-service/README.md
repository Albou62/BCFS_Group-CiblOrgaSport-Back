# Auth Service - API

## Base URL
- Via gateway: `http://localhost:8080`
- Préfixe service: `/api`

## Authentification
- JWT Bearer attendu dans l'en-tête `Authorization: Bearer <token>` pour les routes protégées.
- Routes publiques:
  - `POST /api/auth/register`
  - `POST /api/auth/login`
- Routes protégées:
  - `GET /api/auth/me`
  - `GET /api/hello`
  - `GET /api/admin/users` (role `RESPONSABLE`)
  - `PUT /api/admin/users/{id}/role` (role `RESPONSABLE`)

## Modèles JSON

### LoginRequest
```json
{
  "username": "string",
  "password": "string"
}
```

### TokenResponse
```json
{
  "token": "string"
}
```

### UserDto
```json
{
  "id": 1,
  "username": "string",
  "role": "SPECTATEUR"
}
```

### RoleUpdateRequest
```json
{
  "role": "RESPONSABLE"
}
```

Roles possibles: `SPECTATEUR`, `SPORTIF`, `COMMISSAIRE`, `RESPONSABLE`, `VOLONTAIRE`.

## Endpoints

### `POST /api/auth/register`
Crée un utilisateur avec le rôle par défaut `SPECTATEUR`.

Request body:
```json
{
  "username": "alice",
  "password": "secret"
}
```

Responses:
- `200 OK`: corps texte `OK`
- `4xx/5xx`: erreurs Spring (ex: données invalides, username déjà existant)

### `POST /api/auth/login`
Authentifie l'utilisateur et retourne un JWT.

Request body:
```json
{
  "username": "alice",
  "password": "secret"
}
```

Responses:
- `200 OK`
```json
{
  "token": "<jwt>"
}
```
- `401 Unauthorized`: identifiants invalides

### `GET /api/auth/me`
Retourne l'utilisateur courant (depuis le JWT).

Responses:
- `200 OK`
```json
{
  "id": 1,
  "username": "alice",
  "role": "SPECTATEUR"
}
```
- `401 Unauthorized`: JWT absent/invalide

### `GET /api/hello`
Endpoint de test protégé.

Responses:
- `200 OK`: `Hello, authenticated user!`
- `401 Unauthorized`: JWT absent/invalide

### `GET /api/admin/users`
Liste tous les utilisateurs.

Autorisation: rôle `RESPONSABLE`.

Responses:
- `200 OK`
```json
[
  {
    "id": 1,
    "username": "alice",
    "role": "SPECTATEUR"
  }
]
```
- `401 Unauthorized`: JWT absent/invalide
- `403 Forbidden`: rôle insuffisant

### `PUT /api/admin/users/{id}/role`
Met à jour le rôle d'un utilisateur.

Autorisation: rôle `RESPONSABLE`.

Path params:
- `id` (Long): identifiant utilisateur

Request body:
```json
{
  "role": "COMMISSAIRE"
}
```

Responses:
- `200 OK`
```json
{
  "id": 1,
  "username": "alice",
  "role": "COMMISSAIRE"
}
```
- `401 Unauthorized`: JWT absent/invalide
- `403 Forbidden`: rôle insuffisant
- `500 Internal Server Error`: utilisateur introuvable (exception non mappée)
