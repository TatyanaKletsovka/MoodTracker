# The Mood Crew 15

## Starting app
1.Build application

> ./mvnw package

2.Generate a report for code style into the target/site folder (`checkstyle.html`, `pmd.html`)

> ./mvnw site

3.Build application image

> docker-compose up -d --build

## Stopping app

1.At the end of the test

> docker-compose stop
