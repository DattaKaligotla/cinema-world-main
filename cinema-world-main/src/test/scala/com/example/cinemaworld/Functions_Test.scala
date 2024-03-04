package com.example.cinemaworld

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AsyncFunSuite
import slick.jdbc.H2Profile.api._
import java.time.LocalDateTime


class AppDatabaseTests extends AsyncFunSuite with BeforeAndAfter {
  implicit val ec: ExecutionContext = executionContext

  // Assuming `db` is your test database instance, properly configured for tests
  val db = AppDatabase.db // Utilize the database instance from AppDatabase directly

  before {
    // Setup database (create schema)
    val setupActions = DBIO.seq(
      // Use the schema definitions from AppDatabase directly
      AppDatabase.movies.schema.create,
      AppDatabase.showtimes.schema.create,
      AppDatabase.reservations.schema.create
    )
    db.run(setupActions).failed.foreach(e => fail(s"Failed setup with error: $e"))
  }

  after {
    // Cleanup database (drop schema)
    val cleanupActions = DBIO.seq(
      AppDatabase.reservations.schema.drop,
      AppDatabase.showtimes.schema.drop,
      AppDatabase.movies.schema.drop
    )
    db.run(cleanupActions).failed.foreach(e => fail(s"Failed cleanup with error: $e"))
  }
  test("Add a movie and verify it is added") {
    val movie = Movie(None, "Interstellar", 169, "PG-13")
    for {
      movieId <- AppDatabase.addMovie(movie)
      fetchedMovie <- AppDatabase.getMovieDetailsById(movieId)
    } yield {
      assert(fetchedMovie.isDefined)
      assert(fetchedMovie.get.title == movie.title)
    }
  }

 
  test("Add a showtime for an existing movie and verify") {
  for {
    movieId <- AppDatabase.addMovie(Movie(None, "Dunkirk", 106, "PG-13"))
    showtime = Showtime(None, movieId, "2024-12-15T20:00", "IMAX", 200)
    showtimeId <- AppDatabase.addShowtime(showtime)
    fetchedShowtime <- AppDatabase.getShowtimeById(showtimeId) // Assuming this returns Future[Option[Showtime]]
  } yield {
    assert(fetchedShowtime.isDefined)
    assert(fetchedShowtime.get.theater == showtime.theater)
  }
}


  test("Add a reservation for an existing showtime and verify") {
    for {
      movieId <- AppDatabase.addMovie(Movie(None, "Tenet", 150, "PG-13"))
      showtimeId <- AppDatabase.addShowtime(Showtime(None, movieId, "2024-12-20T18:00", "Main Hall", 150))
      reservation = Reservation(None, showtimeId, "Jane Doe", 2, 40, false)
      reservationId <- AppDatabase.addReservation(reservation)
      fetchedReservation <- AppDatabase.getReservationById(reservationId)
    } yield {
      assert(fetchedReservation.isDefined)
      assert(fetchedReservation.get.customerName == reservation.customerName)
    }
  }

  test("Attempt to add a showtime for a non-existent movie") {
    val showtime = Showtime(None, 999, "2025-01-01T12:00", "Small Room", 50)
    recoverToSucceededIf[Exception] {
      AppDatabase.addShowtime(showtime)
    }
  }

  test("Verify cancellation of a reservation applies penalty if within 24 hours") {
    for {
      movieId <- AppDatabase.addMovie(Movie(None, "Inception", 148, "PG-13"))
      now = LocalDateTime.now
      showtimeId <- AppDatabase.addShowtime(Showtime(None, movieId, now.plusDays(1).minusHours(1).toString, "VIP", 100))
      reservationId <- AppDatabase.addReservation(Reservation(None, showtimeId, "Alice Bob", 1, 10, false))
      cancellationMessage <- AppDatabase.cancelReservation(reservationId)
    } yield {
      assert(cancellationMessage.contains("penalty"))
    }
  }
  test("Add multiple movies and verify they are added") {
    val movies = Seq(
      Movie(None, "Interstellar", 169, "PG-13"),
      Movie(None, "The Prestige", 130, "PG-13"),
      Movie(None, "Memento", 113, "R")
    )

    val addedMovies: Future[Seq[Option[Movie]]] = Future.sequence(movies.map(AppDatabase.addMovie).map(_.flatMap(AppDatabase.getMovieDetailsById)))

    addedMovies.map { movieOptions =>
      assert(movieOptions.forall(_.isDefined))
      assert(movieOptions.map(_.get.title).toSet == movies.map(_.title).toSet)
    }
  }

  test("Verify capacity limitations when adding reservations") {
    for {
      movieId1 <- AppDatabase.addMovie(Movie(None, "Dunkirk", 106, "PG-13"))
      movieId2 <- AppDatabase.addMovie(Movie(None, "Inception", 148, "PG-13"))
      showtimeId1 <- AppDatabase.addShowtime(Showtime(None, movieId1, "2024-12-15T14:00", "IMAX", 100))
      showtimeId2 <- AppDatabase.addShowtime(Showtime(None, movieId2, "2024-12-15T18:00", "Standard", 50))
      reservationAttempt1 = AppDatabase.addReservation(Reservation(None, showtimeId1, "Alice", 101, 0, false)) // Exceeds capacity
      reservationAttempt2 = AppDatabase.addReservation(Reservation(None, showtimeId2, "Bob", 50, 0, false)) // Exactly at capacity
      result1 <- reservationAttempt1.failed
      result2 <- reservationAttempt2
    } yield {
      assert(result1.isInstanceOf[Exception])
      assert(result2 > 0) // Verifies reservationId is a valid, positive integer
    }
  }


