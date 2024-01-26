FROM openjdk:11
COPY /template-rest/target/template-rest-0.0.1.jar /usr/local/lib/template-api.jar

EXPOSE 8083

ENTRYPOINT ["java","-jar","/usr/local/lib/template-api.jar"]