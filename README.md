# Guess-Who

## About
This is a simple API that was created for fun to design a guessing game where every day a question is given for all
users. Every question have hidden tips and users can make attempts to guess the answer. If they want, they can request
tips to be shown, one by one.

This project was designed to practice DDD and clean architecture aspects. It has 3 submodules, which are:
- guess-who-adapters: Responsible for implementing HTTP endpoints and the repository ports.
- guess-who-application: Uses the command handler pattern to send commands and queries to the domain.
- guess-who-domain: Contains the business logic of the project.

Due to this, it might be considered an _over-engineering_, however, this is intentional as it has study purposes.

## 💻 Stack

- [Kotlin](https://kotlinlang.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [R2DBC](https://r2dbc.io/)
- [PostgreSQL](https://www.postgresql.org/)

## 🚀 Executing locally

Clone the project

```bash
  git clone git@github.com:rivelles/guess-who.git
```

Go inside the project's directory.

```bash
  cd guess-who
```

Simply run:

```bash
  docker-compose -f docker/docker-compose.yaml up -d 
  ./gradlew bootRun
```

<p>
    <a href="LICENSE.md"><img src="https://img.shields.io/static/v1?label=License&message=MIT&color=22c55e&labelColor=202024" alt="License"></a>
</p>