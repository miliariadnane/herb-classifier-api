# Build stage
FROM maven:3.6.3-openjdk-17-slim AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk

# Install required libraries
RUN apt-get update && \
    apt-get install -y --no-install-recommends libopenblas-dev liblapack-dev libgomp1 && \
    rm -rf /var/lib/apt/lists/*

# Set library path
ENV JAVA_TOOL_OPTIONS="-Djava.library.path=/opt/libs $JAVA_TOOL_OPTIONS"

# Copy model files
COPY src/main/resources/model /app/model
COPY src/main/resources/static /resources/static

# Check the contents of `/app/model`
RUN ls -l /app/model

# Copy jar file
COPY --from=build /app/target/*.jar app.jar

# Port and container healthcheck
EXPOSE 8080

# Set memory limits
ENV JAVA_TOOL_OPTIONS="-Xmx1g $JAVA_TOOL_OPTIONS"

# Launch application
ENTRYPOINT ["java", "-jar", "/app.jar"]
