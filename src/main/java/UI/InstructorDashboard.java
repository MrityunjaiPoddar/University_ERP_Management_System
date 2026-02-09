package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Data.Admindatalayer;
import Data.Instructordatalayer;
import Data.Stats;
import Data.coursedatalayer;
import Data.enrollmentdatalayer;
import Data.grades;
import Data.gradeslabdatalayer;
import Data.sectiondatalayer;
import Data.settingsdatalayer;
import domain.Course;
import domain.Section;
import domain.Student;
import domain.User;
import domain.gradeslab;
import auth.authhash;
import auth.authsession;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;
import javax.swing.Timer;

public class InstructorDashboard extends JFrame {
    private JPanel topPanel;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private Color primary = new Color(0x006B3C);
    private JToggleButton mainToggle;

    private Admindatalayer adminDao = new Admindatalayer();
    private coursedatalayer coursedoa = new coursedatalayer();
    private enrollmentdatalayer enrolldoa = new enrollmentdatalayer();
    private grades gradedao = new grades();
    private gradeslabdatalayer slabDoa = new gradeslabdatalayer();
    private Instructordatalayer instructorDL = new Instructordatalayer();
    private sectiondatalayer sectiondoa = new sectiondatalayer();
    private Stats statdao = new Stats();
    boolean maintenanceMode;
    private settingsdatalayer settingsDL = new settingsdatalayer();
    private authhash auth = new authhash();

