FROM openjdk:8-alpine

COPY ./target/uberjar/alloc-ui.jar /alloc-ui/app.jar

EXPOSE 3000

ENV DATABASE_URL="jdbc:sqlite:./alloc_ui_dev.db"

COPY alloc_ui_dev.db alloc_ui_dev.db

CMD ["java", "-jar", "/alloc-ui/app.jar"]
