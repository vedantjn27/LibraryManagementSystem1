/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.librarymanagementsystem1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class Library {
    
    

    
       
    public void addBook(int bookId, String title, String author) {
        String sql = "INSERT INTO books (book_id, title, author, isAvailable) VALUES (?, ?, ?, TRUE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.executeUpdate();

            showPanelMessage("Book added successfully!", "Success", true);
        } catch (SQLException e) {
            showPanelMessage("Error: " + e.getMessage(), "Error", false);
        }
    }

    public void displayBooks() {
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Table model to display book data
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Book ID");
            tableModel.addColumn("Title");
            tableModel.addColumn("Author");
            tableModel.addColumn("Available");

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("isAvailable") ? "Yes" : "No"
                });
            }

            // Display table in a JScrollPane
            JTable table = new JTable(tableModel);
            table.setBackground(new Color(235, 235, 255)); // Light blue background
            table.setSelectionBackground(new Color(0, 123, 255)); // Blue selection
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 2)); // Blue border

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.setBackground(new Color(245, 245, 245)); // Light gray background
            showCustomPanel(panel, "Books List");
        } catch (SQLException e) {
            showPanelMessage("Error: " + e.getMessage(), "Error", false);
        }
    }

    public void borrowBook(int bookId, int userId) {
        String checkSql = "SELECT isAvailable FROM books WHERE book_id = ?";
        String borrowSql = "INSERT INTO borrowed_books (book_id, user_id, dueDate, returnDate) VALUES (?, ?, ?, NULL)";
        String updateBookSql = "UPDATE books SET isAvailable = FALSE WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement borrowStmt = conn.prepareStatement(borrowSql);
             PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql)) {

            // Check if the book is available
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getBoolean("isAvailable")) {
                String dueDate = calculateDueDate(14); // 14 days from today

                // Insert a new borrow record
                borrowStmt.setInt(1, bookId);
                borrowStmt.setInt(2, userId);
                borrowStmt.setString(3, dueDate);
                borrowStmt.executeUpdate();

                // Update the book's availability
                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();

                showPanelMessage("Book borrowed successfully! Due Date: " + dueDate, "Success", true);
            } else {
                showPanelMessage("Book is currently unavailable.", "Unavailable", false);
            }
        } catch (SQLException e) {
            showPanelMessage("Error: " + e.getMessage(), "Error", false);
        }
    }

    public void returnBook(int bookId, int userId) {
        String checkSql = "SELECT dueDate, returnDate FROM borrowed_books WHERE book_id = ? AND user_id = ? ORDER BY id DESC LIMIT 1";
        String updateBorrowedSql = "UPDATE borrowed_books SET returnDate = ? WHERE book_id = ? AND user_id = ?";
        String updateBookSql = "UPDATE books SET isAvailable = TRUE WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement updateBorrowedStmt = conn.prepareStatement(updateBorrowedSql);
             PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql)) {

            // Check if the book has been borrowed by the user
            checkStmt.setInt(1, bookId);
            checkStmt.setInt(2, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String dueDate = rs.getString("dueDate");
                String returnDate = rs.getString("returnDate");

                if (returnDate != null) {
                    showPanelMessage("Book already returned on: " + returnDate, "Info", true);
                    return;
                }

                // Calculate late days
                String currentDate = getCurrentDate();
                int lateDays = calculateLateDays(dueDate, currentDate);
                if (lateDays > 0) {
                    showPanelMessage("You are " + lateDays + " days late. Penalty: ₹" + (lateDays * 50), "Late Return", false);
                } else {
                    showPanelMessage("Book returned on time. No penalty!", "Success", true);
                }

                // Update return date and book availability
                updateBorrowedStmt.setString(1, currentDate);
                updateBorrowedStmt.setInt(2, bookId);
                updateBorrowedStmt.setInt(3, userId);
                updateBorrowedStmt.executeUpdate();

                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();

                showPanelMessage("Book returned successfully! Return Date: " + currentDate, "Success", true);
            } else {
                showPanelMessage("No borrowed record found for this book and user.", "Not Found", false);
            }
        } catch (SQLException e) {
            showPanelMessage("Error: " + e.getMessage(), "Error", false);
        }
    }

    public void addFeedback(int bookId, String feedback) {
        String sql = "INSERT INTO feedback (book_id, feedback) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setString(2, feedback);
            pstmt.executeUpdate();
            showPanelMessage("Feedback added successfully!", "Success", true);
        } catch (SQLException e) {
            showPanelMessage("Error: " + e.getMessage(), "Error", false);
        }
    }

    public void viewFeedback(int bookId) {
        String sql = "SELECT feedback FROM feedback WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            JTextArea textArea = new JTextArea(10, 30);
            textArea.setEditable(false);
            textArea.setBackground(new Color(240, 240, 255)); // Light background for text area
            textArea.setFont(new Font("Arial", Font.PLAIN, 14));

            while (rs.next()) {
                textArea.append(rs.getString("feedback") + "\n");
            }

            JScrollPane scrollPane = new JScrollPane(textArea);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.setBackground(new Color(245, 245, 245)); // Light gray background
            showCustomPanel(panel, "Feedback for Book ID: " + bookId);
        } catch (SQLException e) {
            showPanelMessage("Error: " + e.getMessage(), "Error", false);
        }
    }
    public void removeBook(int bookId) {
    String sql = "DELETE FROM books WHERE book_id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, bookId);

        int rowsAffected = pstmt.executeUpdate(); // Execute the DELETE query

        if (rowsAffected > 0) {
            showPanelMessage("Book removed successfully!", "Success", true);
        } else {
            showPanelMessage("Book not found.", "Error", false);
        }
    } catch (SQLException e) {
        showPanelMessage("Error: " + e.getMessage(), "Error", false);
    }
}

    public void viewBorrowedBooks() {
    String sql = "SELECT book_id, user_id, dueDate, returnDate FROM borrowed_books"; // Adjust table/columns as per your database
    DefaultTableModel model = new DefaultTableModel();
    JTable borrowedBooksTable = new JTable(model);

    // Add column names
    model.addColumn("Book ID");
    model.addColumn("User ID");
    model.addColumn("Due Date");
    model.addColumn("Return Date");

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        // Populate table model with data from database
        while (rs.next()) {
            int bookId = rs.getInt("book_id");
            int userId = rs.getInt("user_id");
            String dueDate = rs.getString("dueDate");
            String returnDate = rs.getString("returnDate");
            model.addRow(new Object[]{bookId, userId, dueDate, returnDate});
        }

        // Show the table in a scrollable pane
        if (model.getRowCount() > 0) {
            JScrollPane scrollPane = new JScrollPane(borrowedBooksTable);
            scrollPane.setPreferredSize(new Dimension(500, 200));
            JOptionPane.showMessageDialog(null, scrollPane, "Borrowed Books", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No borrowed books found.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    public void viewAllUsers() {
    String sql = "SELECT id, name, email FROM users"; // Assuming 'users' table and columns
    DefaultTableModel model = new DefaultTableModel();
    JTable userTable = new JTable(model);

    // Add column names
    model.addColumn("User ID");
    model.addColumn("Name");
    model.addColumn("Email");

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        // Populate table model with data from database
        while (rs.next()) {
            int userId = rs.getInt("id");
            String name = rs.getString("name");
            String email = rs.getString("email");
            model.addRow(new Object[]{userId, name, email});
        }

        // Show the table in a scrollable pane
        if (model.getRowCount() > 0) {
            JScrollPane scrollPane = new JScrollPane(userTable);
            scrollPane.setPreferredSize(new Dimension(400, 200));
            JOptionPane.showMessageDialog(null, scrollPane, "All Users", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No users found.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private String calculateDueDate(int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        long dueMillis = now.getTime() + (days * 24L * 60 * 60 * 1000);
        return sdf.format(new Date(dueMillis));
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    private int calculateLateDays(String dueDate, String returnDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date due = sdf.parse(dueDate);
            Date returned = sdf.parse(returnDate);
            long diff = returned.getTime() - due.getTime();
            return (int) (diff / (24 * 60 * 60 * 1000));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void showPanelMessage(String message, String title, boolean isSuccess) {
        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBackground(new Color(245, 245, 245));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textArea, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createLineBorder(isSuccess ? new Color(0, 200, 0) : new Color(200, 0, 0), 2));

        JOptionPane.showMessageDialog(null, panel, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void showCustomPanel(JPanel panel, String title) {
        JOptionPane.showMessageDialog(null, panel, title, JOptionPane.PLAIN_MESSAGE);
    }
}
    