FROM amazoncorretto:21 as builder

WORKDIR app
COPY . .

RUN ./gradlew :payments:clean :payments:bootJar

FROM amazoncorretto:21

COPY --from=builder /app/payments/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["/app.jar"]