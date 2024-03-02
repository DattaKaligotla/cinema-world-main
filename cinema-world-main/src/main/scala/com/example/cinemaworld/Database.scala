package com.example.cinemaworld
import scala.concurrent.ExecutionContext.Implicits.global

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

object AppDatabase extends DatabaseSchema {
  // Define your connection details directly
  val db = Database.forURL(
    url = "jdbc:postgresql://localhost:5432/cinema_world",
    user = "postgres",
    password = "fourarms",
    driver = "org.postgresql.Driver"
  )

  def listAllMovies(): Future[Seq[Movie]] = db.run(movies.result)

  def getMovieDetailsById(movieId: Int): Future[Option[Movie]] = {
    val query = movies.filter(_.movieId === movieId).result.headOption
    db.run(query)
  }

  def getAllShowtimes: Future[Seq[Showtime]] = db.run(showtimes.result)

  def getShowtimeById(showtimeId: Int): Future[Option[Showtime]] = {
    val query = showtimes.filter(_.showtime_id === showtimeId).result.headOption
    db.run(query)
  }

  def getShowtimesByMovieId(movieId: Int): Future[Seq[Showtime]] = {
    val query = showtimes.filter(_.movieId === movieId).result
    db.run(query)
  }

  def addShowtime(showtime: Showtime): Future[Int] = {
    val insertAction = showtimes += showtime
    db.run(insertAction)
  }
  def getAllReservations(): Future[Seq[Reservation]] = {
    db.run(reservations.result)
  }

  // Create a new reservation
  def addReservation(newReservation: Reservation): Future[Int] = {
    db.run(reservations returning reservations.map(_.reservationId) += newReservation)
  }

  // Fetch a reservation by its ID
  def getReservationById(reservationId: Int): Future[Option[Reservation]] = {
    db.run(reservations.filter(_.reservationId === reservationId).result.headOption)
  }

  // Fetch reservations by showtime ID
  def getReservationsByShowtime(showtimeId: Int): Future[Seq[Reservation]] = {
    db.run(reservations.filter(_.showtimeId === showtimeId).result)
  }
 
}

  



  // Implement additional methods as needed
