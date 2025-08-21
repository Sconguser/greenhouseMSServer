# ===== Etap 1: build aplikacji (Maven + JDK17) =====
FROM maven:3.9.7-eclipse-temurin-17 AS build
WORKDIR /app

# Wgraj ustawienia Mavena z wydłużonymi timeoutami i retry.
# Upewnij się, że w repo masz: mvn-settings/settings.xml (patrz niżej komentarz).
COPY mvn-settings/settings.xml /root/.m2/settings.xml

# Czytelniejszy progress pobrań + mniejsze zużycie RAM na RPi
ENV MAVEN_OPTS="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=info -Djava.awt.headless=true -Xmx512m"

# (Opcjonalnie, jeśli używasz wrappera mvnw i katalogu .mvn)
# COPY .mvn/ .mvn
# COPY mvnw .

# Najpierw tylko pom.xml – to pozwala zcache’ować zależności
COPY pom.xml .

# Pobierz zależności (cache’owana warstwa). -B = non-interactive, -e = pełny stacktrace
RUN mvn -B -DskipTests -e dependency:go-offline

# Teraz dopiero źródła – zmiany w src nie psują cache zależności
COPY src ./src

# Zbuduj JAR bez testów (jeśli potrzebujesz testów, usuń -DskipTests)
RUN mvn -B -DskipTests -e clean package

# ===== Etap 2: runtime (lekki JRE17) =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# Skopiuj zbudowany JAR (dopasuj wzorzec, jeśli nazwa artefaktu jest inna)
COPY --from=build /app/target/*.jar /app/app.jar

# Opcje JVM mogą przyjść z docker-compose przez env
ENV JAVA_OPTS=""

# Domyślny port aplikacji (np. Spring Boot)
EXPOSE 8080

# Uruchomienie aplikacji
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
