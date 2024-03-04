
DO
$do$
DECLARE
   r RECORD;
BEGIN
   FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = current_schema()) LOOP
      EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';
   END LOOP;
END
$do$;

CREATE TABLE movies (
    movie_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    duration INT NOT NULL,
    rating VARCHAR(10)
);

CREATE TABLE showtimes (
    showtime_id SERIAL PRIMARY KEY,
    movie_id INT NOT NULL,
    start_time VARCHAR(255) NOT NULL,
    theater VARCHAR(255) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id)
);

CREATE TABLE reservations (
    reservation_id SERIAL PRIMARY KEY,
    showtime_id INT NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(showtime_id)
);
ALTER TABLE showtimes ADD COLUMN total_capacity INT;

ALTER TABLE reservations ADD COLUMN total_charge FLOAT DEFAULT 0;
ALTER TABLE reservations ADD COLUMN is_cancelled BOOLEAN DEFAULT FALSE;
