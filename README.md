# 🌿🌱 Herb Classifier : Coriander VS Parsley  🌿🌱

<img src="src/main/resources/static/assets/images/hero.jpeg" width="400">

Herb Classifier is a Spring Boot application that classifies images of coriander and parsley herbs using a deep learning model built with DeepLearning4j.

## 🖥️ Frontend Application

* The frontend application is built with NextJs and TailwindCSS. Check the [website](https://https://herb-classifier.vercel.app/)
* The frontend application repository is [here](https://github.com/bouazzaayyoub/herb-classifier-front)

## 📚 Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installing](#installing)
- [Deployment](#deployment)
- [Built With](#built-with)
- [Authors](#authors)
- [License](#license)
- [Acknowledgments](#acknowledgments)
- [Key Features](#key-features)

## 🚀Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### 📋Prerequisites

- Java 17
- Maven
- Docker

### 🔧Installing

1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install`
4. Build docker image `docker build -t DOCKER_USERNAME/herb-classifier-api:VERSION -f Dockerfile .

## Deployment

This application is deployed using Docker. The CI/CD pipeline is configured in `.github/workflows/main.yml`.

## 🛠️Built With

- [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
- [Maven](https://maven.apache.org/) - Dependency Management
- [DeepLearning4j](https://deeplearning4j.org/) - Used for image classification
- [Docker](https://www.docker.com/) - Used for deployment
- [Github Actions](https://docs.github.com/en/actions) - Used for CI/CD
- [NextJs](https://nextjs.org/) - Used for the frontend application
- [TailwindCSS](https://tailwindcss.com/) - Used for styling the frontend application

## 👥Authors

- **Adnane Miliari** - *Backend Engineer* - [miliariadnane](https://github.com/miliariadnane)
- **Ayoub Bouazza** - *Frontend Engineer* - [bouazzaayyoub](https://github.com/bouazzaayyoub)

## 📝 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## 🌟Acknowledgments

- Hat tip to anyone whose code was used
- Inspiration
- etc

## Key Features

- [x] Upload an image 🖼️
- [x] Classify the image 🔍
- [x] Display the classification result 📊
- [x] NextJs frontend application
    - [x] Home page 🏠
    - [x] Upload page 📤
    - [x] About page ℹ️