  test("Attempt to add a showtime with invalid movie ID and verify failure") {
    val invalidShowtime = Showtime(None, 99999, "2025-01-01T12:00", "Small Room", 50)
    AppDatabase.addShowtime(invalidShowtime).map { result =>
      fail("This operation should have failed with an exception.")
    }.recover {
      case ex: Exception if ex.getMessage.contains("Movie ID does not exist") => 
        succeed // This is the expected outcome, so the test should pass.
      case _ => 
        fail("An unexpected exception occurred.")
    }
  }



  test("Verify correct penalty application for late cancellation") {
    for {
      movieId <- AppDatabase.addMovie(Movie(None, "Inception", 148, "PG-13"))
      showtimeId <- AppDatabase.addShowtime(Showtime(None, movieId, LocalDateTime.now.plusDays(1).minusHours(1).toString, "VIP", 100))
      reservationId <- AppDatabase.addReservation(Reservation(None, showtimeId, "Alice Bob", 1, 10, false))
      cancellationResult <- AppDatabase.cancelReservation(reservationId)
    } yield {
      assert(cancellationResult.contains("penalty"))
    }
  }

  test("Ensure multiple reservations do not exceed showtime capacity") {
    for {
      movieId <- AppDatabase.addMovie(Movie(None, "Tenet", 150, "PG-13"))
      showtimeId <- AppDatabase.addShowtime(Showtime(None, movieId, "2024-12-20T18:00", "Main Hall", 3))
      reservationId1 <- AppDatabase.addReservation(Reservation(None, showtimeId, "Customer One", 1, 10, false))
      reservationAttempt2 = AppDatabase.addReservation(Reservation(None, showtimeId, "Customer Two", 3, 30, false)) // This should fail due to capacity
      result2 <- reservationAttempt2.failed
    } yield {
      assert(reservationId1 > 0) // Verifies reservationId is a valid, positive integer
      assert(result2.isInstanceOf[Exception])
    }
  }
  test("Add 20 movies and verify they are added") {
  val movies = (1 to 20).map(i => Movie(None, s"Movie $i", 100 + i, if (i % 2 == 0) "PG-13" else "R"))
  val addedMoviesFuture = Future.sequence(movies.map(movie => AppDatabase.addMovie(movie).flatMap(AppDatabase.getMovieDetailsById)))

  addedMoviesFuture.map { addedMovies =>
    assert(addedMovies.forall(_.isDefined), "All movies should be successfully added and fetched")
    assert(addedMovies.map(_.get.title).toSet == movies.map(_.title).toSet, "All movie titles should match")
  }
}

test("Add 20 showtimes for a single movie and verify they are added") {
  for {
    movieId <- AppDatabase.addMovie(Movie(None, "Epic Saga", 120, "PG-13"))
    showtimes = (1 to 20).map(i => Showtime(None, movieId, s"2024-12-${15 + i % 2}T${10 + i}:00", if (i % 2 == 0) "IMAX" else "Standard", 100))
    addedShowtimeIds <- Future.sequence(showtimes.map(AppDatabase.addShowtime))
    fetchedShowtimes <- Future.sequence(addedShowtimeIds.map(AppDatabase.getShowtimeById))
  } yield {
    assert(fetchedShowtimes.forall(_.isDefined), "All showtimes should be successfully added and fetched")
    assert(fetchedShowtimes.flatten.map(_.theater).toSet == Set("IMAX", "Standard"), "Theater types should match")
  }
}

test("Ensure multiple reservations by the same customer do not exceed showtime capacity") {
  for {
    movieId <- AppDatabase.addMovie(Movie(None, "Multiplicity", 110, "PG-13"))
    showtimeId <- AppDatabase.addShowtime(Showtime(None, movieId, "2024-12-20T18:00", "Main Hall", 5))
    reservationResults <- Future.sequence((1 to 5).map(i => AppDatabase.addReservation(Reservation(None, showtimeId, "Clone Customer", 1, 10, false))))
  } yield {
    assert(reservationResults.forall(_ > 0), "All reservations by the same customer should be valid and within capacity")
  }
}

test("Add 20 reservations for different customers and verify they are processed correctly") {
  for {
    movieId <- AppDatabase.addMovie(Movie(None, "Crowded Show", 130, "PG-13"))
    showtimeId <- AppDatabase.addShowtime(Showtime(None, movieId, "2024-12-25T20:00", "Big Hall", 25))
    reservationAttempts = (1 to 20).map(i => Reservation(None, showtimeId, s"Customer $i", 1, 10, false))
    reservationResults <- Future.sequence(reservationAttempts.map(AppDatabase.addReservation))
  } yield {
    assert(reservationResults.forall(_ > 0), "All reservations should be valid and processed correctly")
  }
}





  // Similar structure for other tests...
}
