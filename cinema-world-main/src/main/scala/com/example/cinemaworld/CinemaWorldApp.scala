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
object CinemaWorldApp extends App with JsonSupport {
  implicit val system = ActorSystem("cinemaWorldSystem")
  implicit val materializer = ActorMaterializer()

  // JSON Formats
  override implicit val movieFormat = jsonFormat4(Movie)
  override implicit val showtimeFormat = jsonFormat4(Showtime)
  override implicit val reservationFormat = jsonFormat3(Reservation)

  val route: Route =
  pathPrefix("movies") {
    concat(
      // Route for listing all movies
      pathEnd {
        get {
          complete(AppDatabase.listAllMovies())
        }
      },
      // Route for fetching movie details by ID
      path(IntNumber) { id =>
        get {
          onSuccess(AppDatabase.getMovieDetailsById(id)) {
            case Some(movie) => complete(movie)
            case None        => complete(StatusCodes.NotFound, "Movie not found")
          }
        }
      }
    )
  } ~
 pathPrefix("showtimes") {
    concat(
      // Route for listing all showtimes
      pathEnd {
        get {
          onSuccess(AppDatabase.getAllShowtimes) { showtimes =>
            complete(showtimes)
          }
        }
      },
      // Route for fetching showtime details by ID
      path(IntNumber) { showtimeId =>
        get {
          onSuccess(AppDatabase.getShowtimeById(showtimeId)) {
            case Some(showtime) => complete(showtime)
            case None           => complete(StatusCodes.NotFound, "Showtime not found")
          }
        }
      },
      // Route for adding a new showtime
      post {
        entity(as[Showtime]) { showtime =>
          onSuccess(AppDatabase.addShowtime(showtime)) { _ =>
            complete(StatusCodes.Created, "Showtime added successfully")
          }
        }
      }
    )
  }
     

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/")
  StdIn.readLine() // let it run until user presses return
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}