# Initialization de la base de données

1. Création et run de la base
```shell
docker run --rm --net=host -d -e POSTGRES_USER=admin -e POSTGRES_HOST_AUTH_METHOD=trust -e POSTGRES_PASSWORD=password -e POSTGRES_DB=glop -v /tmp/postgresqlvolume/:/var/lib/postgresql/data --name postgresql_database postgres:16.10-bookworm
```

2. Si besoin de connexion à la base:

```shell
docker exec -it postgresql_database psql -U admin -d glop
```

3. Pour arreter la base de données:
```shell
docker stop postgresql_database
```

Todo:
1. Creer un docker-compose.yml pour le back-end
2. Ajouter la base de données dans le docker-compose
3. Adapter le Dockerfile du back-end pour lancer le docker-compose.

# Lancement d'un container du Back-End

1. docker build -t ciblorgasport-back .
2. docker run -p 1001:80 ciblorgasport-back .
