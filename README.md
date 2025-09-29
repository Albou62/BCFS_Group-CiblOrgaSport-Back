# Lancement d'un container du Back-End

1. docker compose up

# Initialization de la base de données (DEPRECATED)

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
