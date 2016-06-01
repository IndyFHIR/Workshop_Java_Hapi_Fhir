# Java Hapi-FHIR Client Workshop Project
This is a repository for starter projects that use Hapi-FHIR Java API to connect to Open-Epic and Cerner. These will be used for our meetup on 6/1/2016 6:30pm.

# Using Docker
## Dockerfile

```Dockerfile
FROM java:8

RUN apt-get update --fix-missing
# Install maven
RUN apt-get install -y maven
RUN apt-get install -y git
ENV gitrepos githubrepos
RUN mkdir ${gitrepos}

WORKDIR ${gitrepos}

RUN git clone https://github.com/IndyFHIR/Workshop_Java_Hapi_Fhir.git

WORKDIR Workshop_Java_Hapi_Fhir

RUN ls -l

RUN mvn package
```

## Building Docker Image
```sh
docker build -t workshop_java_hapifhir .
```

## Running Docker Image
```sh
winpty docker run -i workshop_java_hapifhir 
```


### vi Test Java File
vi 

### run Java Test App

java -jar target/org.*.jar


