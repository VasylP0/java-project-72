# Page Analyzer

Page Analyzer is a web application that analyzes websites for basic SEO information.

Users can add a website URL, run a check, and view information such as the HTTP response status code, page title, H1 heading, and meta description.

The application is built with Java, Javalin, JTE, JDBC, HikariCP, and PostgreSQL.

## Hexlet tests and linter status

[![Actions Status](https://github.com/VasylP0/java-project-72/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/VasylP0/java-project-72/actions)

[![codecov](https://codecov.io/github/VasylP0/java-project-72/graph/badge.svg?token=1LRCJ5DGNE)](https://codecov.io/github/VasylP0/java-project-72)

## Requirements

- Java 21
- Gradle
- PostgreSQL for production

## Run locally

Clone the repository and open the application directory:

```bash
git clone https://github.com/VasylP0/java-project-72.git
cd java-project-72/app
```

Run the application:

```bash
./gradlew run
```

Then open `http://localhost:7070` in your browser.

## Tests

Run the automated tests with:

```bash
./gradlew test
```

## Deployed application

https://java-project-72-249f.onrender.com
