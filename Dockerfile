FROM eclipse-temurin:21-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR generado (asumimos que el build lo generó en /target)
COPY build/libs/SoccerGenius-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto que utiliza la aplicación Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
