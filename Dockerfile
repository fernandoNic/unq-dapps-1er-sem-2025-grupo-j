# Usa una imagen base de Java (elige la versión adecuada)
FROM eclipse-temurin:17-jdk-jammy

# Argumento para el nombre del JAR
ARG JAR_FILE=target/*.jar

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR construido al contenedor
COPY ${JAR_FILE} app.jar

# Expone el puerto en el que corre la aplicación (Render usará la variable PORT)
# EXPOSE 8080 # No es estrictamente necesario en Render, pero es buena práctica

# Comando para ejecutar la aplicación cuando el contenedor inicie
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
