package com.example.cinemaworld
import scala.concurrent.Future
import akka.http.scaladsl.model.StatusCodes
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import com.example.cinemaworld.Data._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes

object CinemaWorldApp extends App with JsonSupport {
  implicit val system = ActorSystem("cinema-world-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route =
    pathPrefix("movies") {
      concat(
        pathEnd {
          get {
            complete(movies) // List all movies
          }
        },
        path(IntNumber) { id =>
          get {
            val maybeMovie = movies.find(_.id == id)
            maybeMovie match {
              case Some(movie) => 
                val movieShowtimes = showtimes.filter(_.movieId == id)
                complete((movie, movieShowtimes))
              case None => complete(StatusCodes.NotFound)
            }
          }
        }
      )
    } ~
    path("bookings") {
      post {
        entity(as[Booking]) { booking =>
          // Example validation (you would replace this with real validation logic)
          if (booking.quantity > 0) {
            // Simulate async database operation with Future
            val savedBookingFuture = Future {
              // Simulate saving the booking and returning a confirmation
              "Booking confirmed with ID: 12345"
            }
            onSuccess(savedBookingFuture) { confirmationMessage =>
              complete(StatusCodes.OK, confirmationMessage)
            }
          } else {
            complete(StatusCodes.BadRequest, "Invalid booking quantity")
          }
        }
      }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
