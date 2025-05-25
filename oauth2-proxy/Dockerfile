FROM amazoncorretto:21

COPY ./target/oauth2-proxy.jar app.jar

EXPOSE 8080

ENTRYPOINT ["/app.jar"]