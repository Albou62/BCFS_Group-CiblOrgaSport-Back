# Competition Service - API

## Base URL
- Via gateway: `http://localhost:8080`
- Préfixes exposés: `/api/competitions`, `/api/public`, `/api/epreuves`, `/api/manches`

## Endpoints

### `GET /api/competitions`
Liste toutes les compétitions.

Response `200 OK`:
```json
[
  {
    "id": 1,
    "name": "Interclub 2026",
    "dateDebut": "2026-04-12",
    "dateFin": "2026-04-14",
    "finished": false,
    "disciplineId": 2
  }
]
```

### `GET /api/competitions/{id}`
Retourne une compétition.

Responses:
- `200 OK`: `CompetitionDto`
- `404 Not Found`: compétition inexistante

### `POST /api/competitions`
Crée une compétition.

Request body (`CreateCompetitionRequest`):
```json
{
  "name": "Interclub 2026",
  "dateDebut": "2026-04-12",
  "dateFin": "2026-04-14",
  "disciplineId": 2
}
```

Responses:
- `200 OK`: `CompetitionDto`
- `400 Bad Request`: compétition invalide (ex: `dateFin < dateDebut`)
- `404 Not Found`: discipline introuvable

### `PUT /api/competitions/{id}`
Met à jour une compétition.

Request body: même format que création.

Responses:
- `200 OK`: `CompetitionDto`
- `400 Bad Request`: payload invalide
- `404 Not Found`: compétition ou discipline introuvable

### `POST /api/competitions/{id}/finish`
Marque la compétition comme terminée (`finished = true`).

Responses:
- `200 OK`: `CompetitionDto`
- `404 Not Found`: compétition introuvable

### `GET /api/competitions/{competitionId}/epreuves`
Liste les épreuves d'une compétition.

Response `200 OK`:
```json
[
  {
    "id": 10,
    "name": "100m",
    "horaireAthletes": "2026-04-12T09:00:00",
    "horairePublic": "2026-04-12T09:30:00",
    "competitionId": 1
  }
]
```

### `POST /api/competitions/{competitionId}/epreuves`
Crée une épreuve pour une compétition.

Request body (`CreateEpreuveRequest`):
```json
{
  "name": "100m",
  "horaireAthletes": "2026-04-12T09:00:00",
  "horairePublic": "2026-04-12T09:30:00"
}
```

Responses:
- `200 OK`: `EpreuveDto`
- `404 Not Found`: compétition introuvable

### `GET /api/epreuves/{epreuveId}/manches`
Liste les manches d'une épreuve (triées par `ordre`).

Responses:
- `200 OK`: liste `MancheDto`
- `404 Not Found`: épreuve introuvable

### `POST /api/epreuves/{epreuveId}/manches`
Crée une manche.

Request body (`CreateMancheRequest`):
```json
{
  "name": "Finale",
  "typeClassement": "TIME_ASC",
  "ordre": 1
}
```

Enums:
- `typeClassement`: `TIME_ASC`, `SCORE_DESC`

Responses:
- `200 OK`: `MancheDto`
- `400 Bad Request`: `name` ou `typeClassement` manquant
- `404 Not Found`: épreuve introuvable

### `GET /api/manches/{mancheId}/resultats`
Liste les résultats d'une manche.

Responses:
- `200 OK`: liste `ResultatDto`
- `404 Not Found`: manche introuvable

### `POST /api/manches/{mancheId}/resultats`
Crée un résultat et recalcule le classement.

Request body (`CreateResultatRequest`):
```json
{
  "athleteId": 1001,
  "score": 9.95,
  "temps": "00:00:09.95",
  "statut": "VALIDE"
}
```

Enums:
- `statut`: `VALIDE`, `DNF`, `DNS`, `DSQ`

Règles:
- `athleteId` obligatoire
- si `typeClassement=TIME_ASC` et `statut=VALIDE`, `temps` obligatoire
- si `typeClassement=SCORE_DESC` et `statut=VALIDE`, `score` obligatoire
- un seul résultat par couple `(mancheId, athleteId)`

Responses:
- `200 OK`: `ResultatDto`
- `400 Bad Request`: contrainte métier invalide
- `404 Not Found`: manche introuvable
- `409 Conflict`: résultat déjà existant pour cet athlète

### `PUT /api/manches/{mancheId}/resultats/{resultatId}`
Met à jour un résultat et recalcule le classement.

Request body (`UpdateResultatRequest`):
```json
{
  "score": 9.97,
  "temps": "00:00:09.97",
  "statut": "VALIDE"
}
```

Responses:
- `200 OK`: `ResultatDto`
- `400 Bad Request`: contrainte métier invalide
- `404 Not Found`: manche/résultat introuvable

### `GET /api/manches/{mancheId}/classement`
Retourne le classement de la manche.

Response `200 OK` (`ClassementDto`):
```json
{
  "mancheId": 20,
  "typeClassement": "TIME_ASC",
  "resultats": [
    {
      "id": 1,
      "athleteId": 1001,
      "mancheId": 20,
      "score": 9.95,
      "temps": "00:00:09.95",
      "rang": 1,
      "statut": "VALIDE",
      "medaille": "OR"
    }
  ]
}
```

### `GET /api/manches/{mancheId}/podium`
Retourne le podium de la manche.

Response `200 OK` (`PodiumDto`):
```json
{
  "mancheId": 20,
  "orResultat": { "id": 1, "athleteId": 1001, "mancheId": 20, "score": 9.95, "temps": "00:00:09.95", "rang": 1, "statut": "VALIDE", "medaille": "OR" },
  "argentResultat": { "id": 2, "athleteId": 1002, "mancheId": 20, "score": 10.01, "temps": "00:00:10.01", "rang": 2, "statut": "VALIDE", "medaille": "ARGENT" },
  "bronzeResultat": { "id": 3, "athleteId": 1003, "mancheId": 20, "score": 10.08, "temps": "00:00:10.08", "rang": 3, "statut": "VALIDE", "medaille": "BRONZE" }
}
```

### `GET /api/public/upcoming-epreuves?limit=3`
Retourne les épreuves à venir (triées par `horairePublic` ascendant).

Query params:
- `limit` (int, optionnel, défaut `3`)

Response `200 OK`: liste `EpreuveDto`.

## Format d'erreur
Les erreurs métier sont retournées au format:
```json
{
  "timestamp": "2026-02-27T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "...",
  "path": "/api/..."
}
```
