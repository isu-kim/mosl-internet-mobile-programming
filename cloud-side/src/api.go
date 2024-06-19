package main

import (
	"encoding/json"
	"github.com/gorilla/mux"
	"log"
	"net/http"
	"strconv"
)

var r *mux.Router

func initAPIs() {
	r = mux.NewRouter()

	// Define the endpoints
	r.HandleFunc("/user_info", func(w http.ResponseWriter, r *http.Request) {
		userInfoHandler(db, w, r)
	}).Methods("GET")

	r.HandleFunc("/upload_score", func(w http.ResponseWriter, r *http.Request) {
		uploadScoreHandler(db, w, r)
	}).Methods("POST")

	r.HandleFunc("/all_scores", func(w http.ResponseWriter, r *http.Request) {
		allScoresHandler(db, w, r)
	}).Methods("GET")
}

func userInfoHandler(db *DB, w http.ResponseWriter, r *http.Request) {
	userIDStr := r.URL.Query().Get("userid")
	if userIDStr == "" {
		http.Error(w, "userid is required", http.StatusBadRequest)
		return
	}

	userID, err := strconv.ParseInt(userIDStr, 10, 64)
	if err != nil {
		http.Error(w, "invalid userid", http.StatusBadRequest)
		return
	}

	users, err := db.QueryAllRecords()
	if err != nil {
		http.Error(w, "error querying user info: %v", http.StatusInternalServerError)
		return
	}

	for _, user := range users {
		if user.UserID == userID {
			json.NewEncoder(w).Encode(user)
			return
		}
	}

	log.Printf("Adding user %d, score %d", userID, 0)

	if userID > 0 && userID < 65536 {
		_, err := db.InsertRecord(User{
			UserID: userID,
			Score:  0,
		})

		log.Printf("New user %d added, score %d", userID, 0)

		if err != nil {
			http.Error(w, "unable to add user", http.StatusInternalServerError)
			return
		}
	}

	users, err = db.QueryAllRecords()
	if err != nil {
		http.Error(w, "error querying user info: %v", http.StatusInternalServerError)
		return
	}

	for _, user := range users {
		if user.UserID == userID {
			json.NewEncoder(w).Encode(user)
			return
		}
	}

	http.Error(w, "user not found", http.StatusNotFound)
}

func uploadScoreHandler(db *DB, w http.ResponseWriter, r *http.Request) {
	var user User
	err := json.NewDecoder(r.Body).Decode(&user)
	if err != nil {
		http.Error(w, "invalid request payload", http.StatusBadRequest)
		return
	}

	err = db.UpdateRecord(user)
	if err != nil {
		http.Error(w, "error updating score", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(map[string]string{"message": "score uploaded successfully"})
}

func allScoresHandler(db *DB, w http.ResponseWriter, r *http.Request) {
	users, err := db.QueryAllRecords()
	if err != nil {
		http.Error(w, "error querying all scores", http.StatusInternalServerError)
		return
	}

	json.NewEncoder(w).Encode(users)
}

func runServer() {
	// Start the server
	log.Println("Server is running on port 8080")
	err := http.ListenAndServe("0.0.0.0:8080", r)
	if err != nil {
		return
	}
}
