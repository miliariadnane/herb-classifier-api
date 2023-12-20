# Herb Classifier

Herb Classifier is a Spring Boot application that classifies images of coriander and parsley herbs.

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installing](#installing)
- [Running the tests](#running-the-tests)
- [Deployment](#deployment)
- [Built With](#built-with)
- [Versioning](#versioning)
- [Authors](#authors)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java 17
- Maven
- Docker

### Installing

1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install`

## Running the tests

Run `mvn test` to execute the unit tests.

## Deployment

This application is deployed using Docker. The CI/CD pipeline is configured in `.github/workflows/main.yml`.

## Built With

- [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
- [Maven](https://maven.apache.org/) - Dependency Management
- [Docker](https://www.docker.com/) - Used for deployment
- [DeepLearning4j](https://deeplearning4j.org/) - Used for image classification

## Versioning

We use [SemVer](http://semver.org/) for versioning.

## Authors

- **Adnane Miliari** - *Backend Engineer* - [miliariadnane](https://github.com/miliariadnane)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

- Hat tip to anyone whose code was used
- Inspiration
- etc
