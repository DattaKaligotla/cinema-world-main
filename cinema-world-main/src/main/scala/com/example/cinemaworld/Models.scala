package com.example.cinemaworld

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat
case class Movie(id: Int, title: String, duration: Int, rating: String)
case class Showtime(id: Int, movieId: Int, startTime: String, theater: String)
case class Booking(movieId: Int, showtimeId: Int, quantity: Int)

trait JsonSupport extends DefaultJsonProtocol {
  implicit val movieFormat = jsonFormat4(Movie)
  implicit val showtimeFormat = jsonFormat4(Showtime)
  implicit val bookingFormat = jsonFormat3(Booking)
}

object Data {
  var movies: List[Movie] = List(
    Movie(1, "The Shawshank Redemption", 142, "R"),
    Movie(2, "The Godfather", 175, "R")
  )

  var showtimes: List[Showtime] = List(
    Showtime(1, 1, "2024-03-01T19:00:00Z", "Theater 1"),
    Showtime(2, 2, "2024-03-01T20:00:00Z", "Theater 2")
  )
}
