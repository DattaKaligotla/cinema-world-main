package com.example.cinemaworld

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.RootJsonFormat
import spray.json._

object CinemaWorldApp extends App with JsonSupport {
  implicit val system = ActorSystem("cinemaWorldSystem")
  implicit val materializer = ActorMaterializer()

 
  /*implicit val intJsonFormat: RootJsonFormat[Int] = new RootJsonFormat[Int] {
    def write(obj: Int): JsValue = JsNumber(obj)
    def read(json: JsValue): Int = json match {
      case JsNumber(num) => num.toInt
      case _ => deserializationError("Int expected")
    }
  }*/
  
  val route: Route =
  concat(
    pathPrefix("movies") {
      concat(
        // List all movies
        pathEnd {
          concat(
            get {
              complete(AppDatabase.listAllMovies())
            },
            // Add a new movie
            post {
              entity(as[Movie]) { movie =>
                onSuccess(AppDatabase.addMovie(movie)) { _ =>
                  complete(StatusCodes.Created, "Movie added successfully")
                }
              }
            }
          )
        },
        // Fetch movie details by ID
        path(IntNumber) { id =>
          get {
            onSuccess(AppDatabase.getMovieDetailsById(id)) {
              case Some(movie) => complete(movie)
              case None => complete(StatusCodes.NotFound, "Movie not found")
            }
          }
        }
      )
    },
    pathPrefix("showtimes") {
      concat(
        // List all showtimes
        pathEnd {
          get {
            complete(AppDatabase.getAllShowtimes)
          }
        },
        // Fetch showtimes by movie ID
        path("movie" / IntNumber) { movieId =>
          get {
            onSuccess(AppDatabase.getShowtimesByMovieId(movieId)) { showtimes =>
              complete(showtimes)
            }
          }
        },
        // Fetch showtime details by ID
        path(IntNumber) { showtimeId =>
          get {
            onSuccess(AppDatabase.getShowtimeById(showtimeId)) {
              case Some(showtime) => complete(showtime)
              case None => complete(StatusCodes.NotFound, "Showtime not found")
            }
          }
        },
        // Add a new showtime
        post {
          entity(as[Showtime]) { showtime =>
            onSuccess(AppDatabase.addShowtime(showtime)) { showtimeId =>
              complete(StatusCodes.Created, showtimeId.toString) // Convert Int ID to String for response
            }
          }
        }
      )
    },
    pathPrefix("reservations") {
      concat(
        // List all reservations
        pathEnd {
          get {
            complete(AppDatabase.getAllReservations())
          }
        },
        // Add a new reservation with total charge calculation
        post {
          entity(as[Reservation]) { reservation =>
            onSuccess(AppDatabase.addReservation(reservation)) { reservationId =>
              complete(StatusCodes.Created, reservationId.toString) // Convert Int ID to String for response
            }
          }
        },
        // Fetch a reservation by ID
        path(IntNumber) { id =>
          get {
            onSuccess(AppDatabase.getReservationById(id)) {
              case Some(reservation) => complete(reservation)
              case None => complete(StatusCodes.NotFound, "Reservation not found")
            }
          }
        },
        // Fetch reservations by showtime ID
        pathPrefix("showtime" / IntNumber) { showtimeId =>
          get {
            onSuccess(AppDatabase.getReservationsByShowtime(showtimeId)) { reservations =>
              complete(reservations)
            }
          }
        },
        // Cancel a reservation with penalty calculation
        path("cancel" / IntNumber) { reservationId =>
          post {
            onSuccess(AppDatabase.cancelReservation(reservationId)) { result =>
              complete(StatusCodes.OK, s"Reservation cancelled. $result")
            }
          }
        }
      )
    }
  )



  

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/")
  StdIn.readLine() // let it run until user presses return
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}