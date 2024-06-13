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
	UserID int64
	Score  int
}

// NewDB creates a new database connection
func NewDB(dsn string) (*DB, error) {
	mdb, err := sql.Open("mysql", dsn)
	if err != nil {
		return nil, logError("error opening database", err)
	}
	err = db.Ping()
	if err != nil {
		return nil, logError("error verifying connection", err)
	}
	log.Println("Connected to the database successfully")
	ret := &DB{mdb}
	db = ret

	return ret, nil
}

// InsertRecord inserts a record into the database
func (db *DB) InsertRecord(user User) (int64, error) {
	insertQuery := "INSERT INTO users (Userid, Score) VALUES (?, ?)"
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
	updateQuery := "UPDATE users SET Score = ? WHERE Userid = ?"
	_, err := db.Exec(updateQuery, user.Score, user.UserID)
	if err != nil {
		return logError("error updating record", err)
	}
	log.Println("Record updated successfully")
	return nil
}

// DeleteRecord deletes a record from the database
func (db *DB) DeleteRecord(userID int64) error {
	deleteQuery := "DELETE FROM users WHERE Userid = ?"
	_, err := db.Exec(deleteQuery, userID)
	if err != nil {
		return logError("error deleting record", err)
	}
	log.Println("Record deleted successfully")
	return nil
}

// QueryAllRecords queries all records from the database
func (db *DB) QueryAllRecords() ([]User, error) {
	query := "SELECT Userid, Score FROM users"
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
