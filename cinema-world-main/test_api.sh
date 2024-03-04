#!/bin/bash

# Set the base URL
BASE_URL="http://localhost:8080"

echo "Adding movies..."
curl -X POST "$BASE_URL/movies" -H "Content-Type: application/json" -d '{"title":"Inception","duration":148,"rating":"PG-13"}'
echo ""
curl -X POST "$BASE_URL/movies" -H "Content-Type: application/json" -d '{"title":"Interstellar","duration":169,"rating":"PG-13"}'
echo ""

echo "Listing all movies..."
curl "$BASE_URL/movies"
echo ""

echo "Adding showtime for Inception..."
curl -X POST "$BASE_URL/showtimes" -H "Content-Type: application/json" -d '{"movieId":1,"startTime":"2024-12-15T20:00","theater":"IMAX","totalCapacity":200}'
echo ""

echo "Adding showtime for Interstellar..."
curl -X POST "$BASE_URL/showtimes" -H "Content-Type: application/json" -d '{"movieId":2,"startTime":"2024-12-16T20:00","theater":"Standard","totalCapacity":150}'
echo ""

echo "Listing all showtimes..."
curl "$BASE_URL/showtimes"
echo ""

echo "Adding reservation for showtime ID 1..."
curl -X POST "$BASE_URL/reservations" -H "Content-Type: application/json" -d '{"showtimeId":1,"customerName":"John Doe","quantity":2,"totalCharge":40,"isCancelled":false}'
echo ""

echo "Listing all reservations..."
curl "$BASE_URL/reservations"
echo ""

echo "Cancelling reservation ID 1..."
curl -X POST "$BASE_URL/reservations/cancel/1"
echo ""
