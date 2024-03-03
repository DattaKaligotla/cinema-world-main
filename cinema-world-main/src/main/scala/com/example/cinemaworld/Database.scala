package com.example.cinemaworld
import scala.concurrent.ExecutionContext.Implicits.global

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import scala.util.{Failure, Success}

object AppDatabase extends DatabaseSchema {
  // Define your connection details directly
  val db = Database.forURL(
    url = "jdbc:postgresql://localhost:5432/cinema_world",
    user = "postgres",
    password = "fourarms",
    driver = "org.postgresql.Driver"
  )

  def listAllMovies(): Future[Seq[Movie]] = db.run(movies.result)
  def addMovie(movie: Movie): Future[Int] = {
    val action = (movies returning movies.map(_.movieId)) += movie
    db.run(action)
  }

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

  def addShowtime(showtime: Showtime): Future[String] = {
    val movieExistsQuery = movies.filter(_.movieId === showtime.movieId).exists.result

    val action = for {
      movieExists <- movieExistsQuery
      result <- if (movieExists) {
        (showtimes += showtime).map(_ => "Showtime added successfully")
      } else {
        DBIO.successful("Movie ID does not exist")
      }
    } yield result

    db.run(action.transactionally).recover {
      case ex: Exception => s"Failed to add showtime: ${ex.getMessage}"
    }
  }

  def getAllReservations(): Future[Seq[Reservation]] = {
    db.run(reservations.result)
  }

  def addReservation(newReservation: Reservation): Future[String] = {
    val showtimeQuery = showtimes.filter(_.showtime_id === newReservation.showtimeId).result.headOption
    val reservationsQuery = reservations.filter(_.showtimeId === newReservation.showtimeId).result

    val action: DBIO[String] = for {
      showtimeOption <- showtimeQuery
      existingReservations <- reservationsQuery
    } yield {
      showtimeOption match {
        case Some(showtime) if existingReservations.map(_.quantity).sum + newReservation.quantity <= showtime.totalCapacity =>
          // Assuming you have a way to actually insert and return a meaningful message
          reservations += newReservation.copy(totalCharge = newReservation.quantity * 10) // Adjust as necessary for actual insertion
          "Reservation added successfully"
        case Some(_) => "Capacity exceeded"
        case None => "Showtime not found"
      }
    }

    db.run(action.transactionally.asTry).map {
      case Success(result) => result
      case Failure(ex) => s"Failed to add reservation: ${ex.getMessage}"
    }
  }
  def cancelReservation(reservationId: Int): Future[String] = {
    val currentTime = LocalDateTime.now()
    val findReservationQuery = reservations.filter(_.reservationId === reservationId).result.headOption
    val action = findReservationQuery.flatMap {
      case Some(reservation) =>
        val showtimeQuery = showtimes.filter(_.showtime_id === reservation.showtimeId).result.headOption
        showtimeQuery.flatMap {
          case Some(showtime) =>
            val showtimeStart = LocalDateTime.parse(showtime.startTime) // Assuming startTime is properly formatted
            if (ChronoUnit.HOURS.between(currentTime, showtimeStart) < 24) {
              val updatedReservation = reservation.copy(totalCharge = 3, isCancelled = true) // Apply penalty
              reservations.insertOrUpdate(updatedReservation).map(_ => "Reservation cancelled with penalty")
            } else {
              val updatedReservation = reservation.copy(isCancelled = true) // No penalty
              reservations.insertOrUpdate(updatedReservation).map(_ => "Reservation cancelled without penalty")
            }
          case None => DBIO.successful("Showtime not found")
        }
      case None => DBIO.successful("Reservation not found")
    }

    db.run(action.transactionally).recover {
      case ex: Exception => s"Failed to cancel reservation: ${ex.getMessage}"
    }
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
