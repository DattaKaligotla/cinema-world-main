package com.example.cinemaworld

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

  // Implement additional methods as needed
}
