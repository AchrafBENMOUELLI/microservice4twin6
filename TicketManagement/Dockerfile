FROM amazoncorretto:17
EXPOSE 8091
ADD target/ticket-service-1.0.0.jar msticket.jar
ENTRYPOINT ["java", "-jar" , "msticket.jar"]
