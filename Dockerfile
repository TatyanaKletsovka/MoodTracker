FROM maven:3.8.6 AS MAVEN_BUILD
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn clean package
FROM openjdk:17
ENV TZ="Europe/Minsk"
RUN date
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/mood-0.0.1-SNAPSHOT.jar /app/app.jar
CMD ["java", "-jar", "app.jar"]