    private void checkMaintenanceMode() {
        Timer timer = new Timer(3000, e -> {
            boolean currentMode = settingsDL.isMaintenanceOn();
            if (currentMode != maintenanceMode) {
                maintenanceMode = currentMode;

                if (!maintenanceMode) {
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

    public InstructorDashboard() {
        setTitle("ERP Management System - Instructor Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        maintenanceMode = settingsDL.isMaintenanceOn();

        topPanel = topBar();
        mainPanel = mainArea();

        add(mainPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        checkMaintenanceMode();
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

    public void ChangePassword() {

        Color primary = new Color(0x005A2D);
        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JDialog dialog = new JDialog((Frame) null, "Change Password", true);
        dialog.setSize(500, 500);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);
        dialog.getContentPane().setBackground(Color.WHITE);

        JLabel header = new JLabel("Change Password", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(primary);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        dialog.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 20, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new BorderLayout(10, 5));
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)));

            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
            lbl.setForeground(primary);

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

        JPanel p1 = makeField.apply("Current Password *", oldPass);
        JPanel p2 = makeField.apply("New Password *", newPass);
        JPanel p3 = makeField.apply("Confirm New Password *", confirmPass);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(p1, gbc);
        gbc.gridy = 1;
        formPanel.add(p2, gbc);
        gbc.gridy = 2;
        formPanel.add(p3, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = createButton("Update", "SansSerif", Color.WHITE, primary, 16);
        saveBtn.setPreferredSize(new Dimension(140, 40));

        JButton cancelBtn = createButton("Cancel", "SansSerif", Color.WHITE, Color.GRAY, 16);
        cancelBtn.setPreferredSize(new Dimension(140, 40));

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        dialog.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {

            String oldP = new String(oldPass.getPassword()).trim();
            String newP = new String(newPass.getPassword()).trim();
            String confirmP = new String(confirmPass.getPassword()).trim();

            if (oldP.isEmpty() || newP.isEmpty() || confirmP.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "All fields marked * are mandatory!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!newP.equals(confirmP)) {
                JOptionPane.showMessageDialog(dialog,
                        "New password and confirm password do not match!",
                        "Mismatch",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = authsession.getUsername();

            User user = adminDao.getUserByUsername(username);
            boolean changed = auth.changePassword(
                    user.getUserID(),
                    oldP,
                    newP);
            if (!changed) {
                JOptionPane.showMessageDialog(dialog,
                        "Current password is incorrect!",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (changed) {
                JOptionPane.showMessageDialog(dialog,
                        "Password updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    public JPanel topBar() {

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0x005A2D));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftPanel.setOpaque(false);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);

        JLabel title = creatLabel("Instructor Dashboard", "Segoe UI", 20);

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

        leftPanel.add(title);

        rightPanel.add(b1);
        rightPanel.add(b2);
        rightPanel.add(b3);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        return topPanel;
    }

    public JPanel mainArea() {
        JPanel mainPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) mainPanel.getLayout();

        mainPanel.add(createHomePanel(), "Home");
        cardLayout.show(mainPanel, "Home");
        return mainPanel;
    }

    public JPanel createHomePanel() {

        Color primaryColor = new Color(0x005A2D);
        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);

        JPanel outerWrapper = new JPanel(new BorderLayout());
        outerWrapper.setBackground(new Color(0xF9F9F9));
        outerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        outerWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        JPanel maintenancePanel = new JPanel(new BorderLayout());
        maintenancePanel.setBackground(Color.WHITE);
        maintenancePanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel welcome = creatLabel("Hello, Instructor!  Welcome to IIIT Delhi.", "SansSerif", 20);
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

        if (!maintenanceMode) {
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

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Color.WHITE);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)));
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
            lbl.setForeground(primaryColor);
            p.add(lbl);
            p.add(input);
            return p;
        };

        String[] statusOptions = { "All", "Ongoing", "Completed" };
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        JPanel statusCard = makeField.apply("Status:", statusCombo);

        JButton searchBtn = createButton("Search", "SansSerif", Color.WHITE, primary, 16);
        searchBtn.setPreferredSize(new Dimension(140, 40));

        JButton refreshBtn = createButton("Refresh", "SansSerif", Color.WHITE, primary, 16);
        refreshBtn.setPreferredSize(new Dimension(140, 40));

        JLabel mySec = creatLabel("MY SECTIONS", "SansSerif", 20);
        mySec.setForeground(primary);

        gbc.gridx = 0;
        filterPanel.add(mySec, gbc);
        gbc.gridx = 1;
        filterPanel.add(statusCard, gbc);
        gbc.gridx = 2;
        filterPanel.add(searchBtn, gbc);
        gbc.gridx = 3;
        filterPanel.add(refreshBtn, gbc);

        center.add(filterPanel, BorderLayout.NORTH);

        String[] cols = { "Course Code", "Course Title", "Section ID", "Semester" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(32);

        center.add(new JScrollPane(table), BorderLayout.CENTER);

        homePanel.add(center, BorderLayout.CENTER);

        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 12));
        bottomButtons.setBackground(Color.WHITE);

        JButton weightBtn = createButton("Set Weightage", "SansSerif", Color.WHITE, primaryColor, 16);
        JButton slabBtn = createButton("Set Slabs", "SansSerif", Color.WHITE, primaryColor, 16);
        JButton marksBtn = createButton("Assign Marks", "SansSerif", Color.WHITE, primaryColor, 16);
        JButton statsBtn = createButton("Show Stats", "SansSerif", Color.WHITE, primaryColor, 16);

        bottomButtons.add(weightBtn);
        bottomButtons.add(slabBtn);
        bottomButtons.add(marksBtn);
        bottomButtons.add(statsBtn);

        homePanel.add(bottomButtons, BorderLayout.SOUTH);

        ActionListener action = e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(homePanel, "Select a section first!");
                return;
            }

            String code = table.getValueAt(r, 0).toString();
            String title = table.getValueAt(r, 1).toString();
            String section = table.getValueAt(r, 2).toString();
            String semester = table.getValueAt(r, 3).toString();

            Object src = e.getSource();

            if (maintenanceMode) {
                JOptionPane.showMessageDialog(homePanel,
                        "Action disabled during Maintenance Mode!",
                        "Maintenance Active", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (src == weightBtn)
                openWeightagePopup(code, title, semester, section);
            if (src == slabBtn)
                openGradeSlabPopup(code, title, semester, section);
            if (src == marksBtn)
                openStudentListPopup(code, title, section, semester);
            if (src == statsBtn)
                openStatsPopup(code, title, section, semester);
        };

        weightBtn.addActionListener(action);
        slabBtn.addActionListener(action);
        marksBtn.addActionListener(action);
        statsBtn.addActionListener(action);

        searchBtn.addActionListener(e -> {
            model.setRowCount(0);
            int instructorId = instructorDL.getInstructorIdFromUsername(authsession.getUsername());
            int currentYear = 2025;

            List<Section> sections = instructorDL.getSectionsByInstructorId(instructorId);
            String filter = statusCombo.getSelectedItem().toString();

            for (Section s : sections) {

                int year = s.getYear();

                boolean add = false;

                if (filter.equals("All")) {
                    add = true;
                } else if (filter.equals("Ongoing")) {
                    if (year == currentYear)
                        add = true;
                } else if (filter.equals("Completed")) {
                    if (year < currentYear)
                        add = true;
                }

                if (add) {
                    Course c = coursedoa.getcoursebycourseid(s.getCourseID());

                    model.addRow(new Object[] {
                            c.getCode(),
                            c.getTitle(),
                            s.getSectionID(),
                            "Year " + s.getYear()
                    });
                }
            }
        });

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            searchBtn.doClick();
        });

        searchBtn.doClick();

        return homePanel;
    }

