FROM openjdk:8-jdk-alpine
WORKDIR addDir
EXPOSE 8081
COPY target/diploma_cloud_storage-0.0.1-SNAPSHOT.jar diploma.jar
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/postgres
ENTRYPOINT ["java", "-jar", "diploma.jar"]
