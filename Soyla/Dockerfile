FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY .mvn .mvn
<<<<<<< HEAD
COPY src src

RUN mvn -B -DskipTests package
=======
COPY mvnw ./
COPY src src

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw -DskipTests package
>>>>>>> Front

FROM eclipse-temurin:17-jre

WORKDIR /app

<<<<<<< HEAD
COPY --from=build /app/target/*.jar app.jar

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "app.jar"]
=======
ENV PORT=8080

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
>>>>>>> Front
