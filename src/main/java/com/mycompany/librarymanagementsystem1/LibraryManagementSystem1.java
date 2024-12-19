/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.librarymanagementsystem1;

/**
 *
 * @author vedan
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LibraryManagementSystem1 extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private UserAuth userAuth;
    private Library library;
    

    public LibraryManagementSystem1() {
        userAuth = new UserAuth();
        library = new Library();

        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Apply Nimbus Look and Feel for modern design
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add panels for login, registration, and main menu
        addLoginPanel();
    addUserMenuPanel();
    addAdminMenuPanel();


        add(mainPanel);
        setVisible(true);
    }

    private void addLoginPanel() {
        JPanel loginPanel = new JPanel(null); // Use null layout for precise positioning
        loginPanel.setBackground(new Color(240, 248, 255)); // Soft light blue background
        loginPanel.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 5)); // Blue border

        // Title label with a gradient background
        JLabel titleLabel = new JLabel("Welcome to the Library Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 51, 102));
        Dimension labelSize = titleLabel.getPreferredSize();
        titleLabel.setSize(labelSize.width, labelSize.height);
        titleLabel.setLocation(-labelSize.width, 50); // Start outside the screen (left side)
        loginPanel.add(titleLabel);

        // Timer for moving the label to the center
        Timer timer = new Timer(10, new ActionListener() {
            int targetX = (800 - titleLabel.getWidth()) / 2; // Target X for the center
            int currentX = -titleLabel.getWidth();          // Start position (off-screen)

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentX < targetX) {
                    currentX += 2; // Adjust speed here (higher = faster)
                    titleLabel.setLocation(currentX, titleLabel.getY());
                } else {
                    titleLabel.setLocation(targetX, titleLabel.getY()); // Snap to center
                    ((Timer) e.getSource()).stop(); // Stop the timer
                }
            }
        });
        timer.start();

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        buttonPanel.setBounds(200, 300, 400, 100); // Adjust size and position
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton loginButton = createStyledButton("Login", new Color(65, 105, 225));  // Royal blue
        JButton registerButton = createStyledButton("Register", new Color(255, 105, 180));  // Hot pink

        loginButton.addActionListener(e -> showLoginDialog());
        registerButton.addActionListener(e -> showRegisterDialog());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        loginPanel.add(buttonPanel);

        mainPanel.add(loginPanel, "LOGIN");
    }
    private void addAdminMenuPanel() {
    JPanel adminPanel = new JPanel(new BorderLayout());
    adminPanel.setBackground(new Color(255, 239, 213)); // Light peach background
    adminPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Header
    JLabel adminHeaderLabel = new JLabel("Admin Menu", SwingConstants.CENTER);
    adminHeaderLabel.setFont(new Font("Serif", Font.BOLD, 32));
    adminHeaderLabel.setForeground(new Color(128, 0, 0)); // Maroon color
    adminPanel.add(adminHeaderLabel, BorderLayout.NORTH);

    // Button panel with GridLayout
    JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15)); // 3 rows, 2 columns, with gaps
    buttonPanel.setOpaque(false); // Match background with the parent panel

    // Buttons
    JButton displayBooksButton = createStyledButton("Display Books", new Color(100, 150, 250));
    JButton addBookButton = createStyledButton("Add Book", new Color(50, 100, 200));
    JButton removeBookButton = createStyledButton("Remove Book", new Color(200, 100, 50));
    JButton viewBorrowedBooksButton = createStyledButton("View Borrowed Books", new Color(50, 150, 50));
    JButton viewAllUsersButton = createStyledButton("View All Users", new Color(150, 50, 150));
    JButton exitButton = createStyledButton("Exit", new Color(200, 50, 50));

    // Add action listeners
    displayBooksButton.addActionListener(e -> library.displayBooks());
    addBookButton.addActionListener(e -> showAddBookDialog());
    removeBookButton.addActionListener(e -> showRemoveBookDialog());
    viewBorrowedBooksButton.addActionListener(e -> library.viewBorrowedBooks());
    viewAllUsersButton.addActionListener(e -> library.viewAllUsers());
    exitButton.addActionListener(e -> {
        Object[] options = {"Logout", "Exit"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "What would you like to do?",
            "Logout or Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == JOptionPane.YES_OPTION) { // Logout
            userAuth.logoutUser(); // Reset logged-in user details (if needed)
            JOptionPane.showMessageDialog(
                this,
                "You have been logged out successfully.",
                "Logged Out",
                JOptionPane.INFORMATION_MESSAGE
            );
            cardLayout.show(mainPanel, "LOGIN");
        } else if (choice == JOptionPane.NO_OPTION) { // Exit
            JOptionPane.showMessageDialog(
                this,
                "Thank you for using the Library Management System!",
                "Goodbye",
                JOptionPane.INFORMATION_MESSAGE
            );
            System.exit(0);
        }
    });

    // Add buttons to button panel
    buttonPanel.add(displayBooksButton);
    buttonPanel.add(addBookButton);
    buttonPanel.add(removeBookButton);
    buttonPanel.add(viewBorrowedBooksButton);
    buttonPanel.add(viewAllUsersButton);
    buttonPanel.add(exitButton);

    // Add button panel to the center of the admin panel
    adminPanel.add(buttonPanel, BorderLayout.CENTER);

    // Add admin panel to the main panel
    mainPanel.add(adminPanel, "ADMIN_MENU");
}

    private void addUserMenuPanel() {
    JPanel userPanel = new JPanel(new GridLayout(7, 1, 10, 10));
    userPanel.setBackground(new Color(255, 253, 208)); // Light yellow background
    userPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

    JLabel userHeaderLabel = new JLabel("User Menu", SwingConstants.CENTER);
    userHeaderLabel.setFont(new Font("Serif", Font.BOLD, 30));
    userHeaderLabel.setForeground(new Color(0, 0, 0)); // Black color

    userPanel.add(userHeaderLabel);

    // Create buttons for User menu
    JButton displayBooksButton = createStyledButton("Display Books", new Color(100, 150, 250));
    JButton borrowBookButton = createStyledButton("Borrow Book", new Color(50, 150, 50));
    JButton returnBookButton = createStyledButton("Return Book", new Color(150, 100, 50));
    JButton addFeedbackButton = createStyledButton("Add Feedback", new Color(200, 100, 150));
    JButton viewFeedbackButton = createStyledButton("View Feedback", new Color(100, 50, 150));
    JButton exitButton = createStyledButton("Exit", new Color(200, 50, 50));

    displayBooksButton.addActionListener(e -> library.displayBooks());
    borrowBookButton.addActionListener(e -> showBorrowBookDialog());
    returnBookButton.addActionListener(e -> showReturnBookDialog());
    addFeedbackButton.addActionListener(e -> showAddFeedbackDialog());
    viewFeedbackButton.addActionListener(e -> showViewFeedbackDialog());

    exitButton.addActionListener(e -> {
    Object[] options = {"Logout", "Exit"};
    int choice = JOptionPane.showOptionDialog(
            this,
            "What would you like to do?",
            "Logout or Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
    );

    if (choice == JOptionPane.YES_OPTION) { // Logout
        userAuth.logoutUser(); // Reset logged-in user details (if needed)
        JOptionPane.showMessageDialog(
                this,
                "You have been logged out successfully.",
                "Logged Out",
                JOptionPane.INFORMATION_MESSAGE
        );
        cardLayout.show(mainPanel, "LOGIN");
    } else if (choice == JOptionPane.NO_OPTION) { // Exit
        JOptionPane.showMessageDialog(
                this,
                "Thank you for using the Library Management System!",
                "Goodbye",
                JOptionPane.INFORMATION_MESSAGE
        );
        System.exit(0);
    }
});

    userPanel.add(displayBooksButton);
    userPanel.add(borrowBookButton);
    userPanel.add(returnBookButton);
    userPanel.add(addFeedbackButton);
    userPanel.add(viewFeedbackButton);
    userPanel.add(exitButton);

    mainPanel.add(userPanel, "USER_MENU");
}


    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Reduced font size
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(15));  // Rounded corners with bigger radius
        button.setPreferredSize(new Dimension(150, 50)); // Set a fixed size for buttons

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void showLoginDialog() {
    JTextField emailField = new JTextField();
    String[] roles = {"User", "Admin"};
    JComboBox<String> roleComboBox = new JComboBox<>(roles);

    Object[] message = {"Email:", emailField, "Role:", roleComboBox};

    int option = JOptionPane.showConfirmDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        String email = emailField.getText();
        String selectedRole = (String) roleComboBox.getSelectedItem();

        if (userAuth.loginUser(email)) {
            JOptionPane.showMessageDialog(this, selectedRole + " Login Successful!");

            if ("User".equals(selectedRole)) {
                cardLayout.show(mainPanel, "USER_MENU");
            } else if ("Admin".equals(selectedRole)) {
                cardLayout.show(mainPanel, "ADMIN_MENU");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Email. Please register first.");
        }
    }
}


    private void showRegisterDialog() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] message = {"Name:", nameField, "Email:", emailField};

        int option = JOptionPane.showConfirmDialog(this, message, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            if (userAuth.registerUser(name, email)) {
                JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed. Email might already exist.");
            }
        }
    }

    private void showAddBookDialog() {
        JTextField bookIdField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();

        Object[] message = {"Book ID:", bookIdField, "Title:", titleField, "Author:", authorField};

        int option = JOptionPane.showConfirmDialog(this, message, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int bookId = Integer.parseInt(bookIdField.getText());
            String title = titleField.getText();
            String author = authorField.getText();
            library.addBook(bookId, title, author);
        }
    }

    private void showBorrowBookDialog() {
        JTextField bookIdField = new JTextField();
        Object[] message = {"Book ID:", bookIdField};

        int option = JOptionPane.showConfirmDialog(this, message, "Borrow Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int bookId = Integer.parseInt(bookIdField.getText());
            library.borrowBook(bookId, userAuth.getLoggedInUserId());
        }
    }

    private void showReturnBookDialog() {
        JTextField bookIdField = new JTextField();
        Object[] message = {"Book ID:", bookIdField};

        int option = JOptionPane.showConfirmDialog(this, message, "Return Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int bookId = Integer.parseInt(bookIdField.getText());
            library.returnBook(bookId, userAuth.getLoggedInUserId());
        }
    }

    private void showAddFeedbackDialog() {
        JTextField bookIdField = new JTextField();
        JTextArea feedbackArea = new JTextArea(5, 20);

        Object[] message = {"Book ID:", bookIdField, "Feedback:", new JScrollPane(feedbackArea)};

        int option = JOptionPane.showConfirmDialog(this, message, "Add Feedback", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int bookId = Integer.parseInt(bookIdField.getText());
            String feedback = feedbackArea.getText();
            library.addFeedback(bookId, feedback);
        }
    }

    private void showViewFeedbackDialog() {
        JTextField bookIdField = new JTextField();
        Object[] message = {"Book ID:", bookIdField};

        int option = JOptionPane.showConfirmDialog(this, message, "View Feedback", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int bookId = Integer.parseInt(bookIdField.getText());
            library.viewFeedback(bookId);
        }
    }
    private void showRemoveBookDialog() {
    JTextField bookIdField = new JTextField();
    Object[] message = {"Book ID:", bookIdField};

    int option = JOptionPane.showConfirmDialog(this, message, "Remove Book", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        int bookId = Integer.parseInt(bookIdField.getText());
        library.removeBook(bookId);
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryManagementSystem1::new);
    }
}

class RoundedBorder extends javax.swing.border.AbstractBorder {
    private int radius;

    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK); // Border color
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(10, 10, 10, 10); // Adjust padding inside the border
    }
}
