FROM java:8 

# Update Linux
RUN apt-get update --fix-missing

# Install maven
RUN apt-get install -y maven
# Install git
RUN apt-get install -y git
# Install vi
RUN apt-get install -y vim

# set gitrepo folder
ENV gitrepos githubrepos

# make gitrepo folder
RUN mkdir ${gitrepos}

# set current folder the git repos folder
WORKDIR ${gitrepos}

# check out this sample project
RUN git clone https://github.com/IndyFHIR/Workshop_Java_Hapi_Fhir.git

# update current folder to checked out project folder
WORKDIR Workshop_Java_Hapi_Fhir

# list files in current folder
RUN ls -l

# Build project in Batch mode and raise listener level to WARN to stop the downloading messages
RUN mvn package

