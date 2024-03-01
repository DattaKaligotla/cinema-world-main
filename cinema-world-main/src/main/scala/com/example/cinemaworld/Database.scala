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

  def bookTickets(showtimeId: Int, quantity: Int): Future[Boolean] = {
    // Assuming Reservation(id: Option[Int], showtimeId: Int, quantity: Int)
    // Adjust according to your actual Reservation case class and Reservations table schema
    val action = reservations.map(r =>
      (r.showtimeId, r.quantity)
    ) returning reservations.map(_.id) += (showtimeId, quantity)

    db.run(action)
      .map { case _ =>
        true // Successfully inserted
      }
      .recover { case _: Exception =>
        false // Failed to insert
      }
  }

  // Implement additional methods as needed
}
