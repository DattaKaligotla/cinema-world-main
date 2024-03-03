package com.example.cinemaworld

import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.RootJsonFormat

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  
  implicit val movieFormat: RootJsonFormat[Movie] = jsonFormat4(Movie)
  implicit val showtimeFormat: RootJsonFormat[Showtime] = jsonFormat5(Showtime) // Adjust number based on Showtime parameters
  implicit val reservationFormat: RootJsonFormat[Reservation] = jsonFormat6(Reservation) // Adjust number based on Reservation parameters*/

}
