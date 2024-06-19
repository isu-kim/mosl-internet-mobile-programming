package main
import (
	"fmt"
	"log"
	"time"
)

func main() {
	initAPIs()
	err := loadEnv()
	if err != nil {
		log.Fatalf("Failed to load ENV: %v", err)
	}

	// Database connection details
	dsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s", DBUser, DBPass, DBHost, DBPort, DBName)

	// Retry logic for database connection
	var db *DB
	retries := 10
	for i := 0; i < retries; i++ {
		db, err = NewDB(dsn)
		if err == nil {
			break // Connected successfully
		}
		log.Printf("Database connection failed (attempt %d): %v", i+1, err)
		time.Sleep(1 * time.Second) // Retry after 1 second
	}

	if err != nil {
		log.Fatalf("Failed to connect to database after %d retries: %v", retries, err)
	}

	defer func(db *DB) {
		err := db.Close()
		if err != nil {
			log.Printf("Failed to close DB")
		}
	}(db)

	runServer()
}

