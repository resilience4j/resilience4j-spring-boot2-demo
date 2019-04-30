FROM gradle:5.4.0-jdk8-alpine
ADD . /code
WORKDIR /code
RUN gradle clean package -DskipTests=true
CMD gradle spring-boot:run