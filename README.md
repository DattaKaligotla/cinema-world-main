# CinemaWorld Project

## Overview
CinemaWorld is a Scala-based REST API project for managing a cinema platform, where you can list movies, showtimes, and make reservations. The project utilizes Slick for database operations, Akka HTTP for the web server and routing, and ScalaTest for testing.

## Prerequisites
- Java JDK 8 or above
- Scala 2.13.x
- sbt (Scala Build Tool)
- PostgreSQL

## Dependencies
- **Akka HTTP**: Used for creating the REST API server.
- **Slick**: Scala's functional relational mapping (FRM) library for interacting with the database in a functional style.
- **ScalaTest**: Testing tool for Scala and Java developers.
- **PostgreSQL**: Database used for storing movies, showtimes, and reservations data.

## Project Structure
- `src/main/scala/com/example/cinemaworld`
  - **AppDatabase.scala**: Contains the database operations and business logic.
  - **CinemaWorldApp.scala**: Defines the server and routes for the API.
  - **Models.scala**: Contains case classes for movies, showtimes, and reservations, and their corresponding Slick table mappings.
- `src/main/resources/application.conf`: Configuration file for database connection settings.
- `src/test/scala/com/example/cinemaworld`: Contains ScalaTest unit tests for the application's core functionality and edge cases.
## API Endpoint Overview

### Movies
- **GET /movies**: List all movies.
- **POST /movies**: Add a new movie.
- **GET /movies/{id}**: Fetch movie details by ID.

### Showtimes
- **GET /showtimes**: List all showtimes.
- **POST /showtimes**: Add a new showtime.
- **GET /showtimes/movie/{movieId}**: Fetch showtimes by movie ID.
- **GET /showtimes/{showtimeId}**: Fetch showtime details by ID.

### Reservations
- **GET /reservations**: List all reservations.
- **POST /reservations**: Add a new reservation.
- **GET /reservations/{id}**: Fetch a reservation by ID.
- **GET /reservations/showtime/{showtimeId}**: Fetch reservations by showtime ID.
- **POST /reservations/cancel/{reservationId}**: Cancel a reservation with penalty calculation.

## Setup Instructions

### Configuring the Database
1. Update `src/main/resources/application.conf` with your PostgreSQL username and password:
   ```hocon
   slick {
     dbs {
       default {
         profile = "slick.jdbc.PostgresProfile$"
         db {
           driver = "org.postgresql.Driver"
           url = "jdbc:postgresql://localhost:5432/cinema_world"
           user = "postgres"
           password = "yourpassword"
         }
       }
     }
   }
   # CinemaWorld Project Setup and Usage Guide

### Database Initialization
Before running the application or tests, initialize the database schema by executing the `init.sql` script in your PostgreSQL database. This script will create the necessary tables and relationships:
IMPORTANT : sql commands change "postgres" in the cmd script to whatever your superuser name is in postgres
IMPORTANT: after using the erase.sql script(will get into it below), always run the init.sql script
```bash
psql -d cinema_world -U postgres -f init.sql
```
### Test Business Functionality
```bash
sbt test
```
^this will test business functioanlity

### Testing the API Endpoints
run this bash script in terminal to test the endpoint calls in conjunction with business logic
Open a new terminal window and execute 
```bash
./test_api.sh
```
to test the API using curl commands. This script performs a series of automated calls(a lot) for the API endpoints. Feel free to look at this as well to see what exact calls this is making
### Viewing Data in the Database
After running that script, you can view the contents of the movies, showtimes, and reservations tables in the command line by running the provided SQL scripts:
```bash
psql -d cinema_world -U postgres -f movies.sql
psql -d cinema_world -U postgres -f showtimes.sql
psql -d cinema_world -U postgres -f reservations.sql
```
after that feel free to erase the tables to clean everything up, running
```bash
psql -d cinema_world -U postgres -f erase.sql
```
IMPORTANT: run psql -d cinema_world -U postgres -f init.sql to generate the tables again in postgres

## Now for the Magic
make sure to have
```bash

psql -d cinema_world -U postgres -f init.sql
```
ran first to have the values in the table to populate
then run sbt run in terminal and after it is setup in `http://localhost:8080/`

open a new terminal and run curl commands based on the schema:
here are examples of curl commands to play around with,
anytime u want to see the tables, run these scripts to generate the tables
```bash
psql -d cinema_world -U postgres -f movies.sql
psql -d cinema_world -U postgres -f showtimes.sql
psql -d cinema_world -U postgres -f reservations.sql
```
please reach out to me at `sdkaligotla@gmail.com` or at `240-474-7153`, if you need assistance at all with anything here or if the documentation is unclear
I had a great time working on this project and want it running it smoothly. Thank You!
