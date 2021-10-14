FROM gradle:7-jdk-openj9
ADD --chown=gradle . /code
WORKDIR /code
RUN gradle clean build -x test
CMD gradle bootRun
