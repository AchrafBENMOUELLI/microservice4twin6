FROM amazoncorretto:17
EXPOSE 8087
ADD target/ApiGatewayDemo-0.0.1-SNAPSHOT.jar apigateway.jar
ENTRYPOINT ["java", "-jar" , "apigateway.jar"]
