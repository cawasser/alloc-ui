FROM openjdk:8-alpine

COPY target/uberjar/alloc-ui.jar /alloc-ui/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/alloc-ui/app.jar"]