    public void openWeightagePopup(String courseCode, String courseTitle, String semester, String sectionId) {

        Color primaryColor = new Color(0x005A2D);
        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JDialog d = new JDialog((Frame) null, "Weightage Settings", true);
        d.setSize(850, 600);
        d.setLocationRelativeTo(null);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(Color.WHITE);

        BiFunction<String, String, JPanel> infoCard = (label, value) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));

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

        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 15, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        infoPanel.add(infoCard.apply("Course Code", courseCode));
        infoPanel.add(infoCard.apply("Course Title", courseTitle));
        infoPanel.add(infoCard.apply("Section ID", sectionId));
        infoPanel.add(infoCard.apply("Semester", semester));
        d.add(infoPanel, BorderLayout.NORTH);

        String[] cols = { "Component", "Percentage" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0xE0E0E0)));
        d.add(scroll, BorderLayout.CENTER);

        // bottom
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel bonusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bonusPanel.setBackground(Color.WHITE);

        JLabel bLabel = new JLabel("Bonus Marks (%): ");
        bLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        bLabel.setForeground(primaryColor);

        JTextField bonusField = new JTextField("0");
        bonusField.setPreferredSize(new Dimension(60, 28));

        bonusPanel.add(bLabel);
        bonusPanel.add(bonusField);

        bottom.add(bonusPanel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        btnPanel.setBackground(Color.WHITE);

        JButton addBtn = createButton("Add", "SansSerif", Color.WHITE, primaryColor, 15);
        JButton editBtn = createButton("Edit", "SansSerif", Color.WHITE, new Color(0x004C99), 15);
        JButton delBtn = createButton("Delete", "SansSerif", Color.WHITE, new Color(0x8B0000), 15);
        JButton saveBtn = createButton("Save", "SansSerif", Color.WHITE, primaryColor, 16);
        JButton closeBtn = createButton("Close", "SansSerif", Color.WHITE, Color.GRAY, 16);

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(closeBtn);

        bottom.add(btnPanel, BorderLayout.SOUTH);
        d.add(bottom, BorderLayout.SOUTH);

        int secId = Integer.parseInt(sectionId);
        Map<String, Integer> saved = sectiondoa.getWeightageForSection(secId);
        if (!saved.isEmpty()) {
            model.setRowCount(0);
            for (Map.Entry<String, Integer> e : saved.entrySet()) {
                if (!e.getKey().equals("Bonus")) {
                    model.addRow(new Object[] { e.getKey(), e.getValue() });
                } else {
                    bonusField.setText(String.valueOf(e.getValue()));
                }
            }
        }
        addBtn.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField percField = new JTextField();

            Object[] input = {
                    "Component Name:", nameField,
                    "Percentage:", percField
            };

            if (JOptionPane.showConfirmDialog(d, input, "Add Component",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

                String name = nameField.getText().trim();
                String perc = percField.getText().trim();

                if (name.isEmpty() || perc.isEmpty()) {
                    JOptionPane.showMessageDialog(d, "Fields cannot be empty!");
                    return;
                }

                try {
                    int p = Integer.parseInt(perc);
                    if (p < 0)
                        throw new Exception();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(d, "Invalid percentage");
                    return;
                }

                model.addRow(new Object[] { name, perc });
            }
        });

        editBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(d, "Select a row to edit!");
                return;
            }

            String oldC = table.getValueAt(r, 0).toString();
            String oldP = table.getValueAt(r, 1).toString();

            JTextField nameField = new JTextField(oldC);
            JTextField percField = new JTextField(oldP);

            Object[] input = {
                    "Component Name:", nameField,
                    "Percentage:", percField
            };

            if (JOptionPane.showConfirmDialog(d, input, "Edit Component",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

                String name = nameField.getText().trim();
                String perc = percField.getText().trim();

                try {
                    int p = Integer.parseInt(perc);
                    if (p < 0)
                        throw new Exception();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(d, "Invalid percentage");
                    return;
                }

                table.setValueAt(name, r, 0);
                table.setValueAt(perc, r, 1);
            }
        });

        delBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(d, "Select a row to delete!");
                return;
            }

            model.removeRow(r);
        });

        saveBtn.addActionListener(e -> {
            int total = 0;
            Map<String, Integer> weightage = new HashMap<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                String component = model.getValueAt(i, 0).toString();
                int perc = Integer.parseInt(model.getValueAt(i, 1).toString());
                weightage.put(component, perc);
                total += perc;
            }

            int bonus = 0;
            try {
                bonus = Integer.parseInt(bonusField.getText().trim());
                if (bonus < 0)
                    throw new Exception();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Bonus must be a valid number!");
                return;
            }

            if (total + bonus > 100) {
                JOptionPane.showMessageDialog(d,
                        "Total + Bonus cannot exceed 100%\nCurrent = " + (total + bonus),
                        "Invalid", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (total + bonus < 100) {
                JOptionPane.showMessageDialog(d,
                        "Total + Bonus must equal 100%\nCurrent = " + (total + bonus),
                        "Invalid", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (sectiondoa.updateWeightage(secId, weightage, bonus)) {
                JOptionPane.showMessageDialog(d, "Weightage saved successfully!");
                d.dispose();
            } else {
                JOptionPane.showMessageDialog(d, "Error saving weightage!");
            }
        });

        closeBtn.addActionListener(e -> d.dispose());

        d.setVisible(true);
    }

    public void openGradeSlabPopup(String courseCode, String courseTitle, String semester, String sectionId) {

        Color primaryColor = new Color(0x005A2D);
        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JDialog d = new JDialog((Frame) null, "Grade Slabs Settings", true);
        d.setSize(850, 600);
        d.setLocationRelativeTo(null);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(Color.WHITE);

        BiFunction<String, String, JPanel> infoCard = (label, value) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));

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

        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 15, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        infoPanel.add(infoCard.apply("Course Code", courseCode));
        infoPanel.add(infoCard.apply("Course Title", courseTitle));
        infoPanel.add(infoCard.apply("Section ID", sectionId));
        infoPanel.add(infoCard.apply("Semester", semester));

        d.add(infoPanel, BorderLayout.NORTH);

        String[] cols = { "Grade", "Min Marks", "Max Marks", "Grade Point" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0xE0E0E0)));
        d.add(scroll, BorderLayout.CENTER);

        int instId = instructorDL.getInstructorIdFromUsername(authsession.getUsername());
        int secId = Integer.parseInt(sectionId);

        List<gradeslab> slabsFromDB = slabDoa.getSlabsForSection(instId, secId);

        for (gradeslab s : slabsFromDB) {
            model.addRow(new Object[] {
                    s.getGrade(),
                    s.getMinMarks(),
                    s.getMaxMarks(),
                    s.getGradePoint()
            });
        }

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        btnPanel.setBackground(Color.WHITE);

        JButton addBtn = createButton("Add", "SansSerif", Color.WHITE, primaryColor, 15);
        JButton editBtn = createButton("Edit", "SansSerif", Color.WHITE, new Color(0x004C99), 15);
        JButton delBtn = createButton("Delete", "SansSerif", Color.WHITE, new Color(0x8B0000), 15);
        JButton saveBtn = createButton("Save", "SansSerif", Color.WHITE, primaryColor, 16);
        JButton closeBtn = createButton("Close", "SansSerif", Color.WHITE, Color.GRAY, 16);

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(closeBtn);

        bottom.add(btnPanel, BorderLayout.SOUTH);
        d.add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {

            JTextField gradeField = new JTextField();
            JTextField minField = new JTextField();
            JTextField maxField = new JTextField();
            JTextField gpField = new JTextField();

            Object[] input = {
                    "Grade (Example: A+):", gradeField,
                    "Min Marks (inclusive):", minField,
                    "Max Marks (inclusive):", maxField,
                    "Grade Point:", gpField
            };

            if (JOptionPane.showConfirmDialog(d, input,
                    "Add Grade Slab", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

                String grade = gradeField.getText().trim();
                String min = minField.getText().trim();
                String max = maxField.getText().trim();
                String gp = gpField.getText().trim();

                if (grade.isEmpty() || min.isEmpty() || max.isEmpty() || gp.isEmpty()) {
                    JOptionPane.showMessageDialog(d, "All fields are required!");
                    return;
                }

                try {
                    int a = Integer.parseInt(min);
                    int b = Integer.parseInt(max);
                    double gpVal = Double.parseDouble(gp);

                    if (a < 0 || b < 0 || b < a)
                        throw new Exception();
                    if (gpVal < 0)
                        throw new Exception();

                    model.addRow(new Object[] { grade, a, b, gpVal });

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(d, "Invalid numeric values!");
                }
            }
        });

        editBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(d, "Select a slab to edit!");
                return;
            }

            String oldGrade = table.getValueAt(r, 0).toString();
            String oldMin = table.getValueAt(r, 1).toString();
            String oldMax = table.getValueAt(r, 2).toString();
            String oldGP = table.getValueAt(r, 3).toString();

            JTextField gradeField = new JTextField(oldGrade);
            JTextField minField = new JTextField(oldMin);
            JTextField maxField = new JTextField(oldMax);
            JTextField gpField = new JTextField(oldGP);

            Object[] input = {
                    "Grade:", gradeField,
                    "Min Marks:", minField,
                    "Max Marks:", maxField,
                    "Grade Point:", gpField
            };

            if (JOptionPane.showConfirmDialog(d, input,
                    "Edit Grade Slab", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

                try {
                    int min = Integer.parseInt(minField.getText().trim());
                    int max = Integer.parseInt(maxField.getText().trim());
                    double gp = Double.parseDouble(gpField.getText().trim());

                    if (min < 0 || max < 0 || max < min)
                        throw new Exception();
                    if (gp < 0)
                        throw new Exception();

                    table.setValueAt(gradeField.getText().trim(), r, 0);
                    table.setValueAt(min, r, 1);
                    table.setValueAt(max, r, 2);
                    table.setValueAt(gp, r, 3);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(d, "Invalid numeric values!");
                }
            }
        });

        delBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(d, "Select a slab to delete!");
                return;
            }
            model.removeRow(r);
        });

        saveBtn.addActionListener(e -> {
            List<gradeslab> slabs = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                slabs.add(new gradeslab(
                        model.getValueAt(i, 0).toString(),
                        Integer.parseInt(model.getValueAt(i, 1).toString()),
                        Integer.parseInt(model.getValueAt(i, 2).toString()),
                        Double.parseDouble(model.getValueAt(i, 3).toString())));
            }

            int instructorId = instructorDL.getInstructorIdFromUsername(authsession.getUsername());
            boolean ok = slabDoa.saveSlabsForSection(instructorId, Integer.parseInt(sectionId), slabs);
            if (ok) {
                JOptionPane.showMessageDialog(d, "Grade slabs saved successfully!");
            } else {
                JOptionPane.showMessageDialog(d, "Error saving grade slabs!");
            }
            d.dispose();
        });

        closeBtn.addActionListener(e -> d.dispose());

        d.setVisible(true);
    }

    private void openStudentListPopup(String courseCode, String courseTitle,
            String sectionId, String semester) {

        Color primaryColor = new Color(0x005A2D);

        JDialog d = new JDialog((Frame) null, "Students in Section", true);
        d.setSize(850, 600);
        d.setLocationRelativeTo(null);
        d.setLayout(new BorderLayout());

        JPanel info = new JPanel(new GridLayout(2, 2, 15, 10));
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        info.add(infoCard("Course Code", courseCode));
        info.add(infoCard("Course Title", courseTitle));
        info.add(infoCard("Section ID", sectionId));
        info.add(infoCard("Semester", semester));

        d.add(info, BorderLayout.NORTH);

        String[] cols = { "Roll No", "Name", "Assign" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 2;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);

        table.getColumn("Assign").setCellRenderer(
                (tbl, v, s, f, r, c) -> createButton("Assign Marks", "SansSerif", Color.WHITE, primaryColor, 14));

        table.getColumn("Assign").setCellEditor(new DefaultCellEditor(new JTextField()) {
            JButton btn = createButton("Assign Marks", "SansSerif", Color.WHITE, primaryColor, 14);

            {
                btn.addActionListener(e -> {
                    int r = table.getSelectedRow();
                    if (r == -1)
                        return;

                    String roll = table.getValueAt(r, 0).toString();
                    String name = table.getValueAt(r, 1).toString();

                    openAssignMarksPopup(courseCode, courseTitle, semester, sectionId, roll, name);

                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
                return btn;
            }
        });
        int sectionid = Integer.parseInt(sectionId);
        List<Student> students = enrolldoa.getStudentDetails(sectionid);
        for (Student s : students) {
            model.addRow(new Object[] {
                    s.getRollNumber(),
                    s.getName(),
                    "Assign"
            });
        }

        d.add(new JScrollPane(table), BorderLayout.CENTER);

        d.setVisible(true);
    }

    private void openAssignMarksPopup(String courseCode, String courseTitle,
            String semester, String sectionId,
            String roll, String stuName) {

        Color primary = new Color(0x005A2D);

        JDialog d = new JDialog((Frame) null, "Assign Marks", true);
        d.setSize(850, 600);
        d.setLocationRelativeTo(null);
        d.setLayout(new BorderLayout());

        JPanel info = new JPanel(new GridLayout(3, 2, 15, 10));
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        info.add(infoCard("Course Code", courseCode));
        info.add(infoCard("Course Title", courseTitle));
        info.add(infoCard("Section ID", sectionId));
        info.add(infoCard("Semester", semester));
        info.add(infoCard("Roll No", roll));
        info.add(infoCard("Student Name", stuName));

        d.add(info, BorderLayout.NORTH);

        String[] cols = { "Component", "Max Marks", "Enter Marks" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 2;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);

        int secId = Integer.parseInt(sectionId);
        Map<String, Integer> weightMap = sectiondoa.getWeightageForSection(secId);
        for (Map.Entry<String, Integer> entry : weightMap.entrySet()) {
            String comp = entry.getKey();
            int max = entry.getValue();
            int studentid = enrolldoa.getStudentIdFromRoll(roll);
            int enrollmentId = enrolldoa.getEnrollmentIdFromStudentAndSection(studentid, secId);
            double existingMarks = gradedao.getComponentScore(enrollmentId, comp);
            model.addRow(new Object[] { comp, max, existingMarks == -1 ? "" : existingMarks });
        }

        d.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton save = createButton("Save Marks", "SansSerif", Color.WHITE, primary, 16);
        save.addActionListener(e -> {
            int studentId = enrolldoa.getStudentIdFromRoll(roll);
            int enrollmentId = enrolldoa.getEnrollmentIdFromStudentAndSection(studentId, secId);

            System.out.println("DEBUG → roll=" + roll +
                    " studentId=" + studentId +
                    " enrollmentId=" + enrollmentId);

            for (int i = 0; i < model.getRowCount(); i++) {

                String comp = model.getValueAt(i, 0).toString();
                int maxMarks = Integer.parseInt(model.getValueAt(i, 1).toString());
                String input = model.getValueAt(i, 2) == null ? "" : model.getValueAt(i, 2).toString().trim();

                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(d,
                            "Please enter marks for component: " + comp);
                    return;
                }

                double score;
                try {
                    score = Double.parseDouble(input);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(d,
                            "Invalid number entered for " + comp);
                    return;
                }

                if (score < 0 || score > maxMarks) {
                    JOptionPane.showMessageDialog(d,
                            "Marks out of range for: " + comp +
                                    "\nAllowed: 0 to " + maxMarks);
                    return;
                }
                if (gradedao.componentExists(enrollmentId, comp)) {
                    gradedao.updateScore(enrollmentId, comp, score);
                } else {
                    gradedao.insertComponent(enrollmentId, comp, score, maxMarks);
                }
            }

            if (gradedao.allComponentsAssigned(enrollmentId, secId)) {

                String finalLetter = gradedao.computeFinalLetterGrade(enrollmentId, secId);

                if (finalLetter != null) {
                    gradedao.setFinalGrade(enrollmentId, finalLetter);
                    System.out.println("Final grade assigned: " + finalLetter);
                }
            }
            JOptionPane.showMessageDialog(d, "Marks saved successfully!");
            d.dispose();

        });

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.add(save);

        d.add(bottom, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    private JPanel infoCard(String label, String value) {
        Color primaryColor = new Color(0x005A2D);
        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.setBackground(cardBg);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel l1 = new JLabel(label + ": ");
        l1.setFont(new Font("SansSerif", Font.BOLD, 15));
        l1.setForeground(primaryColor);

        JLabel l2 = new JLabel(value);
        l2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        l2.setForeground(new Color(40, 40, 40));

        p.add(l1);
        p.add(l2);

        return p;
    }

    private void openStatsPopup(String courseCode, String courseTitle, String sectionId, String semester) {

        JDialog d = new JDialog((Frame) null, "Section Statistics", true);
        d.setSize(900, 700);
        d.setLocationRelativeTo(null);
        d.setLayout(new BorderLayout());

        JPanel info = new JPanel(new GridLayout(2, 2, 15, 10));
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        info.add(infoCard("Course Code", courseCode));
        info.add(infoCard("Course Title", courseTitle));
        info.add(infoCard("Section ID", sectionId));
        info.add(infoCard("Semester", semester));

        d.add(info, BorderLayout.NORTH);

        String[] cols = { "Component", "Average", "Median", "Mode" };
        DefaultTableModel compModel = new DefaultTableModel(cols, 0);

        int secId = Integer.parseInt(sectionId);
        Map<String, List<Double>> compScores = statdao.getComponentScores(secId);

        for (String comp : compScores.keySet()) {
            List<Double> s = compScores.get(comp);

            compModel.addRow(new Object[] {
                    comp,
                    statdao.average(s),
                    statdao.median(s),
                    statdao.mode(s)
            });
        }

        JTable compTable = new JTable(compModel);
        compTable.setRowHeight(28);

        JPanel compPanel = new JPanel(new BorderLayout());
        compPanel.setBorder(BorderFactory.createTitledBorder("Component-wise Statistics"));
        compPanel.add(new JScrollPane(compTable), BorderLayout.CENTER);

        JPanel overallPanel = new JPanel();
        overallPanel.setLayout(new BoxLayout(overallPanel, BoxLayout.Y_AXIS));
        overallPanel.setBorder(BorderFactory.createTitledBorder("Overall Statistics"));
        overallPanel.setBackground(Color.WHITE);
        List<Double> totals = statdao.getTotalScores(secId);

        overallPanel.removeAll();
        overallPanel.add(infoCard("Average Total", String.format("%.2f", statdao.average(totals))));
        overallPanel.add(Box.createVerticalStrut(8));
        overallPanel.add(infoCard("Median Total", String.format("%.2f", statdao.median(totals))));
        overallPanel.add(Box.createVerticalStrut(8));
        overallPanel.add(infoCard("Mode Total", String.format("%.2f", statdao.mode(totals))));
        overallPanel.add(Box.createVerticalStrut(8));
        overallPanel.add(infoCard("Std Deviation", String.format("%.2f", statdao.stddev(totals))));
        overallPanel.add(Box.createVerticalStrut(8));
        overallPanel.add(infoCard("Total Students", String.valueOf(totals.size())));

        String[] gradeCols = { "Grade", "Count" };
        DefaultTableModel gradeModel = new DefaultTableModel(gradeCols, 0);
        Map<String, Integer> grades = statdao.getGradeDistribution(secId);

        gradeModel.setRowCount(0);

        for (Map.Entry<String, Integer> e : grades.entrySet()) {
            gradeModel.addRow(new Object[] { e.getKey(), e.getValue() });
        }

        JTable gradeTable = new JTable(gradeModel);
        gradeTable.setRowHeight(28);

        JPanel gradePanel = new JPanel(new BorderLayout());
        gradePanel.setBorder(BorderFactory.createTitledBorder("Grade Distribution"));
        gradePanel.add(new JScrollPane(gradeTable), BorderLayout.CENTER);

        JPanel center = new JPanel(new GridLayout(1, 3, 10, 10));
        center.setBackground(Color.WHITE);
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        center.add(compPanel);
        center.add(overallPanel);
        center.add(gradePanel);

        d.add(center, BorderLayout.CENTER);

        JButton closeBtn = createButton("Close", "SansSerif", Color.WHITE, Color.GRAY, 16);
        closeBtn.addActionListener(e -> d.dispose());

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.add(closeBtn);
        d.add(bottom, BorderLayout.SOUTH);

        d.setVisible(true);
    }
}
