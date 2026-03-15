FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/notification-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar \
  --server.port=${PORT:-8080} \
  --spring.datasource.url=${SPRING_DATASOURCE_URL} \
  --spring.mail.host=smtp-relay.brevo.com \
  --spring.mail.port=587 \
  --spring.mail.username=${MAIL_USERNAME} \
  --spring.mail.password=${MAIL_PASSWORD} \
  --spring.mail.properties.mail.smtp.auth=true \
  --spring.mail.properties.mail.smtp.starttls.enable=true \
  --notification.email.from=${MAIL_FROM} \
  --notification.email.from-name=${MAIL_FROM_NAME}"]