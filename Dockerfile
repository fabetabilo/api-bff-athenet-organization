# ---- Build ----
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B \
    && cp target/bff-organization-*.jar app.jar

# ---- Runtime ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN useradd --system --create-home --uid 1000 spring
USER spring

COPY --from=build /app/app.jar app.jar

EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]
