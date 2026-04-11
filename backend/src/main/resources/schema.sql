-- Voting System Database Schema
-- Run this script to initialize the database

CREATE DATABASE IF NOT EXISTS voting_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE voting_db;

-- 1. Voters Table
CREATE TABLE IF NOT EXISTS voters (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    national_id VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    profile_picture VARCHAR(500),
    role ENUM('ADMIN', 'VOTER') NOT NULL DEFAULT 'VOTER',
    status ENUM('PENDING', 'ACTIVE', 'DEACTIVATED') NOT NULL DEFAULT 'PENDING',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expires DATETIME,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_voters_email (email),
    INDEX idx_voters_status (status),
    INDEX idx_voters_role (role)
    );

-- 2. Elections Table
CREATE TABLE IF NOT EXISTS elections (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    results_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES voters(id) ON DELETE RESTRICT,
    INDEX idx_elections_active (is_active),
    INDEX idx_elections_start_time (start_time),
    INDEX idx_elections_end_time (end_time)
    );

-- 3. Election Categories Table
CREATE TABLE IF NOT EXISTS election_categories (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   election_id BIGINT NOT NULL,
                                                   category_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (election_id) REFERENCES elections(id) ON DELETE CASCADE,
    INDEX idx_election_categories_election_id (election_id)
    );

-- 4. Candidates Table
CREATE TABLE IF NOT EXISTS candidates (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          full_name VARCHAR(255) NOT NULL,
    party VARCHAR(255),
    bio TEXT,
    photo_url VARCHAR(500),
    election_category_id BIGINT NOT NULL,
    FOREIGN KEY (election_category_id) REFERENCES election_categories(id) ON DELETE CASCADE,
    INDEX idx_candidates_election_category_id (election_category_id)
    );

-- 5. Votes Table (unique constraint prevents double voting)
CREATE TABLE IF NOT EXISTS votes (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     voter_id BIGINT NOT NULL,
                                     candidate_id BIGINT NOT NULL,
                                     election_category_id BIGINT NOT NULL,
                                     voted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (voter_id) REFERENCES voters(id) ON DELETE RESTRICT,
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE RESTRICT,
    FOREIGN KEY (election_category_id) REFERENCES election_categories(id) ON DELETE RESTRICT,
    UNIQUE KEY uk_voter_election_category (voter_id, election_category_id),
    INDEX idx_votes_voter_id (voter_id),
    INDEX idx_votes_candidate_id (candidate_id),
    INDEX idx_votes_election_category_id (election_category_id)
    );

-- 6. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             voter_id BIGINT NOT NULL,
                                             message TEXT NOT NULL,
                                             is_read BOOLEAN NOT NULL DEFAULT FALSE,
                                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             FOREIGN KEY (voter_id) REFERENCES voters(id) ON DELETE CASCADE,
    INDEX idx_notifications_voter_id (voter_id),
    INDEX idx_notifications_is_read (is_read)
    );

-- 7. Announcements Table
CREATE TABLE IF NOT EXISTS announcements (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES voters(id) ON DELETE RESTRICT,
    INDEX idx_announcements_created_at (created_at)
    );

-- 8. Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          user_id BIGINT,
                                          action VARCHAR(255) NOT NULL,
    details TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES voters(id) ON DELETE SET NULL,
    INDEX idx_audit_logs_user_id (user_id),
    INDEX idx_audit_logs_action (action),
    INDEX idx_audit_logs_timestamp (timestamp)
    );

-- Default Admin Account
-- Password: Admin@123 (BCrypt hashed)
INSERT INTO voters (full_name, email, password, national_id, phone, address, role, status, email_verified, created_at)
VALUES (
           'System Administrator',
           'admin@votingsystem.com',
           '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
           'ADMIN-001',
           '+1234567890',
           'System Address',
           'ADMIN',
           'ACTIVE',
           TRUE,
           NOW()
       ) ON DUPLICATE KEY UPDATE id=id;