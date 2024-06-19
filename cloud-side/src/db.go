package main

import (
	"database/sql"
	"log"

	_ "github.com/go-sql-driver/mysql"
)

// DB wraps around sql.DB and provides methods for database operations
type DB struct {
	*sql.DB
}

var db *DB

// User represents a user with a UserID and Score
type User struct {
	UserID int64 `json:"user_id"`
	Score  int   `json:"score"`
}

// NewDB creates a new database connection
func NewDB(dsn string) (*DB, error) {
	// Open a database connection
	mdb, err := sql.Open("mysql", dsn)
	if err != nil {
		return nil, logError("error opening database", err)
	}
	// Verify the database connection
	//err = mdb.Ping()
	//if err != nil {
	//		mdb.Close() // Close the connection before returning nil
	//		return nil, logError("error verifying connection", err)
	//	}

	log.Println("Connected to the database successfully")

	// Create a new DB instance and assign it to the global db variable
	ret := &DB{mdb}

	err = ret.createDatabaseIfNotExists()
	if err != nil {
		return nil, err
	}

	err = ret.createTableIfNotExists()
	if err != nil {
		return nil, err
	}

	db = ret
	return ret, nil
}

// createDatabaseIfNotExists creates the database if it does not already exist
func (db *DB) createDatabaseIfNotExists() error {
	_, err := db.Exec("CREATE DATABASE IF NOT EXISTS " + DBName)
	if err != nil {
		return logError("error creating database", err)
	}
	log.Println("Database created or already exists")
	return nil
}

// createTableIfNotExists creates the tetris table if it does not exist
func (db *DB) createTableIfNotExists() error {
	createTableQuery := `
	CREATE TABLE IF NOT EXISTS Tetris (
		ID BIGINT PRIMARY KEY,
		Score INT
	)`
	_, err := db.Exec(createTableQuery)
	if err != nil {
		return logError("error creating table", err)
	}
	log.Println("Checked for table and created if not exists")
	return nil
}

// InsertRecord inserts a record into the database
func (db *DB) InsertRecord(user User) (int64, error) {
	insertQuery := "INSERT INTO Tetris (ID, Score) VALUES (?, ?)"
	result, err := db.Exec(insertQuery, user.UserID, user.Score)
	if err != nil {
		return 0, logError("error inserting record", err)
	}
	insertedID, err := result.LastInsertId()
	if err != nil {
		return 0, logError("error getting last insert ID", err)
	}
	log.Printf("Record inserted successfully with ID %d\n", insertedID)
	return insertedID, nil
}

// UpdateRecord updates a record in the database
func (db *DB) UpdateRecord(user User) error {
	updateQuery := "UPDATE Tetris SET Score = ? WHERE ID = ?"
	_, err := db.Exec(updateQuery, user.Score, user.UserID)
	if err != nil {
		return logError("error updating record", err)
	}
	log.Println("Record updated successfully")
	return nil
}

// DeleteRecord deletes a record from the database
func (db *DB) DeleteRecord(userID int64) error {
	deleteQuery := "DELETE FROM Tetris WHERE ID = ?"
	_, err := db.Exec(deleteQuery, userID)
	if err != nil {
		return logError("error deleting record", err)
	}
	log.Println("Record deleted successfully")
	return nil
}

// QueryAllRecords queries all records from the database
func (db *DB) QueryAllRecords() ([]User, error) {
	log.Printf("query called")

	query := "SELECT ID, Score FROM Tetris"
	rows, err := db.Query(query)
	if err != nil {
		return nil, logError("error querying all records", err)
	}
	defer rows.Close()

	var records []User
	for rows.Next() {
		var user User
		err := rows.Scan(&user.UserID, &user.Score)
		if err != nil {
			return nil, logError("error scanning row", err)
		}
		records = append(records, user)
	}
	if err = rows.Err(); err != nil {
		return nil, logError("error with rows", err)
	}
	return records, nil
}

func logError(message string, err error) error {
	log.Printf("%s: %v", message, err)
	return err
}
