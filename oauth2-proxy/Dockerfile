FROM amazoncorretto:21 as builder

WORKDIR app
COPY . .

RUN yum -y update && yum -y install tar && yum -y install gzip

RUN chmod +x ./mvnw && ./mvnw clean package -pl oauth2-proxy -Dmaven.test.skip

FROM amazoncorretto:21

COPY --from=builder /app/oauth2-proxy/target/oauth2-proxy.jar app.jar

EXPOSE 8080

ENTRYPOINT ["/app.jar"]