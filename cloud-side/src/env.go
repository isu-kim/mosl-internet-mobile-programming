package main

import (
	"errors"
	"fmt"
	"os"
)

var (
	DBHost string
	DBPort string
	DBName string
	DBUser string
	DBPass string
)

func loadEnv() error {
	DBHost = os.Getenv("DB_HOST")
	DBPort = os.Getenv("DB_PORT")
	DBName = os.Getenv("DB_NAME")
	DBUser = os.Getenv("DB_USER")
	DBPass = os.Getenv("DB_PASS")

	// Optionally, you can add checks to ensure the environment variables are set
	if DBHost == "" || DBPort == "" || DBName == "" || DBUser == "" || DBPass == "" {
		fmt.Println("Warning: One or more environment variables are not set")
		return errors.New("one or more environment variables are not set")
	}

	return nil
}
