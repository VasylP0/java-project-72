FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./gradlew shadowJar

CMD ["java", "-jar", "build/libs/app-all.jar"]
