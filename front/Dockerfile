FROM amazoncorretto:21 as builder

WORKDIR app
COPY . .

RUN yum -y update && yum -y install tar && yum -y install gzip

RUN chmod +x ./mvnw && ./mvnw clean package -pl front -am -Dmaven.test.skip

FROM amazoncorretto:21

COPY --from=builder /app/front/target/front.jar app.jar

EXPOSE 8080

ENTRYPOINT ["/app.jar", "--spring.profiles.active=prod"]