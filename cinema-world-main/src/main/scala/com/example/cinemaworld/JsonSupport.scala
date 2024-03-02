package com.example.cinemaworld

import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val movieFormat = jsonFormat4(Movie)
  implicit val showtimeFormat = jsonFormat4(Showtime)
  implicit val reservationFormat = jsonFormat4(Reservation)
}
