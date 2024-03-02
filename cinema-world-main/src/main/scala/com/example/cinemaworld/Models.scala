package com.example.cinemaworld
import java.time.LocalDateTime

import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}

case class Movie(
    movieId: Option[Int],
    title: String,
    duration: Int,
    rating: String
)
case class Showtime(
    id: Option[Int],
    movieId: Int,
    startTime: String, // Changed to LocalDateTime
    theater: String
)

case class Reservation(id: Option[Int], showtimeId: Int, quantity: Int)

trait DatabaseSchema {
  class Movies(tag: Tag) extends Table[Movie](tag, "movies") {
    def movieId = column[Int](
      "movie_id",
      O.PrimaryKey,
      O.AutoInc
    ) // Changed to non-optional
    def title = column[String]("title")
    def duration = column[Int]("duration")
    def rating = column[String]("rating")

    // Define the projection mapping for the * method
    def * = (
      movieId.?,
      title,
      duration,
      rating
    ) <> (Movie.tupled, Movie.unapply) // Adapted constructor for optional ID
  }

  class Showtimes(tag: Tag) extends Table[Showtime](tag, "showtimes") {
    def showtime_id = column[Int]("showtime_id", O.PrimaryKey, O.AutoInc)
    def movieId = column[Int]("movie_id")
    def startTime = column[String]("start_time")
    def theater = column[String]("theater")

    def movieFK: ForeignKeyQuery[Movies, Movie] =
      foreignKey("movie_fk", movieId, TableQuery[Movies])(
        _.movieId
      ) // Corrected to _.movieId
    override def * =
      (showtime_id.?, movieId, startTime, theater) <> (Showtime.tupled, Showtime.unapply)
  }

  class Reservations(tag: Tag) extends Table[Reservation](tag, "reservations") {
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def showtimeId: Rep[Int] = column[Int]("showtime_id")
    def quantity: Rep[Int] = column[Int]("quantity")
    def showtimeFK: ForeignKeyQuery[Showtimes, Showtime] =
      foreignKey("showtime_fk", showtimeId, TableQuery[Showtimes])(_.showtime_id)

    override def * : ProvenShape[Reservation] =
      (id.?, showtimeId, quantity) <> (Reservation.tupled, Reservation.unapply)
  }

  val movies = TableQuery[Movies]
  val showtimes = TableQuery[Showtimes]
  val reservations = TableQuery[Reservations]
}

object DBSchema extends DatabaseSchema