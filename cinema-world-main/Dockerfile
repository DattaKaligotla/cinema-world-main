# Start with a Scala and sbt base image
FROM hseeberger/scala-sbt:11.0.13_1.5.5_2.13.6 as build

# Set the working directory in the Docker image
WORKDIR /app

# Copy your build definition files
COPY build.sbt /app/
COPY project /app/project

# Copy your source code into the Docker image
COPY src /app/src

# Compile your application
RUN sbt clean compile

# Define the command to run your application
CMD ["sbt", "run"]
