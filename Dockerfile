FROM gradle:8-jdk17 AS build
WORKDIR /app

COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test


# Run stage

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/build/libs/SWP391_D01_G3-0.0.1-SNAPSHOT.jar app.jar
# The EXPOSE instruction does not actually publish the port. It functions as a type of documentation between the person who builds the image and the person who runs the container, about which ports are intended to be published.
# Render will automatically detect the port specified by the PORT environment variable.
EXPOSE 8080 

ENTRYPOINT ["java","-jar","app.jar"] 