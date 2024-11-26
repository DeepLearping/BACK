CREATE TABLE chat_history (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              session_id LONG NOT NULL,
                              message TEXT NOT NULL,
                              createdDate DATETIME DEFAULT CURRENT_TIMESTAMP
);