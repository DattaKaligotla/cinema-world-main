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
    startTime: String, // Assuming you'll handle the conversion from String to LocalDateTime elsewhere
    theater: String,
    totalCapacity: Int // New field
)

case class Reservation(reservationId: Option[Int], showtimeId: Int, customerName: String, quantity: Int, totalCharge: Float, isCancelled: Boolean)


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
    def totalCapacity = column[Int]("total_capacity") // New column for total capacity
    override def * = (showtime_id.?, movieId, startTime, theater, totalCapacity) <> (Showtime.tupled, Showtime.unapply)
  }

  class Reservations(tag: Tag) extends Table[Reservation](tag, "reservations") {
    def reservationId: Rep[Int] = column[Int]("reservation_id", O.PrimaryKey, O.AutoInc)
    def showtimeId: Rep[Int] = column[Int]("showtime_id")
    def customerName: Rep[String] = column[String]("customer_name") // Add this line
    def quantity: Rep[Int] = column[Int]("quantity")
    def showtimeFK: ForeignKeyQuery[Showtimes, Showtime] =
      foreignKey("showtime_fk", showtimeId, TableQuery[Showtimes])(_.showtime_id)

    def isCancelled = column[Boolean]("is_cancelled") // New column for cancellation status
    def totalCharge = column[Float]("total_charge")
  // Ensure the type here matches the case class
    override def * = (reservationId.?, showtimeId, customerName, quantity, totalCharge, isCancelled) <> ((Reservation.apply _).tupled, Reservation.unapply)
  }


  val movies = TableQuery[Movies]
  val showtimes = TableQuery[Showtimes]
  val reservations = TableQuery[Reservations]
}

object DBSchema extends DatabaseSchema