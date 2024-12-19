/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.librarymanagementsystem1;

/**
 *
 * @author vedan
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserAuth {
    private int loggedInUserId = -1;

    /**
     * Registers a new user in the database.
     * 
     * @param name  The name of the user.
     * @param email The email of the user.
     * @return true if registration is successful, false otherwise.
     */
    public boolean registerUser(String name, String email) {
        String query = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, name);
            pst.setString(2, email);
            pst.executeUpdate();
            System.out.println("Registration successful for: " + email);
            return true;
        } catch (Exception e) {
            System.out.println("Registration Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Logs in a user by checking their email in the database.
     * 
     * @param email The email of the user trying to log in.
     * @return true if login is successful, false otherwise.
     */
    public boolean loginUser(String email) {
        String query = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                loggedInUserId = rs.getInt("id");
                System.out.println("Login successful for user ID: " + loggedInUserId);
                return true;
            } else {
                System.out.println("Login failed: Email not found.");
            }
        } catch (Exception e) {
            System.out.println("Login Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Logs out the currently logged-in user by resetting the session.
     */
    public void logoutUser() {
        if (loggedInUserId != -1) {
            System.out.println("User with ID " + loggedInUserId + " logged out.");
        }
        loggedInUserId = -1; // Reset logged-in user ID
    }

    /**
     * Gets the currently logged-in user's ID.
     * 
     * @return The logged-in user's ID, or -1 if no user is logged in.
     */
    public int getLoggedInUserId() {
        return loggedInUserId;
    }
}
