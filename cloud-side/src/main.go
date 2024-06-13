package main

import (
	"fmt"
	"log"
)

func main() {
	initAPIs()
	err := loadEnv()
	if err != nil {
		log.Fatalf("Failed to load ENV: %v", err)
	}

	// Database connection details
	dsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s", DBUser, DBPass, DBHost, DBPort, DBName)

	// Connect to the database
	_, err = NewDB(dsn)
	if err != nil {
		log.Fatalf("Database connection failed: %v", err)
	}
	defer func(db *DB) {
		err := db.Close()
		if err != nil {
			log.Printf("Failed to close DB")
		}
	}(db)

	runServer()
}
