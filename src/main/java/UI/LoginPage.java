package UI;

import javax.swing.*;

import auth.AuthService;
import auth.authsession;

import java.awt.*;

public class LoginPage {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Login Page");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            JPanel backgroundPanel = new JPanel() {
                Image backgroundImage = new ImageIcon(
                        "src\\main\\java\\UI\\IIITD.jpeg").getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    int imgWidth = backgroundImage.getWidth(null);
                    int imgHeight = backgroundImage.getHeight(null);

                    double scale = Math.max(
                            (double) panelWidth / imgWidth,
                            (double) panelHeight / imgHeight);
                    int newWidth = (int) (imgWidth * scale);
                    int newHeight = (int) (imgHeight * scale);
                    int x = (panelWidth - newWidth) / 2;
                    int y = (panelHeight - newHeight) / 2;

                    g.drawImage(backgroundImage, x, y, newWidth, newHeight, this);
                }
            };
            backgroundPanel.setBounds(0, 0, 800, 600);
            backgroundPanel.setLayout(null);

            JPanel loginPanel = new JPanel();
            loginPanel.setBackground(new Color(255, 255, 255, 150));
            loginPanel.setBounds(200, 80, 400, 400);
            loginPanel.setLayout(null);

            JTextArea titleLabel = new JTextArea("INDRAPRASTHA INSTITUTE of INFORMATION TECHNOLOGY DELHI");
            titleLabel.setLineWrap(true);
            titleLabel.setWrapStyleWord(true);
            titleLabel.setEditable(false);
            titleLabel.setOpaque(false);
            titleLabel.setFocusable(false);
            titleLabel.setFont(new Font("Myriad Pro", Font.BOLD, 16));
            titleLabel.setForeground(new Color(2, 39, 66));
            titleLabel.setBounds(50, 20, 300, 80);
            loginPanel.add(titleLabel);

            JTextArea titleLabel2 = new JTextArea("Login Portal - ERP");
            titleLabel2.setLineWrap(true);
            titleLabel2.setWrapStyleWord(true);
            titleLabel2.setEditable(false);
            titleLabel2.setOpaque(false);
            titleLabel2.setFocusable(false);
            titleLabel2.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel2.setForeground(new Color(2, 39, 66));
            titleLabel2.setBounds(60, 10, 300, 50);
            titleLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel loginBox = new JPanel();
            loginBox.setBackground(new Color(240, 240, 240));
            loginBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            loginBox.setBounds(50, 150, 300, 225);
            loginBox.setLayout(null);
            loginBox.add(titleLabel2);

            JLabel userLabel = new JLabel("Username");
            userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            userLabel.setBounds(30, 50, 100, 25);
            JTextField userField = new JTextField();
            userField.setBounds(30, 75, 225, 25);

            JLabel passLabel = new JLabel("Password");
            passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            passLabel.setBounds(30, 110, 100, 25);
            JPasswordField passField = new JPasswordField();
            passField.setBounds(0, 0, 225, 25);
            passField.setEchoChar('•');
            JLabel eyeLabel = new JLabel("\uD83D\uDC41");
            eyeLabel.setBounds(200, 0, 25, 25);
            eyeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLayeredPane layeredPanel = new JLayeredPane();
            layeredPanel.setBounds(30, 135, 260, 25);
            layeredPanel.add(passField, JLayeredPane.DEFAULT_LAYER);
            layeredPanel.add(eyeLabel, JLayeredPane.PALETTE_LAYER);

            eyeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                private boolean visible = false;

                public void mouseClicked(java.awt.event.MouseEvent e) {
                    visible = !visible;
                    passField.setEchoChar(visible ? (char) 0 : '•');
                }
            });

            JButton loginButton = new JButton("Login");
            loginButton.setFont(new Font("Arial", Font.BOLD, 14));
            loginButton.setBounds(90, 175, 100, 30);
            loginButton.setBackground(new Color(2, 39, 66));
            loginButton.setForeground(Color.WHITE);
            loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            loginBox.add(userLabel);
            loginBox.add(userField);
            loginBox.add(passLabel);
            loginBox.add(loginButton);
            loginBox.add(layeredPanel);

            loginButton.addActionListener(e -> {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                boolean success = AuthService.login(username, password);
                System.out.println("Login success: " + success);
                if (success) {
                    frame.dispose();
                    String role = authsession.getRole();
                    if ("admin".equals(role)) {
                        AdminDashboard admin = new AdminDashboard();
                        admin.setVisible(success);
                    } else if ("instructor".equals(role)) {
                        InstructorDashboard instructor = new InstructorDashboard();
                        instructor.setVisible(true);
                    } else if ("user".equals(role)) {
                        StudentDashboard student = new StudentDashboard();
                        student.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Unknown role: " + role);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password!");
                }
            });

            loginPanel.add(loginBox);

            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(800, 600));

            layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

            layeredPane.add(loginPanel, JLayeredPane.PALETTE_LAYER);

            frame.setContentPane(layeredPane);
            frame.setVisible(true);
        });
    }
}
