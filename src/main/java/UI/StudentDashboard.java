package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Data.Admindatalayer;
import Data.Instructordatalayer;
import Data.coursedatalayer;
import Data.enrollmentdatalayer;
import Data.grades;
import Data.sectiondatalayer;
import Data.settingsdatalayer;
import Data.studentdatalayer;
import domain.Course;
import domain.Grade;
import domain.Section;
import domain.TimeSlot;
import domain.User;
import auth.authhash;
import auth.authsession;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StudentDashboard extends JFrame {

    private JPanel topPanel;
    private JPanel sidePanel;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private Color primary = new Color(0x006B3C);
    private Color hover = new Color(0x00A859);

    private JButton menuButton;
    private JToggleButton mainToggle;

    private boolean sidebarVisible = true;

    private Admindatalayer adminDao = new Admindatalayer();
    private coursedatalayer coursedoa = new coursedatalayer();
    private enrollmentdatalayer enrolldoa = new enrollmentdatalayer();
    private grades gradeDoa = new grades();
    private Instructordatalayer instructorDoa = new Instructordatalayer();
    private sectiondatalayer sectionDoa = new sectiondatalayer();
    private settingsdatalayer settingsDao = new settingsdatalayer();
    private studentdatalayer studentDoa = new studentdatalayer();
    private authhash auth = new authhash();
    boolean maintainenceMode;

    private void checkMaintenanceMode() {
        Timer timer = new Timer(3000, e -> {

            boolean currentMode = settingsDao.isMaintenanceOn();
            if (currentMode != maintainenceMode) {
                maintainenceMode = currentMode;

                if (!maintainenceMode) {
                    mainToggle.setText("OFF");
                    mainToggle.setBackground(new Color(0x388E3C));
                } else {
                    mainToggle.setText("ON");
                    mainToggle.setBackground(new Color(0xD32F2F));
                }
            }
        });
        timer.start();
    }

    public StudentDashboard() {

        setTitle("ERP Management System - Student Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        maintainenceMode = settingsDao.isMaintenanceOn();

        topPanel = topBar();
        sidePanel = sideBar();
        mainPanel = mainArea();

        add(mainPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        add(sidePanel, BorderLayout.WEST);

        checkMaintenanceMode();
    }

    public void ChangePassword() {
        Color primaryColor = new Color(0x005A2D);
        Color cardBackground = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JDialog changePasswordDialog = new JDialog((Frame) null, "Change Password", true);
        changePasswordDialog.setSize(400, 500);
        changePasswordDialog.setLayout(new BorderLayout());
        changePasswordDialog.setLocationRelativeTo(null);
        changePasswordDialog.getContentPane().setBackground(Color.WHITE);

        JLabel passwordLabel = new JLabel("Change Password", SwingConstants.CENTER);
        passwordLabel.setOpaque(true);
        passwordLabel.setBackground(primaryColor);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        changePasswordDialog.add(passwordLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 20, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new BorderLayout(8, 6));
            p.setBackground(cardBackground);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)));

            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
            lbl.setForeground(primaryColor);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

            input.setFont(new Font("SansSerif", Font.PLAIN, 15));
            input.setBackground(Color.WHITE);
            input.setForeground(Color.DARK_GRAY);
            input.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));

            p.add(lbl, BorderLayout.NORTH);
            p.add(input, BorderLayout.CENTER);

            return p;
        };

        JPasswordField oldPass = new JPasswordField(15);
        JPasswordField newPass = new JPasswordField(15);
        JPasswordField confirmPass = new JPasswordField(15);

        // oldPass.setEchoChar((char)0);
        newPass.setEchoChar((char) 0);
        confirmPass.setEchoChar((char) 0);

        JPanel p1 = makeField.apply("Current Password*", oldPass);
        JPanel p2 = makeField.apply("New Password*", newPass);
        JPanel p3 = makeField.apply("Confirm New Password*", confirmPass);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(p1, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(p2, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(p3, gbc);

        changePasswordDialog.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = createButton("Update Password", "SansSerif", Color.WHITE, primary, 16);
        saveBtn.setPreferredSize(new Dimension(175, 40));

        JButton cancelBtn = createButton("Cancel", "SansSerif", Color.WHITE, Color.GRAY, 16);
        cancelBtn.setPreferredSize(new Dimension(140, 40));

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        changePasswordDialog.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> changePasswordDialog.dispose());

        saveBtn.addActionListener(e -> {
            String oldP = new String(oldPass.getPassword()).trim();
            String newP = new String(newPass.getPassword()).trim();
            String confirmP = new String(confirmPass.getPassword()).trim();

            if (oldP.isEmpty() || newP.isEmpty() || confirmP.isEmpty()) {
                JOptionPane.showMessageDialog(changePasswordDialog,
                        "All fields marked * are mandatory!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!newP.equals(confirmP)) {
                JOptionPane.showMessageDialog(changePasswordDialog,
                        "New password and confirm password do not match!",
                        "Mismatch",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newP.length() < 6) {
                JOptionPane.showMessageDialog(changePasswordDialog,
                        "Password must be at least 6 characters long!",
                        "Weak Password",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String username = authsession.getUsername();

            User user = adminDao.getUserByUsername(username);
            boolean changed = auth.changePassword(
                    user.getUserID(),
                    oldP,
                    newP);
            if (!changed) {
                JOptionPane.showMessageDialog(changePasswordDialog,
                        "Current password is incorrect!",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (changed) {
                JOptionPane.showMessageDialog(changePasswordDialog,
                        "Password updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            changePasswordDialog.dispose();
        });

        changePasswordDialog.setVisible(true);
    }

    public JButton createButton(String text, String fontText, Color foreColor, Color backColor, int size) {
        JButton button = new JButton(text);
        button.setFont(new Font(fontText, Font.BOLD, size));
        button.setForeground(foreColor);
        button.setBackground(backColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public JLabel creatLabel(String text, String fonText, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(fonText, Font.BOLD, size));
        label.setForeground(Color.WHITE);
        return label;
    }

    public JPanel topBar() {

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0x005A2D));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftPanel.setOpaque(false);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);

        menuButton = createButton("\u2630", "SansSerif", Color.WHITE, new Color(0x005A2D), 20);
        JLabel title = creatLabel("Student Dashboard", "Segoe UI", 20);

        menuButton.addActionListener(e -> {
            sidebarVisible = !sidebarVisible;
            sidePanel.setVisible(sidebarVisible);
            revalidate();
            repaint();
        });

        JButton b1 = createButton("My Profile", "SansSerif", Color.WHITE, primary, 16);
        JButton b2 = createButton("Change Password", "SansSerif", Color.WHITE, primary, 16);
        JButton b3 = createButton("Logout", "SansSerif", Color.WHITE, primary, 16);

        b1.addActionListener(e -> {
            String username = authsession.getUsername();
            String role = authsession.getRole();
            JOptionPane.showMessageDialog(null,
                    "Logged in as: " + username + "\nRole: " + role,
                    "My Profile", JOptionPane.INFORMATION_MESSAGE);
        });

        b2.addActionListener(e -> ChangePassword());
        b3.addActionListener(e -> {
            dispose();
            LoginPage.main(null);
        });

        leftPanel.add(menuButton);
        leftPanel.add(title);

        rightPanel.add(b1);
        rightPanel.add(b2);
        rightPanel.add(b3);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        return topPanel;
    }

    public JPanel sideBar() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(0x006B3C));
        sidePanel.setPreferredSize(new Dimension(200, getHeight()));

        String[] buttons = { "HOME", "COURSE CATALOG", "MY COURSES", "GRADES", "TIMETABLE" };
        sidePanel.add(Box.createVerticalStrut(10));
        for (String text : buttons) {
            JButton btn = createButton(text, "Segoe UI", Color.WHITE, primary, 15);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 20));
            sidePanel.add(btn);

            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(hover);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(primary);
                }
            });
            btn.addActionListener(e -> {
                switch (text) {
                    case "HOME":
                        cardLayout.show(mainPanel, "Home");
                        break;
                    case "COURSE CATALOG":
                        cardLayout.show(mainPanel, "CourseCatalog");
                        break;
                    case "MY COURSES":
                        cardLayout.show(mainPanel, "MyCourses");
                        break;
                    case "GRADES":
                        cardLayout.show(mainPanel, "Grades");
                        break;
                    case "TIMETABLE":
                        cardLayout.show(mainPanel, "Timetable");
                        break;
                    default:
                        break;
                }
            });
        }
        return sidePanel;
    }

    public JPanel mainArea() {
        JPanel mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.add(createHomePanel(), "Home");
        mainPanel.add(createCourseCatalog(), "CourseCatalog");
        mainPanel.add(createMyCourses(), "MyCourses");
        mainPanel.add(createGrades(), "Grades");
        mainPanel.add(createTimetable(), "Timetable");

        cardLayout.show(mainPanel, "Home");
        return mainPanel;
    }

    public JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);

        JPanel outerWrapper = new JPanel(new BorderLayout());
        outerWrapper.setBackground(new Color(0xF9F9F9));
        outerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        outerWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        JPanel maintenancePanel = new JPanel(new BorderLayout());
        maintenancePanel.setBackground(Color.WHITE);
        maintenancePanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel welcome = creatLabel("Hello, Student!  Welcome to IIIT Delhi.", "SansSerif", 20);
        welcome.setForeground(new Color(0x005A2D));
        welcome.setHorizontalAlignment(SwingConstants.LEFT);
        welcome.setVerticalAlignment(SwingConstants.CENTER);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(welcome);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);

        JLabel mainMode = creatLabel("View Maintenance Mode", "SansSerif", 18);
        mainMode.setForeground(new Color(0x333333));

        mainToggle = new JToggleButton();
        mainToggle.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainToggle.setFocusPainted(false);
        mainToggle.setForeground(Color.WHITE);
        mainToggle.setOpaque(true);
        mainToggle.setContentAreaFilled(true);
        mainToggle.setBorderPainted(false);
        mainToggle.setUI(new javax.swing.plaf.basic.BasicToggleButtonUI());
        mainToggle.setPreferredSize(new Dimension(90, 35));
        mainToggle.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        mainToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (!maintainenceMode) {
            mainToggle.setText("OFF");
            mainToggle.setBackground(new Color(0x388E3C));
        } else {
            mainToggle.setText("ON");
            mainToggle.setBackground(new Color(0xD32F2F));
        }
        rightPanel.add(mainMode);
        rightPanel.add(mainToggle);

        maintenancePanel.add(leftPanel, BorderLayout.WEST);
        maintenancePanel.add(rightPanel, BorderLayout.EAST);

        outerWrapper.add(maintenancePanel, BorderLayout.CENTER);

        homePanel.add(outerWrapper, BorderLayout.NORTH);
        return homePanel;
    }

    public void createRegisterSection(String code, String title, String credits) {
        Color primaryColor = new Color(0x005A2D);
        Color cardBackground = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JDialog d = new JDialog((Frame) null, "Select Section", true);
        d.setSize(900, 600);
        d.setLocationRelativeTo(null);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(Color.WHITE);

        BiFunction<String, String, JPanel> infoCard = (label, value) -> {

            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            p.setBackground(cardBackground);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)));

            JLabel l1 = new JLabel(label + ": ");
            l1.setFont(new Font("SansSerif", Font.BOLD, 15));
            l1.setForeground(primaryColor);

            JLabel l2 = new JLabel(value);
            l2.setFont(new Font("SansSerif", Font.PLAIN, 15));
            l2.setForeground(new Color(40, 40, 40));

            p.add(l1);
            p.add(l2);

            return p;
        };

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);

        gbc.gridx = 0;
        infoPanel.add(infoCard.apply("Course Code", code), gbc);
        gbc.gridx = 1;
        infoPanel.add(infoCard.apply("Course Title", title), gbc);
        gbc.gridx = 2;
        infoPanel.add(infoCard.apply("Credits", credits), gbc);

        d.add(infoPanel, BorderLayout.NORTH);

        String[] cols = { "Section", "Time", "Room", "Cap", "Enrolled", "Instructor" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);

        int courseId = coursedoa.getCourseIdByCode(code);
        List<Section> sectionsforstudent = sectionDoa.getSectionBycouseid(courseId);
        String instructorname;

        for (Section s : sectionsforstudent) {
            int instructorID = Integer.parseInt(s.getInstructorID());
            instructorname = instructorDoa.getInstructorNameById(instructorID);
            Object[] row = {
                    s.getSectionID(),
                    s.getTimeTable(),
                    s.getRoom(),
                    s.getSectionCapacity(),
                    s.getCurrentEnrollment(),
                    instructorname
            };
            model.addRow(row);
        }
        d.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        bottom.setBackground(Color.WHITE);

        JButton regBtn = createButton("Register", "SansSerif", Color.WHITE, primaryColor, 16);
        regBtn.setPreferredSize(new Dimension(180, 40));

        JButton closeBtn = createButton("Close", "SansSerif", Color.WHITE, Color.GRAY, 16);
        closeBtn.setPreferredSize(new Dimension(120, 40));

        bottom.add(regBtn);
        bottom.add(closeBtn);

        d.add(bottom, BorderLayout.SOUTH);

        regBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(d, "Select a section");
                return;
            }

            int sectionId = Integer.parseInt(table.getValueAt(r, 0).toString());
            int cap = Integer.parseInt(table.getValueAt(r, 3).toString());
            int curr = Integer.parseInt(table.getValueAt(r, 4).toString());

            if (curr >= cap) {
                JOptionPane.showMessageDialog(d, "Section full!");
                return;
            }
            String username1 = authsession.getUsername();
            int studentId1 = studentDoa.getstudentIdfromusername(username1);

            int result = enrolldoa.registerStudentInSection(studentId1, sectionId);

            switch (result) {

                case 1:
                    JOptionPane.showMessageDialog(d, "Successfully registered!");
                    System.out.println(
                            "Registration successful for student ID: " + studentId1 + " in section ID: " + sectionId);
                    d.dispose();
                    break;

                case -1:
                    JOptionPane.showMessageDialog(
                            d,
                            "You are already registered in this section.",
                            "Duplicate Registration",
                            JOptionPane.WARNING_MESSAGE);
                    break;

                case 2:
                    JOptionPane.showMessageDialog(
                            d,
                            "You have reached the maximum credit limit of 20.\nYou cannot register for more courses.",
                            "Credit Limit Reached",
                            JOptionPane.WARNING_MESSAGE);
                    break;
                case 3:
                    JOptionPane.showMessageDialog(
                            d,
                            "You are already enrolled in another section of this course.",
                            "Already Enrolled",
                            JOptionPane.WARNING_MESSAGE);
                    break;
                case 4:
                    System.out.println("Semester mismatch detected");
                    JOptionPane.showMessageDialog(
                            d,
                            "You cannot register for a section outside your current semester.",
                            "Semester Mismatch",
                            JOptionPane.WARNING_MESSAGE);
                    break;

                default:
                    JOptionPane.showMessageDialog(
                            d,
                            "Registration failed due to a system error.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        });

        closeBtn.addActionListener(e -> d.dispose());
        d.setVisible(true);
    }

    public JPanel createCourseCatalog() {

        Color primaryColor = new Color(0x005A2D);
        Color cardBackground = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField codeField = new JTextField(15);
        codeField.setPreferredSize(new Dimension(160, 30));

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            p.setBackground(cardBackground);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)));

            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            labelPanel.setBackground(cardBackground);

            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
            lbl.setForeground(primaryColor);
            labelPanel.add(lbl);

            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
            inputPanel.setBackground(cardBackground);

            input.setFont(new Font("SansSerif", Font.PLAIN, 15));
            input.setForeground(new Color(40, 40, 40));
            input.setBackground(Color.WHITE);
            input.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            inputPanel.add(input);

            p.add(labelPanel);
            p.add(inputPanel);

            return p;
        };

        JPanel f1 = makeField.apply("Course Code: ", codeField);

        JButton searchBtn = createButton("Search", "SansSerif", Color.WHITE, primary, 16);
        searchBtn.setPreferredSize(new Dimension(140, 40));

        JButton refreshBtn = createButton("Refresh", "SansSerif", Color.WHITE, primary, 14);
        refreshBtn.setPreferredSize(new Dimension(120, 40));

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(f1, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        filterPanel.add(searchBtn, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        filterPanel.add(refreshBtn, gbc);

        panel.add(filterPanel, BorderLayout.NORTH);

        String[] cols = { "Code", "Title", "Credits", "Register" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 3;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.getColumn("Register").setCellRenderer((tbl, val, sel, foc, row, col) -> {
            JButton btn = createButton("Register", "SansSerif", Color.WHITE, new Color(0x006B3C), 14);
            return btn;
        });

        table.getColumn("Register").setCellEditor(new DefaultCellEditor(new JTextField()) {
            JButton btn = createButton("Register", "SansSerif", Color.WHITE, new Color(0x006B3C), 14);
            {
                btn.addActionListener(e -> {
                    if (!maintainenceMode) {
                        int r = table.getSelectedRow();
                        String code = table.getValueAt(r, 0).toString();
                        String title = table.getValueAt(r, 1).toString();
                        String credits = table.getValueAt(r, 2).toString();
                        createRegisterSection(code, title, credits);
                        fireEditingStopped();
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Course registration is temporarily disabled.\nThe system is currently in Maintenance Mode.\nPlease try again later.",
                                "Maintenance Mode Active", JOptionPane.WARNING_MESSAGE);
                    }
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                return btn;
            }
        });
        searchBtn.addActionListener(e -> {
            String code = codeField.getText().trim();

            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter Course Code to search.");
                return;
            }

            model.setRowCount(0);

            Course course = coursedoa.getCourseByCode(code);

            if (course == null) {
                JOptionPane.showMessageDialog(null, "Course not found.");
                return;
            }

            model.addRow(new Object[] {
                    course.getCode(),
                    course.getTitle(),
                    course.getCredits(),
                    "Register"
            });
        });
        List<Course> updateddata = coursedoa.getAllCourses();
        model.setRowCount(0);
        Object[][] updatedData = new Object[updateddata.size()][3];
        for (int i = 0; i < updateddata.size(); i++) {
            Course course = updateddata.get(i);
            updatedData[i][0] = course.getCode();
            updatedData[i][1] = course.getTitle();
            updatedData[i][2] = course.getCredits();
        }
        for (Object[] row : updatedData) {
            model.addRow(new Object[] {
                    row[0], row[1], row[2], "Register"
            });
        }
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            List<Course> updateddata1 = coursedoa.getAllCourses();
            model.setRowCount(0);
            Object[][] updatedData1 = new Object[updateddata1.size()][3];
            for (int i = 0; i < updateddata1.size(); i++) {
                Course course = updateddata1.get(i);
                updatedData1[i][0] = course.getCode();
                updatedData1[i][1] = course.getTitle();
                updatedData1[i][2] = course.getCredits();
            }
            for (Object[] row : updatedData1) {
                model.addRow(new Object[] {
                        row[0], row[1], row[2], "Register"
                });
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMyCourses() {

        Color primaryColor = new Color(0x005A2D);
        Color cardBackground = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            p.setBackground(cardBackground);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)));

            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            labelPanel.setBackground(cardBackground);

            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
            lbl.setForeground(primaryColor);
            labelPanel.add(lbl);

            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
            inputPanel.setBackground(cardBackground);

            input.setFont(new Font("SansSerif", Font.PLAIN, 15));
            input.setForeground(new Color(40, 40, 40));
            input.setBackground(Color.WHITE);
            input.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            inputPanel.add(input);

            p.add(labelPanel);
            p.add(inputPanel);

            return p;
        };

        JTextField semField = new JTextField(15);
        semField.setPreferredSize(new Dimension(160, 30));

        JPanel semPanel = makeField.apply("Semester: ", semField);

        JButton searchBtn = createButton("Search", "SansSerif", Color.WHITE, primary, 16);
        searchBtn.setPreferredSize(new Dimension(140, 40));

        JButton refreshBtn = createButton("Refresh", "SansSerif", Color.WHITE, primary, 14);
        refreshBtn.setPreferredSize(new Dimension(120, 40));

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(semPanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        filterPanel.add(searchBtn, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        filterPanel.add(refreshBtn, gbc);

        panel.add(filterPanel, BorderLayout.NORTH);

        String[] cols = { "Course Code", "Course Title", "Section", "Semester", "Instructor", "Drop" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 5;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);

        table.getColumn("Drop").setCellRenderer((tbl, val, sel, foc, row, col) -> {
            String status = tbl.getValueAt(row, 5).toString();

            JButton btn;

            if (status.equals("Locked")) {
                btn = createButton("Locked", "SansSerif", Color.WHITE, Color.GRAY, 14);
                btn.setEnabled(false);
            } else {
                btn = createButton("Drop", "SansSerif", Color.WHITE, new Color(0x8B0000), 14);
            }

            return btn;
        });

        table.getColumn("Drop").setCellEditor(new DefaultCellEditor(new JTextField()) {
            JButton btn = createButton("Drop", "SansSerif", Color.WHITE, new Color(0x8B0000), 14);

            {
                btn.addActionListener(e -> {
                    if (!maintainenceMode) {
                        int r = table.getSelectedRow();
                        String courseCode = table.getValueAt(r, 0).toString();
                        String section = table.getValueAt(r, 2).toString();
                        boolean canDrop = true;
                        int sectionSemester = Integer.parseInt(table.getValueAt(r, 3).toString());
                        String username = authsession.getUsername();
                        int studentId = studentDoa.getstudentIdfromusername(username);
                        int studentSemester = studentDoa.getStudentSemester(studentId);
                        if (sectionSemester != studentSemester) {
                            JOptionPane.showMessageDialog(panel,
                                    "You cannot drop sections from other semesters.\n" +
                                            "Only current semester (" + studentSemester + ") sections can be dropped.",
                                    "Drop Not Allowed",
                                    JOptionPane.WARNING_MESSAGE);
                            fireEditingStopped();
                            return;
                        }
                        if (!canDrop) {
                            JOptionPane.showMessageDialog(panel, "Drop deadline passed!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            fireEditingStopped();
                            return;
                        }
                        int sectionId = Integer.parseInt(section);
                        boolean success = enrolldoa.dropStudentFromSection(studentId, sectionId);

                        if (!success) {
                            JOptionPane.showMessageDialog(
                                    panel,
                                    "Failed to drop the course. Please try again.",
                                    "Drop Failed",
                                    JOptionPane.ERROR_MESSAGE);
                            fireEditingStopped();
                            return;
                        }
                        JOptionPane.showMessageDialog(panel,
                                "Successfully dropped " + courseCode + " (Section " + section + ")");

                        ((DefaultTableModel) table.getModel()).removeRow(r);
                        fireEditingStopped();
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Course Drop is temporarily disabled.\nThe system is currently in Maintenance Mode.\nPlease try again later.",
                                "Maintenance Mode Active", JOptionPane.WARNING_MESSAGE);
                    }

                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
                String status = t.getValueAt(r, 5).toString();

                if (status.equals("Locked")) {
                    btn.setEnabled(false);
                } else {
                    btn.setEnabled(true);
                }

                return btn;
            }

        });

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);
        String username1 = authsession.getUsername();

        int studentId1 = studentDoa.getstudentIdfromusername(username1);
        int currentSemester = studentDoa.getStudentSemester(studentId1);
        List<Section> sections1 = sectionDoa.getAllEnrolledSections(studentId1);
        for (Section s : sections1) {
            String instructorname = instructorDoa.getInstructorNameById(Integer.parseInt(s.getInstructorID()));
            boolean canDrop = (s.getSemester() == currentSemester);
            Course c = coursedoa.getcoursebycourseid(s.getCourseID());
            model.addRow(new Object[] {
                    c.getCode(),
                    c.getTitle(),
                    s.getSectionID(),
                    s.getSemester(),
                    instructorname,
                    canDrop ? "Drop" : "Locked"
            });
        }
        searchBtn.addActionListener(e -> {
            String sem = semField.getText().trim();
            int semint = Integer.parseInt(sem);
            DefaultTableModel m = (DefaultTableModel) table.getModel();
            m.setRowCount(0);
            String username = authsession.getUsername();
            int studentId = studentDoa.getstudentIdfromusername(username);
            List<Section> sections = sectionDoa.getSectionsForStudent(studentId, semint);
            for (Section s : sections) {
                String instructorname = instructorDoa.getInstructorNameById(Integer.parseInt(s.getInstructorID()));
                Course c = coursedoa.getcoursebycourseid(s.getCourseID());
                boolean canDrop = (s.getSemester() == currentSemester);
                m.addRow(new Object[] {
                        c.getCode(),
                        c.getTitle(),
                        s.getSectionID(),
                        s.getSemester(),
                        instructorname,
                        canDrop ? "Drop" : "Locked"
                });
            }

        });

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            String username11 = authsession.getUsername();
            int studentId11 = studentDoa.getstudentIdfromusername(username11);
            List<Section> sections11 = sectionDoa.getAllEnrolledSections(studentId11);

            for (Section s : sections11) {
                String instructorname = instructorDoa.getInstructorNameById(Integer.parseInt(s.getInstructorID()));
                boolean canDrop = (s.getSemester() == currentSemester);
                Course c = coursedoa.getcoursebycourseid(s.getCourseID());
                model.addRow(new Object[] {
                        c.getCode(),
                        c.getTitle(),
                        s.getSectionID(),
                        s.getSemester(),
                        instructorname,
                        canDrop ? "Drop" : "Locked"
                });
            }
        });

        return panel;
    }

    private void createShowGradeDetails(String code, String title, String section, String gradeNum,
            String gradeLetter, Map<String, Double> marksMap) {
        Color primaryColor = new Color(0x005A2D);
        Color cardBackground = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JDialog d = new JDialog((Frame) null, "Grade Details", true);
        d.setSize(750, 500);
        d.setLocationRelativeTo(null);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(Color.WHITE);

        BiFunction<String, String, JPanel> infoCard = (label, value) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            p.setBackground(cardBackground);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));

            JLabel l1 = new JLabel(label + " : ");
            l1.setFont(new Font("SansSerif", Font.BOLD, 15));
            l1.setForeground(primaryColor);

            JLabel l2 = new JLabel(value);
            l2.setFont(new Font("SansSerif", Font.PLAIN, 15));
            l2.setForeground(new Color(40, 40, 40));

            p.add(l1);
            p.add(l2);

            return p;
        };

        JPanel infoContainer = new JPanel(new BorderLayout());
        infoContainer.setBackground(Color.WHITE);
        infoContainer.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        JPanel rowsPanel = new JPanel();
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
        rowsPanel.setBackground(Color.WHITE);
        rowsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel row1 = new JPanel(new GridLayout(1, 2, 15, 10));
        row1.setBackground(Color.WHITE);
        row1.add(infoCard.apply("Course Code: ", code));
        row1.add(infoCard.apply("Course Title: ", title));

        JPanel row2 = new JPanel(new GridLayout(1, 3, 15, 10));
        row2.setBackground(Color.WHITE);
        row2.add(infoCard.apply("Section ID: ", section));
        row2.add(infoCard.apply("Marks: ", gradeNum));
        row2.add(infoCard.apply("Grade: ", gradeLetter));

        rowsPanel.add(row1);
        rowsPanel.add(Box.createVerticalStrut(10));
        rowsPanel.add(row2);

        infoContainer.add(rowsPanel, BorderLayout.CENTER);

        d.add(infoContainer, BorderLayout.NORTH);

        String[] cols = { "Component", "Marks" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable compTable = new JTable(model);
        compTable.setRowHeight(26);

        for (Map.Entry<String, Double> e : marksMap.entrySet()) {
            model.addRow(new Object[] {
                    e.getKey(),
                    e.getValue()
            });
        }

        d.add(new JScrollPane(compTable), BorderLayout.CENTER);

        JButton closeBtn = createButton("Close", "SansSerif", Color.WHITE, Color.GRAY, 16);
        closeBtn.setPreferredSize(new Dimension(120, 40));

        JPanel bottom = new JPanel(new FlowLayout());
        bottom.setBackground(Color.WHITE);
        bottom.add(closeBtn);

        closeBtn.addActionListener(e -> d.dispose());

        d.add(bottom, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private JPanel createGrades() {
        Color cardBackground = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            p.setBackground(cardBackground);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));

            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
            lbl.setForeground(primary);

            input.setFont(new Font("SansSerif", Font.PLAIN, 15));
            input.setForeground(Color.DARK_GRAY);
            input.setBackground(Color.WHITE);
            input.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));

            p.add(lbl);
            p.add(input);

            return p;
        };

        JTextField semField = new JTextField(15);
        semField.setPreferredSize(new Dimension(160, 30));
        JPanel semPanel = makeField.apply("Semester: ", semField);

        JButton searchBtn = createButton("Search", "SansSerif", Color.WHITE, primary, 16);
        searchBtn.setPreferredSize(new Dimension(140, 40));

        JButton refreshBtn = createButton("Refresh", "SansSerif", Color.WHITE, primary, 14);
        refreshBtn.setPreferredSize(new Dimension(120, 40));

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(semPanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        filterPanel.add(searchBtn, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        filterPanel.add(refreshBtn, gbc);

        panel.add(filterPanel, BorderLayout.NORTH);

        String[] cols = { "Course Code", "Course Title", "Section", "Marks", "Grade", "Details" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 5;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        table.getColumn("Details").setCellRenderer((tbl, val, sel, foc, row, col) -> {
            return createButton("See Details", "SansSerif", Color.WHITE, new Color(0x006B3C), 14);
        });

        table.getColumn("Details").setCellEditor(new DefaultCellEditor(new JTextField()) {
            JButton btn = createButton("See Details", "SansSerif", Color.WHITE, new Color(0x006B3C), 14);

            {

                btn.addActionListener(e -> {
                    System.out.println("Details clicked");
                    int r = table.getSelectedRow();
                    String code = table.getValueAt(r, 0).toString();
                    String title = table.getValueAt(r, 1).toString();
                    String section = table.getValueAt(r, 2).toString();
                    String gradeNum = table.getValueAt(r, 3).toString();
                    String gradeLetter = "NA";
                    String username = authsession.getUsername();
                    int studentId = studentDoa.getstudentIdfromusername(username);
                    int sectionId = Integer.parseInt(section);
                    int enrollmentId = enrolldoa.getEnrollmentId(studentId, sectionId);

                    Grade g = gradeDoa.getGradesByEnrollment(enrollmentId);
                    if (table.getValueAt(r, 4) != null) {
                        gradeLetter = table.getValueAt(r, 4).toString();
                    }
                    createShowGradeDetails(code, title, section, gradeNum, gradeLetter,
                            g.getMarks());
                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
                return btn;
            }
        });
        String username = authsession.getUsername();
        int studentId = studentDoa.getstudentIdfromusername(username);
        int currentSemester = studentDoa.getStudentSemester(studentId);
        List<Section> sections = sectionDoa.getSectionsForStudent(studentId, currentSemester);
        for (Section s : sections) {

            Course c = coursedoa.getcoursebycourseid(s.getCourseID());
            int sectionId = Integer.parseInt(s.getSectionID());
            int enrollmentId = enrolldoa.getEnrollmentId(studentId, sectionId);
            model.addRow(new Object[] {
                    c.getCode(),
                    c.getTitle(),
                    s.getSectionID(),
                    gradeDoa.gettotalmarks(enrollmentId),
                    gradeDoa.getFinalGrade(enrollmentId),
                    "Details"
            });
        }
        searchBtn.addActionListener(e -> {
            String sem = semField.getText().trim();
            DefaultTableModel m = (DefaultTableModel) table.getModel();
            m.setRowCount(0);

            int semint = Integer.parseInt(sem);
            String username1 = authsession.getUsername();
            int studentId1 = studentDoa.getstudentIdfromusername(username1);
            List<Section> sections1 = sectionDoa.getSectionsForStudent(studentId1, semint);
            for (Section s : sections1) {

                Course c = coursedoa.getcoursebycourseid(s.getCourseID());
                int sectionId = Integer.parseInt(s.getSectionID());
                int enrollmentId = enrolldoa.getEnrollmentId(studentId1, sectionId);

                m.addRow(new Object[] {
                        c.getCode(),
                        c.getTitle(),
                        s.getSectionID(),
                        gradeDoa.gettotalmarks(enrollmentId),
                        gradeDoa.getFinalGrade(enrollmentId),
                        "Details"
                });
            }
        });

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            String username1 = authsession.getUsername();
            int studentId1 = studentDoa.getstudentIdfromusername(username1);
            int currentSemester1 = studentDoa.getStudentSemester(studentId1);
            List<Section> sections1 = sectionDoa.getSectionsForStudent(studentId1, currentSemester1);
            for (Section s : sections1) {

                Course c = coursedoa.getcoursebycourseid(s.getCourseID());
                int sectionId = Integer.parseInt(s.getSectionID());
                int enrollmentId = enrolldoa.getEnrollmentId(studentId1, sectionId);

                model.addRow(new Object[] {
                        c.getCode(),
                        c.getTitle(),
                        s.getSectionID(),
                        gradeDoa.gettotalmarks(enrollmentId),
                        gradeDoa.getFinalGrade(enrollmentId),
                        "Details"
                });
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton exportBtn = createButton("Export CSV", "SansSerif", Color.WHITE, primary, 16);
        exportBtn.setPreferredSize(new Dimension(160, 40));
        exportBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export Grades as CSV");
            chooser.setSelectedFile(new File("grades_export.csv"));

            int result = chooser.showSaveDialog(panel);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = chooser.getSelectedFile();

            try (FileWriter fw = new FileWriter(file)) {

                for (int i = 0; i < model.getColumnCount() - 1; i++) {
                    fw.append(model.getColumnName(i));
                    if (i != model.getColumnCount() - 2)
                        fw.append(',');
                }
                fw.append('\n');

                for (int r = 0; r < model.getRowCount(); r++) {
                    for (int c = 0; c < model.getColumnCount() - 1; c++) {

                        Object val = model.getValueAt(r, c);
                        String text = val == null ? "" : val.toString().replace(",", " ");

                        fw.append(text);
                        if (c != model.getColumnCount() - 2)
                            fw.append(',');
                    }
                    fw.append('\n');
                }

                fw.flush();
                JOptionPane.showMessageDialog(panel,
                        "CSV Exported Successfully!\nSaved to: " + file.getAbsolutePath(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                        "Error exporting CSV: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        bottomPanel.add(exportBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTimetable() {

        Color primaryColor = new Color(0x005A2D);
        Color cardBackground = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        BiFunction<String, String, JPanel> makeField = (label, label2) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            p.setBackground(cardBackground);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)));

            JLabel lbl = new JLabel(label + " : ");
            lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
            lbl.setForeground(primaryColor);

            JLabel lbl2 = new JLabel(label2);
            lbl2.setFont(new Font("SansSerif", Font.BOLD, 15));
            lbl2.setForeground(Color.BLACK);

            p.add(lbl);
            p.add(lbl2);
            return p;
        };

        JPanel semPanel = makeField.apply("Semester", "Current");

        JButton refreshBtn = createButton("Refresh", "SansSerif", Color.WHITE, primaryColor, 16);
        refreshBtn.setPreferredSize(new Dimension(120, 40));

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(semPanel, gbc);
        gbc.gridx = 1;
        filterPanel.add(refreshBtn, gbc);

        main.add(filterPanel, BorderLayout.NORTH);

        JPanel timetable = new JPanel();
        timetable.setLayout(new BoxLayout(timetable, BoxLayout.Y_AXIS));
        timetable.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(timetable);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        main.add(scrollPane, BorderLayout.CENTER);

        Function<String, JPanel> dayHeader = (day) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
            p.setBackground(primaryColor);

            JLabel lbl = new JLabel(day);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
            lbl.setForeground(Color.WHITE);
            p.add(lbl);

            return p;
        };

        BiFunction<String[], String, JPanel> makeCourseCard = (data, time) -> {
            JPanel p = new JPanel(new GridLayout(2, 1));
            p.setBackground(cardBackground);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));

            JLabel line1 = new JLabel(data[0] + " | Sec " + data[2] + "  (" + time + ")");
            line1.setFont(new Font("SansSerif", Font.BOLD, 14));
            line1.setForeground(primaryColor);

            JLabel line2 = new JLabel(data[1]);
            line2.setFont(new Font("SansSerif", Font.PLAIN, 14));

            p.add(line1);
            p.add(line2);

            return p;
        };

        int currentSem = studentDoa.getStudentSemester(
                studentDoa.getstudentIdfromusername(authsession.getUsername()));
        int studentId = studentDoa.getstudentIdfromusername(authsession.getUsername());

        List<Section> sections = sectionDoa.getSectionsForStudent(studentId, currentSem);

        Map<String, List<Object[]>> timetableData = new LinkedHashMap<>();
        timetableData.put("Monday", new ArrayList<>());
        timetableData.put("Tuesday", new ArrayList<>());
        timetableData.put("Wednesday", new ArrayList<>());
        timetableData.put("Thursday", new ArrayList<>());
        timetableData.put("Friday", new ArrayList<>());
        timetableData.put("Saturday", new ArrayList<>());
        timetableData.put("Sunday", new ArrayList<>());

        for (Section s : sections) {
            Course c = coursedoa.getcoursebycourseid(s.getCourseID());

            Map<DayOfWeek, TimeSlot> map = s.getTimeTable();
            for (Map.Entry<DayOfWeek, TimeSlot> e : map.entrySet()) {

                DayOfWeek day = e.getKey();
                TimeSlot ts = e.getValue();

                String dayStr = day.toString().substring(0, 1) + day.toString().substring(1).toLowerCase();
                String time = ts.toString();

                timetableData.get(dayStr).add(new Object[] {
                        c.getCode(),
                        c.getTitle(),
                        s.getSectionID(),
                        time
                });
            }
        }

        ActionListener loadAction = e -> {

            timetable.removeAll();

            for (String day : timetableData.keySet()) {

                JPanel dayPanel = new JPanel();
                dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
                dayPanel.setBackground(Color.WHITE);
                dayPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                dayPanel.add(dayHeader.apply(day));

                List<Object[]> entries = timetableData.get(day);

                if (entries.isEmpty()) {
                    JPanel empty = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
                    empty.setBackground(Color.WHITE);
                    JLabel msg = new JLabel("No classes scheduled");
                    msg.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    empty.add(msg);
                    dayPanel.add(empty);
                } else {
                    for (Object[] c : entries) {
                        String[] data = { c[0].toString(), c[1].toString(), c[2].toString() };
                        String time = c[3].toString();
                        dayPanel.add(makeCourseCard.apply(data, time));
                        dayPanel.add(Box.createVerticalStrut(8));
                    }
                }

                timetable.add(dayPanel);
                timetable.add(Box.createVerticalStrut(10));
            }

            timetable.revalidate();
            timetable.repaint();
        };

        loadAction.actionPerformed(null);

        refreshBtn.addActionListener(e -> {

            timetable.removeAll();
            int newStudentId = studentDoa.getstudentIdfromusername(authsession.getUsername());
            int newSem = studentDoa.getStudentSemester(newStudentId);

            List<Section> newSections = sectionDoa.getSectionsForStudent(newStudentId, newSem);

            Map<String, List<Object[]>> newTimetable = new LinkedHashMap<>();
            newTimetable.put("Monday", new ArrayList<>());
            newTimetable.put("Tuesday", new ArrayList<>());
            newTimetable.put("Wednesday", new ArrayList<>());
            newTimetable.put("Thursday", new ArrayList<>());
            newTimetable.put("Friday", new ArrayList<>());
            newTimetable.put("Saturday", new ArrayList<>());
            newTimetable.put("Sunday", new ArrayList<>());

            for (Section s : newSections) {

                Course c = coursedoa.getcoursebycourseid(s.getCourseID());
                Map<DayOfWeek, TimeSlot> map = s.getTimeTable();

                for (Map.Entry<DayOfWeek, TimeSlot> entry : map.entrySet()) {
                    DayOfWeek day = entry.getKey();
                    TimeSlot ts = entry.getValue();

                    String dayStr = day.toString().substring(0, 1)
                            + day.toString().substring(1).toLowerCase();

                    newTimetable.get(dayStr).add(new Object[] {
                            c.getCode(),
                            c.getTitle(),
                            s.getSectionID(),
                            ts.toString()
                    });
                }
            }

            for (String day : newTimetable.keySet()) {

                JPanel dayPanel = new JPanel();
                dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
                dayPanel.setBackground(Color.WHITE);
                dayPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                dayPanel.add(dayHeader.apply(day));

                List<Object[]> entries = newTimetable.get(day);

                if (entries.isEmpty()) {
                    JPanel empty = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
                    empty.setBackground(Color.WHITE);
                    empty.add(new JLabel("No classes scheduled"));
                    dayPanel.add(empty);
                } else {
                    for (Object[] c : entries) {
                        String[] data = { c[0].toString(), c[1].toString(), c[2].toString() };
                        String time = c[3].toString();
                        dayPanel.add(makeCourseCard.apply(data, time));
                        dayPanel.add(Box.createVerticalStrut(8));
                    }
                }

                timetable.add(dayPanel);
                timetable.add(Box.createVerticalStrut(10));
            }

            timetable.revalidate();
            timetable.repaint();
        });

        return main;
    }
}