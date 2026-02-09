package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import Data.Admindatalayer;
import Data.Instructordatalayer;
import Data.coursedatalayer;
import Data.sectiondatalayer;
import Data.settingsdatalayer;
import domain.Admin;
import domain.Course;
import domain.Instructor;
import domain.Section;
import domain.Student;
import domain.TimeSlot;
import domain.User;
import auth.authhash;
import auth.authsession;
import backup.BackupUtility;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.DayOfWeek;
import java.util.function.BiFunction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JFrame {
    private JPanel topPanel;
    private JPanel sidePanel;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private Color primary = new Color(0x006B3C);
    private Color hover = new Color(0x00A859);

    private JButton menuButton;

    private boolean sidebarVisible = true;

    public Runnable refreshUserTableCallback;
    public Runnable refreshStudentTableCallback;
    private Admindatalayer adminDao = new Admindatalayer();
    private coursedatalayer CourseDao = new coursedatalayer();
    private Instructordatalayer instructordao = new Instructordatalayer();
    private sectiondatalayer sectionDoa = new sectiondatalayer();
    private settingsdatalayer settingsDao = new settingsdatalayer();
    private authhash auth = new authhash();

    public AdminDashboard() {
        setTitle("ERP Management System - Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        topPanel = topBar();
        sidePanel = sideBar();
        mainPanel = mainArea();

        add(mainPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        add(sidePanel, BorderLayout.WEST);
    }

    private boolean isMouseOver(Component comp) {
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouse, comp);
        return comp.contains(mouse);
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

            if (newP.length() < 6) {
                JOptionPane.showMessageDialog(dialog,
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

        menuButton = createButton("\u2630", "SansSerif", Color.WHITE, new Color(0x005A2D), 20);
        JLabel title = creatLabel("Admin Dashboard", "Segoe UI", 20);

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

        String[] buttons = { "HOME", "MANAGE USERS", "MANAGE COURSES", "SEARCH SECTION" };
        sidePanel.add(Box.createVerticalStrut(10));
        for (String text : buttons) {
            JButton btn = createButton(text, "Segoe UI", Color.WHITE, primary, 15);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 20));

            sidePanel.add(btn);
            if (text.equals("MANAGE USERS")) {
                JPanel panel1 = new JPanel();
                panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
                String[] subButtons = { "STUDENT", "INSTRUCTOR", "ADMIN" };
                for (String subText : subButtons) {
                    JButton subBtn = createButton(subText, "Segoe UI", Color.WHITE, primary, 12);
                    subBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
                    subBtn.setHorizontalAlignment(SwingConstants.LEFT);
                    subBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                    subBtn.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 20));

                    panel1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseExited(MouseEvent e) {
                            Timer t = new Timer(200, ev -> {
                                if (!AdminDashboard.this.isMouseOver(subBtn)
                                        && !AdminDashboard.this.isMouseOver(panel1)) {
                                    panel1.setVisible(false);
                                }
                            });
                            t.setRepeats(false);
                            t.start();
                        }
                    });
                    subBtn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            subBtn.setBackground(hover);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            subBtn.setBackground(primary);
                        }
                    });
                    panel1.add(subBtn);
                    subBtn.addActionListener(e -> {
                        switch (subText) {
                            case "STUDENT":
                                cardLayout.show(mainPanel, "ManageStudents");
                                break;
                            case "INSTRUCTOR":
                                cardLayout.show(mainPanel, "ManageInstructors");
                                break;
                            case "ADMIN":
                                cardLayout.show(mainPanel, "ManageAdmin");
                                break;
                            default:
                                break;
                        }
                    });
                }
                panel1.setVisible(false);
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        panel1.setVisible(true);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        Timer t = new Timer(200, ev -> {
                            if (!AdminDashboard.this.isMouseOver(btn) && !AdminDashboard.this.isMouseOver(panel1)) {
                                panel1.setVisible(false);
                            }
                        });
                        t.setRepeats(false);
                        t.start();
                    }

                });
                sidePanel.add(panel1);
            }

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
                    case "MANAGE USERS":
                        cardLayout.show(mainPanel, "ManageUsers");
                        break;
                    case "MANAGE COURSES":
                        cardLayout.show(mainPanel, "ManageCourses");
                        break;
                    case "SEARCH SECTION":
                        cardLayout.show(mainPanel, "SearchSection");
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
        mainPanel.add(createManageUsers(), "ManageUsers");
        mainPanel.add(createStudent(), "ManageStudents");
        mainPanel.add(createInstructor(), "ManageInstructors");
        mainPanel.add(createAdmin(), "ManageAdmin");
        mainPanel.add(createManageCourses(), "ManageCourses");
        mainPanel.add(createSearchSections(), "SearchSection");
        mainPanel.add(createSearchUser(), "SearchUserScreen");

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

        JLabel welcome = creatLabel("Hello, ADMIN!  Welcome to IIIT Delhi.", "SansSerif", 20);
        welcome.setForeground(new Color(0x005A2D));
        welcome.setHorizontalAlignment(SwingConstants.LEFT);
        welcome.setVerticalAlignment(SwingConstants.CENTER);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(welcome);

        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        middlePanel.setBackground(Color.WHITE);

        JButton backupBtn = new JButton("Backup");
        backupBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backupBtn.setBackground(new Color(0x005A2D));
        backupBtn.setForeground(Color.WHITE);
        backupBtn.setFocusPainted(false);
        backupBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backupBtn.setPreferredSize(new Dimension(120, 35));

        JButton restoreBtn = new JButton("Restore");
        restoreBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        restoreBtn.setBackground(new Color(0x005A2D));
        restoreBtn.setForeground(Color.WHITE);
        restoreBtn.setFocusPainted(false);
        restoreBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restoreBtn.setPreferredSize(new Dimension(120, 35));

        middlePanel.add(backupBtn);
        middlePanel.add(restoreBtn);
        maintenancePanel.add(middlePanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);

        JLabel mainMode = creatLabel("Set Maintenance Mode", "SansSerif", 18);
        mainMode.setForeground(new Color(0x333333));
        boolean isMaintenanceOn = settingsDao.isMaintenanceOn();
        String toggleText = isMaintenanceOn ? "ON" : "OFF";
        JToggleButton mainToggle = new JToggleButton(toggleText);
        mainToggle.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainToggle.setFocusPainted(false);
        mainToggle.setForeground(Color.WHITE);
        mainToggle.setOpaque(true);
        mainToggle.setContentAreaFilled(true);
        mainToggle.setBorderPainted(false);
        mainToggle.setUI(new javax.swing.plaf.basic.BasicToggleButtonUI());
        if (toggleText.equals("ON")) {
            mainToggle.setBackground(new Color(0x388E3C)); // Green
        } else {
            mainToggle.setBackground(new Color(0xD32F2F)); // Red
        }
        mainToggle.setPreferredSize(new Dimension(90, 35));
        mainToggle.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        mainToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        mainToggle.addItemListener(e -> {
            boolean isOn = (e.getStateChange() == ItemEvent.SELECTED);
            if (isOn) {
                mainToggle.setText("ON");
                mainToggle.setBackground(new Color(0x388E3C)); // Green
            } else {
                mainToggle.setText("OFF");
                mainToggle.setBackground(new Color(0xD32F2F)); // Red
            }

            settingsDao.toggleMaintenanceMode(isOn);
        });

        rightPanel.add(mainMode);
        rightPanel.add(mainToggle);

        maintenancePanel.add(leftPanel, BorderLayout.WEST);
        maintenancePanel.add(rightPanel, BorderLayout.EAST);

        outerWrapper.add(maintenancePanel, BorderLayout.CENTER);

        JPanel SearchUser = new JPanel(new BorderLayout());
        SearchUser.setBackground(Color.WHITE);
        SearchUser.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));

        JPanel top1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top1.setBackground(Color.WHITE);
        top1.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        JLabel name1 = creatLabel("Admin Details", "SansSerif", 20);
        name1.setForeground(new Color(0x005A2D));
        top1.setBackground(Color.LIGHT_GRAY);
        top1.add(name1);
        SearchUser.add(top1, BorderLayout.NORTH);

        JPanel ManageStudents = new JPanel(new BorderLayout());
        ManageStudents.setBackground(Color.WHITE);
        ManageStudents.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));

        JPanel top2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top2.setBackground(Color.WHITE);
        top2.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        JLabel name2 = creatLabel("Instructors Details", "SansSerif", 20);
        name2.setForeground(new Color(0x005A2D));
        top2.setBackground(Color.LIGHT_GRAY);
        top2.add(name2);
        ManageStudents.add(top2, BorderLayout.NORTH);

        JPanel ManageInstructors = new JPanel(new BorderLayout());
        ManageInstructors.setBackground(Color.WHITE);
        ManageInstructors.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));

        JPanel top3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top3.setBackground(Color.WHITE);
        top3.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        JLabel name3 = creatLabel("Students Details", "SansSerif", 20);
        name3.setForeground(new Color(0x005A2D));
        top3.setBackground(Color.LIGHT_GRAY);
        top3.add(name3);
        ManageInstructors.add(top3, BorderLayout.NORTH);

        JPanel ManageAdmins = new JPanel(new BorderLayout());
        ManageAdmins.setBackground(Color.WHITE);
        ManageAdmins.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));

        JPanel top4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top4.setBackground(Color.WHITE);
        top4.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        JLabel name4 = creatLabel("Course/Section Details", "SansSerif", 20);
        name4.setForeground(new Color(0x005A2D));
        top4.setBackground(Color.LIGHT_GRAY);
        top4.add(name4);
        ManageAdmins.add(top4, BorderLayout.NORTH);

        JPanel masterJPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        masterJPanel.setBackground(Color.WHITE);
        masterJPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        masterJPanel.add(SearchUser);
        masterJPanel.add(ManageStudents);
        masterJPanel.add(ManageInstructors);
        masterJPanel.add(ManageAdmins);

        homePanel.add(outerWrapper, BorderLayout.NORTH);
        homePanel.add(masterJPanel, BorderLayout.CENTER);

        backupBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("ERP_Backup.sql"));

            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String file = chooser.getSelectedFile().getAbsolutePath();

                boolean ok = BackupUtility.backup("erp_main", file);

                JOptionPane.showMessageDialog(null,
                        ok ? "Backup Completed!" : "Backup Failed!");
            }
        });
        restoreBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String file = chooser.getSelectedFile().getAbsolutePath();

                boolean ok = BackupUtility.restore("erp_main", file);

                JOptionPane.showMessageDialog(null,
                        ok ? "Restore Completed!" : "Restore Failed!");
            }
        });

        return homePanel;
    }

    public JPanel createManageUsers() {
        JPanel manageUsers = new JPanel(new BorderLayout());
        manageUsers.setBackground(Color.WHITE);

        JPanel SearchUser = new JPanel(new BorderLayout());
        SearchUser.setBackground(new Color(0x3FADA8));
        SearchUser.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));

        JLabel Icon1 = creatLabel("\uD83D\uDD0D", "Segoe UI Emoji", 125);
        Icon1.setForeground(Color.WHITE);
        Icon1.setAlignmentX(Component.CENTER_ALIGNMENT);
        Icon1.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        JLabel name1 = creatLabel("SEARCH USER", "SansSerif", 36);
        name1.setForeground(Color.WHITE);
        name1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel temp1 = new JPanel();
        temp1.setLayout(new BoxLayout(temp1, BoxLayout.Y_AXIS));
        temp1.setBackground(new Color(0x3FADA8));
        temp1.add(Box.createVerticalGlue());
        temp1.add(Icon1);
        temp1.add(Box.createRigidArea(new Dimension(0, 10)));
        temp1.add(name1);
        temp1.add(Box.createVerticalGlue());

        SearchUser.add(temp1, BorderLayout.CENTER);
        SearchUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        SearchUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                temp1.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                temp1.setBackground(new Color(0x3FADA8));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "SearchUserScreen");
            }
        });

        JPanel ManageStudents = new JPanel(new BorderLayout());
        ManageStudents.setBackground(new Color(0x3FADA8));
        ManageStudents.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));
        JLabel Icon2 = creatLabel("\uD83C\uDF93", "Segoe UI Emoji", 125);
        Icon2.setForeground(Color.WHITE);
        Icon2.setAlignmentX(Component.CENTER_ALIGNMENT);
        Icon2.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        JLabel name2 = creatLabel("MANAGE STUDENTS", "SansSerif", 36);
        name2.setForeground(Color.WHITE);
        name2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel temp2 = new JPanel();
        temp2.setLayout(new BoxLayout(temp2, BoxLayout.Y_AXIS));
        temp2.setBackground(new Color(0x3FADA8));
        temp2.add(Box.createVerticalGlue());
        temp2.add(Icon2);
        temp2.add(Box.createRigidArea(new Dimension(0, 10)));
        temp2.add(name2);
        temp2.add(Box.createVerticalGlue());

        ManageStudents.add(temp2, BorderLayout.CENTER);
        ManageStudents.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ManageStudents.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                temp2.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                temp2.setBackground(new Color(0x3FADA8));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "ManageStudents");
            }
        });

        JPanel ManageInstructors = new JPanel(new BorderLayout());
        ManageInstructors.setBackground(new Color(0x3FADA8));
        ManageInstructors.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));

        JLabel Icon3 = creatLabel("\u2712", "Segoe UI Emoji", 125);
        Icon3.setForeground(Color.WHITE);
        Icon3.setAlignmentX(Component.CENTER_ALIGNMENT);
        Icon3.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel name3 = creatLabel("MANAGE INSTRUCTORS", "SansSerif", 36);
        name3.setForeground(Color.WHITE);
        name3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel temp3 = new JPanel();
        temp3.setLayout(new BoxLayout(temp3, BoxLayout.Y_AXIS));
        temp3.setBackground(new Color(0x3FADA8));
        temp3.add(Box.createVerticalGlue());
        temp3.add(Icon3);
        temp3.add(Box.createRigidArea(new Dimension(0, 10)));
        temp3.add(name3);
        temp3.add(Box.createVerticalGlue());

        ManageInstructors.add(temp3, BorderLayout.CENTER);
        ManageInstructors.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ManageInstructors.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                temp3.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                temp3.setBackground(new Color(0x3FADA8));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "ManageInstructors");
            }
        });

        JPanel ManageAdmins = new JPanel(new BorderLayout());
        ManageAdmins.setBackground(new Color(0x3FADA8));
        ManageAdmins.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));

        JLabel Icon4 = creatLabel("\uD83C\uDFDB", "Segoe UI Emoji", 125);
        Icon4.setForeground(Color.WHITE);
        Icon4.setAlignmentX(Component.CENTER_ALIGNMENT);
        Icon4.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel name4 = creatLabel("MANAGE ADMINS", "SansSerif", 36);
        name4.setForeground(Color.WHITE);
        name4.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel temp4 = new JPanel();
        temp4.setLayout(new BoxLayout(temp4, BoxLayout.Y_AXIS));
        temp4.setBackground(new Color(0x3FADA8));
        temp4.add(Box.createVerticalGlue());
        temp4.add(Icon4);
        temp4.add(Box.createRigidArea(new Dimension(0, 10)));
        temp4.add(name4);
        temp4.add(Box.createVerticalGlue());

        ManageAdmins.add(temp4, BorderLayout.CENTER);
        ManageAdmins.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ManageAdmins.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                temp4.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                temp4.setBackground(new Color(0x3FADA8));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "ManageAdmin");
            }
        });

        JPanel masterJPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        masterJPanel.setBackground(Color.WHITE);
        masterJPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        masterJPanel.add(SearchUser);
        masterJPanel.add(ManageStudents);
        masterJPanel.add(ManageInstructors);
        masterJPanel.add(ManageAdmins);

        manageUsers.add(masterJPanel, BorderLayout.CENTER);

        return manageUsers;
    }

    public void loadAllUsersIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<User> users = adminDao.getAllUsers();

        for (User u : users) {
            model.addRow(new Object[] {
                    u.getUserID(),
                    u.getUserName(),
                    u.getRole()
            });
        }
    }

    public DefaultTableModel usermodel;

    public JPanel createSearchUser() {
        JPanel searchUserPanel = new JPanel(new BorderLayout());
        searchUserPanel.setBackground(Color.WHITE);

        JPanel filterDataPanel = new JPanel(new GridBagLayout());
        filterDataPanel.setBackground(Color.WHITE);
        filterDataPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        filterDataPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel rolePanel = new JPanel();
        rolePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        rolePanel.setBackground(cardBg);
        rolePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel roleLabel = creatLabel("Role:", "SansSerif", 16);
        roleLabel.setForeground(new Color(0x005A2D));

        JLabel roleBox = creatLabel("All", "SansSerif", 16);
        roleBox.setForeground(Color.BLACK);

        rolePanel.add(roleLabel);
        rolePanel.add(roleBox);

        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        usernamePanel.setBackground(cardBg);
        usernamePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel usernameLabel = creatLabel("User Name:", "SansSerif", 16);
        usernameLabel.setForeground(new Color(0x005A2D));

        JTextField usernameField = new JTextField(15);
        usernameField.setPreferredSize(new Dimension(180, 30));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 15));

        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(0x005A2D));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.setPreferredSize(new Dimension(120, 40));
        searchBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterDataPanel.add(rolePanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        filterDataPanel.add(usernamePanel, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        filterDataPanel.add(searchBtn, gbc);

        JScrollPane userDataPanel = new JScrollPane();
        String[] tableColumns = { "User ID", "User Name", "Role" };
        Object[][] getData = {};

        usermodel = new DefaultTableModel(getData, tableColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        loadAllUsersIntoTable(usermodel);
        JTable dataTable = new JTable(usermodel);
        dataTable.setRowHeight(30);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JPanel addNewUserPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addNewUserPanel.setBackground(Color.WHITE);
        addNewUserPanel.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, new Color(0xE0E0E0)));

        searchBtn.addActionListener(e -> {
            String fetch_userName = usernameField.getText().trim();
            DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
            model.setRowCount(0);
            if (fetch_userName.isEmpty()) {
                List<User> ulist = adminDao.getAllUsers();

                for (User u : ulist) {
                    model.addRow(new Object[] {
                            u.getUserID(),
                            u.getUserName(),
                            u.getRole()
                    });
                }
                return;
            }
            User user = adminDao.getUserByUsername(fetch_userName);
            if (user == null) {
                JOptionPane.showMessageDialog(null, "No user found!");
                return;
            }
            model.addRow(new Object[] {
                    user.getUserID(),
                    user.getUserName(),
                    user.getRole()
            });
        });

        userDataPanel.setViewportView(dataTable);

        searchUserPanel.add(filterDataPanel, BorderLayout.NORTH);
        searchUserPanel.add(userDataPanel, BorderLayout.CENTER);
        searchUserPanel.add(addNewUserPanel, BorderLayout.SOUTH);

        return searchUserPanel;
    }

    public void openPopup(JFrame parent, JPanel content, String title) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(content);
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public JPanel getAddStudentForm() {

        JPanel addStudentPanel = new JPanel(new GridBagLayout());
        addStudentPanel.setBackground(Color.WHITE);
        addStudentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel panel = new JPanel(new BorderLayout(10, 5));
            panel.setBackground(cardBg);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));

            JLabel lbl = creatLabel(label, "SansSerif", 15);
            lbl.setForeground(new Color(0x005A2D));

            panel.add(lbl, BorderLayout.NORTH);
            panel.add(input, BorderLayout.CENTER);
            return panel;
        };

        JTextField usernameField = new JTextField(15);
        JTextField studentNameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField rollField = new JTextField(15);

        JComboBox<String> programBox = new JComboBox<>(new String[] {
                "B.Tech", "M.Tech", "PhD"
        });

        JComboBox<String> deptBox = new JComboBox<>(new String[] {
                "CSE", "ECE", "CSB", "CSAI", "CSSS", "CB", "CSEcon",
        });

        JComboBox<String> semBox = new JComboBox<>(new String[] {
                "Semester 1", "Semester 2", "Semester 3", "Semester 4",
                "Semester 5", "Semester 6", "Semester 7", "Semester 8"
        });

        JTextField yearField = new JTextField(15);

        JPanel p1 = makeField.apply("Set Username *", usernameField);
        JPanel p2 = makeField.apply("Set Student Name *", studentNameField);
        JPanel p3 = makeField.apply("Set Password *", passwordField);
        JPanel p4 = makeField.apply("Roll Number *", rollField);
        JPanel p5 = makeField.apply("Program *", programBox);
        JPanel p6 = makeField.apply("Department *", deptBox);
        JPanel p7 = makeField.apply("Semester *", semBox);
        JPanel p8 = makeField.apply("Year *", yearField);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        addStudentPanel.add(p1, gbc);
        gbc.gridx = 1;
        addStudentPanel.add(p2, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        addStudentPanel.add(p3, gbc);
        gbc.gridx = 1;
        addStudentPanel.add(p4, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        addStudentPanel.add(p5, gbc);
        gbc.gridx = 1;
        addStudentPanel.add(p6, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        addStudentPanel.add(p7, gbc);
        gbc.gridx = 1;
        addStudentPanel.add(p8, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(0x005A2D));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(120, 40));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(120, 40));

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addStudentPanel.add(btnPanel, gbc);

        cancelBtn.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(addStudentPanel).dispose();
        });

        saveBtn.addActionListener(e -> {
            if (usernameField.getText().trim().isEmpty() ||
                    studentNameField.getText().trim().isEmpty() ||
                    rollField.getText().trim().isEmpty() ||
                    new String(passwordField.getPassword()).trim().isEmpty() ||
                    yearField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(addStudentPanel,
                        "All fields marked * are mandatory!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = usernameField.getText().trim();
            String name = studentNameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String rollStr = rollField.getText().trim();
            String program = (String) programBox.getSelectedItem();
            String dept = (String) deptBox.getSelectedItem();
            int semester = semBox.getSelectedIndex() + 1;
            int year = Integer.parseInt(yearField.getText().trim());
            int roll = Integer.parseInt(rollStr);
            boolean ok = adminDao.addStudentUser(username, password, name, roll, program, dept, semester, year);
            if (!ok) {
                JOptionPane.showMessageDialog(addStudentPanel,
                        "Failed to add student. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(addStudentPanel, "Student added successfully!");
            loadAllUsersIntoTable(usermodel);
            loadAllStudentsIntoTable(studentmodel);

            SwingUtilities.getWindowAncestor(addStudentPanel).dispose();

            JOptionPane.showMessageDialog(addStudentPanel, "Student added successfully!");
            SwingUtilities.getWindowAncestor(addStudentPanel).dispose();
        });

        return addStudentPanel;
    }

    public void loadAllStudentsIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Student> all = adminDao.getAllStudents();

        for (Student st : all) {
            model.addRow(new Object[] {
                    st.getRollNumber(),
                    st.getName(),
                    st.getDepartment(),
                    st.getSemester(),
                    st.getCGPA()
            });
        }

    }

    public DefaultTableModel studentmodel;

    public JPanel createStudent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        rolePanel.setBackground(cardBg);
        rolePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel roleLabel = creatLabel("Role:", "SansSerif", 16);
        roleLabel.setForeground(new Color(0x005A2D));

        JLabel fixedRoleValue = creatLabel("Student", "SansSerif", 16);
        fixedRoleValue.setForeground(Color.BLACK);

        rolePanel.add(roleLabel);
        rolePanel.add(fixedRoleValue);

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        idPanel.setBackground(cardBg);
        idPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel idLabel = creatLabel("Roll Number:", "SansSerif", 16);
        idLabel.setForeground(new Color(0x005A2D));

        JTextField idField = new JTextField(15);
        idField.setPreferredSize(new Dimension(150, 30));

        idPanel.add(idLabel);
        idPanel.add(idField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setPreferredSize(new Dimension(120, 40));
        searchBtn.setBackground(new Color(0x005A2D));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(rolePanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        filterPanel.add(idPanel, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        filterPanel.add(searchBtn, gbc);

        String[] studentColumns = { "Roll Number", "Student Name", "Branch", "Semester", "CGPA" };
        Object[][] getData = {};

        studentmodel = new DefaultTableModel(getData, studentColumns) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        loadAllStudentsIntoTable(studentmodel);

        JTable table = new JTable(studentmodel);
        table.setRowHeight(30);

        JScrollPane scroll = new JScrollPane(table);

        searchBtn.addActionListener(e -> {
            studentmodel.setRowCount(0);
            String fetch_roll = idField.getText().trim();
            if (fetch_roll.isEmpty()) {
                List<Student> students = adminDao.getAllStudents();
                for (Student st : students) {
                    studentmodel.addRow(new Object[] {
                            st.getRollNumber(),
                            st.getName(),
                            st.getDepartment(),
                            st.getSemester(),
                            st.getCGPA()
                    });
                }
                return;
            }
            Student student = adminDao.getStudentByRollNo(fetch_roll);
            if (student == null) {
                JOptionPane.showMessageDialog(null, "No student found!");
                return;
            }
            studentmodel.addRow(new Object[] {
                    student.getRollNumber(),
                    student.getName(),
                    student.getDepartment(),
                    student.getSemester(),
                    student.getCGPA()
            });

        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, new Color(0xE0E0E0)));

        JButton addStudentBtn = new JButton("+ Add New Student");
        addStudentBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        addStudentBtn.setBackground(new Color(0x005A2D));
        addStudentBtn.setForeground(Color.WHITE);
        addStudentBtn.setFocusPainted(false);
        addStudentBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addStudentBtn.setPreferredSize(new Dimension(200, 40));
        bottomPanel.add(addStudentBtn);

        addStudentBtn.addActionListener(e -> {
            JPanel addForm = getAddStudentForm();
            openPopup((JFrame) SwingUtilities.getWindowAncestor(panel), addForm, "Add Student");
        });

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    public JPanel getAddInstructorForm() {
        JPanel addInstructorPanel = new JPanel(new GridBagLayout());
        addInstructorPanel.setBackground(Color.WHITE);
        addInstructorPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new BorderLayout(10, 5));
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));

            JLabel lbl = creatLabel(label, "SansSerif", 15);
            lbl.setForeground(new Color(0x005A2D));

            p.add(lbl, BorderLayout.NORTH);
            p.add(input, BorderLayout.CENTER);
            return p;
        };

        JTextField usernameField = new JTextField(15);
        JTextField instructorNameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JComboBox<String> deptBox = new JComboBox<>(new String[] {
                "Computer Science", "Electronics", "Mathematics", "Design", "SSH"
        });

        JComboBox<String> designationBox = new JComboBox<>(new String[] {
                "Assistant Professor",
                "Associate Professor",
                "Professor",
                "Visiting Faculty",
        });

        JPanel p1 = makeField.apply("Set Username *", usernameField);
        JPanel p2 = makeField.apply("Instructor Name *", instructorNameField);

        JPanel p3 = makeField.apply("Set Password *", passwordField);
        JPanel p4 = makeField.apply("Department *", deptBox);

        JPanel p5 = makeField.apply("Designation *", designationBox);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        addInstructorPanel.add(p1, gbc);
        gbc.gridx = 1;
        addInstructorPanel.add(p2, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        addInstructorPanel.add(p3, gbc);
        gbc.gridx = 1;
        addInstructorPanel.add(p4, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        addInstructorPanel.add(p5, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(0x005A2D));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(120, 40));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(120, 40));

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addInstructorPanel.add(btnPanel, gbc);

        cancelBtn.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(addInstructorPanel).dispose();
        });

        saveBtn.addActionListener(e -> {

            if (usernameField.getText().trim().isEmpty() ||
                    instructorNameField.getText().trim().isEmpty() ||
                    new String(passwordField.getPassword()).trim().isEmpty()) {

                JOptionPane.showMessageDialog(addInstructorPanel,
                        "All fields marked * are mandatory!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = usernameField.getText().trim();
            String name = instructorNameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String dept = deptBox.getSelectedItem().toString();
            String designation = designationBox.getSelectedItem().toString();
            boolean success = adminDao.addInstructorUser(username, password, name, dept, designation);
            if (!success) {
                JOptionPane.showMessageDialog(addInstructorPanel,
                        "Error adding instructor!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(addInstructorPanel, "Instructor added successfully!");
            loadAllUsersIntoTable(usermodel);
            loadAllInstructorIntoTable(instructormodel);
            JOptionPane.showMessageDialog(addInstructorPanel, "Instructor added successfully!");
            SwingUtilities.getWindowAncestor(addInstructorPanel).dispose();
        });

        return addInstructorPanel;
    }

    public DefaultTableModel instructormodel;

    public void loadAllInstructorIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Instructor> all = adminDao.getAllInstructors();

        for (Instructor st : all) {
            model.addRow(new Object[] {
                    st.getName(),
                    st.getDepartment(),
                    st.getDesignation()
            });
        }

    }

    public JPanel createInstructor() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        rolePanel.setBackground(cardBg);
        rolePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel roleLabel = creatLabel("Role:", "SansSerif", 16);
        roleLabel.setForeground(new Color(0x005A2D));

        JLabel fixedRoleValue = creatLabel("Instructor", "SansSerif", 16);
        fixedRoleValue.setForeground(Color.BLACK);

        rolePanel.add(roleLabel);
        rolePanel.add(fixedRoleValue);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        namePanel.setBackground(cardBg);
        namePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel nameLabel = creatLabel("Instructor Name:", "SansSerif", 16);
        nameLabel.setForeground(new Color(0x005A2D));

        JTextField nameField = new JTextField(15);
        nameField.setPreferredSize(new Dimension(180, 30));

        namePanel.add(nameLabel);
        namePanel.add(nameField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setPreferredSize(new Dimension(120, 40));
        searchBtn.setBackground(new Color(0x005A2D));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(rolePanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        filterPanel.add(namePanel, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        filterPanel.add(searchBtn, gbc);

        String[] columns = { "Instructor Name", "Department", "Designation" };

        instructormodel = new DefaultTableModel(new Object[][] {}, columns) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        loadAllInstructorIntoTable(instructormodel);
        JTable table = new JTable(instructormodel);
        table.setRowHeight(30);

        JScrollPane scroll = new JScrollPane(table);

        searchBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            instructormodel.setRowCount(0);

            List<Instructor> instructors;

            if (name.isEmpty()) {
                instructors = adminDao.getAllInstructors();
            } else {
                instructors = adminDao.getInstructorsByName(name);
            }
            if (instructors.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No instructors found!");
                return;
            }
            for (Instructor i : instructors) {
                instructormodel.addRow(new Object[] {
                        i.getName(),
                        i.getDepartment(),
                        i.getDesignation()
                });
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, new Color(0xE0E0E0)));

        JButton addInstructorBtn = new JButton("+ Add New Instructor");
        addInstructorBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        addInstructorBtn.setBackground(new Color(0x005A2D));
        addInstructorBtn.setForeground(Color.WHITE);
        addInstructorBtn.setFocusPainted(false);
        addInstructorBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addInstructorBtn.setPreferredSize(new Dimension(200, 40));

        bottomPanel.add(addInstructorBtn);

        addInstructorBtn.addActionListener(ev -> {
            JPanel form = getAddInstructorForm();
            openPopup((JFrame) SwingUtilities.getWindowAncestor(panel), form, "Add Instructor");
        });

        panel.add(bottomPanel, BorderLayout.SOUTH);
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    public JPanel getAddAdminForm() {

        JPanel addAdminPanel = new JPanel(new GridBagLayout());
        addAdminPanel.setBackground(Color.WHITE);
        addAdminPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new BorderLayout(10, 5));
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));

            JLabel lbl = creatLabel(label, "SansSerif", 15);
            lbl.setForeground(new Color(0x005A2D));

            p.add(lbl, BorderLayout.NORTH);
            p.add(input, BorderLayout.CENTER);
            return p;
        };

        JTextField usernameField = new JTextField(15);
        JTextField adminNameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JComboBox<String> roleBox = new JComboBox<>(new String[] {
                "Admin B.Tech",
                "Admin M.Tech",
                "Admin Scholarship"
        });

        JPanel p1 = makeField.apply("Set Username *", usernameField);
        JPanel p2 = makeField.apply("Admin Name *", adminNameField);

        JPanel p3 = makeField.apply("Set Password *", passwordField);
        JPanel p4 = makeField.apply("Admin Role *", roleBox);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        addAdminPanel.add(p1, gbc);
        gbc.gridx = 1;
        addAdminPanel.add(p2, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        addAdminPanel.add(p3, gbc);
        gbc.gridx = 1;
        addAdminPanel.add(p4, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(0x005A2D));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(120, 40));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(120, 40));

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addAdminPanel.add(btnPanel, gbc);

        cancelBtn.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(addAdminPanel).dispose();
        });

        saveBtn.addActionListener(e -> {

            if (usernameField.getText().trim().isEmpty() ||
                    adminNameField.getText().trim().isEmpty() ||
                    new String(passwordField.getPassword()).trim().isEmpty()) {

                JOptionPane.showMessageDialog(addAdminPanel,
                        "All fields marked * are mandatory!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = usernameField.getText().trim();
            // String adminName = adminNameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            boolean ok = adminDao.addAdminUser(username, password);

            if (!ok) {
                JOptionPane.showMessageDialog(addAdminPanel,
                        "Error adding admin! Username may already exist.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(addAdminPanel, "Admin added successfully!");
            loadAllUsersIntoTable(usermodel);
            loadAllAdminIntoTable(adminmodel);
            SwingUtilities.getWindowAncestor(addAdminPanel).dispose();
        });

        return addAdminPanel;
    }

    DefaultTableModel adminmodel;

    public void loadAllAdminIntoTable(DefaultTableModel model) {
        model.setRowCount(0);

        List<Admin> all = adminDao.getAllAdmins();

        for (Admin st : all) {
            model.addRow(new Object[] {
                    st.getUserID(),
                    st.getName(),
                    st.getRole()
            });
        }

    }

    public JPanel createAdmin() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        rolePanel.setBackground(cardBg);
        rolePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel roleLabel = creatLabel("Role:", "SansSerif", 16);
        roleLabel.setForeground(new Color(0x005A2D));

        JLabel roleValue = creatLabel("Admin", "SansSerif", 16);
        roleValue.setForeground(Color.BLACK);

        rolePanel.add(roleLabel);
        rolePanel.add(roleValue);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        namePanel.setBackground(cardBg);
        namePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel nameLabel = creatLabel("Admin Name:", "SansSerif", 16);
        nameLabel.setForeground(new Color(0x005A2D));

        JTextField nameField = new JTextField(15);
        nameField.setPreferredSize(new Dimension(180, 30));

        namePanel.add(nameLabel);
        namePanel.add(nameField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setPreferredSize(new Dimension(120, 40));
        searchBtn.setBackground(new Color(0x005A2D));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(rolePanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        filterPanel.add(namePanel, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        filterPanel.add(searchBtn, gbc);

        String[] cols = { "Admin ID", "Admin Name", "Admin Role" };

        adminmodel = new DefaultTableModel(new Object[][] {}, cols) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        loadAllAdminIntoTable(adminmodel);

        JTable table = new JTable(adminmodel);
        table.setRowHeight(30);

        JScrollPane scroll = new JScrollPane(table);

        searchBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            adminmodel.setRowCount(0);

            List<Admin> admins;
            if (name.isEmpty()) {
                admins = adminDao.getAllAdmins();
            } else {
                admins = adminDao.getAdminsByName(name);
            }
            if (admins.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No Admins found!");
                return;
            }
            System.out.println("Found admins: " + admins.size());
            for (Admin i : admins) {
                adminmodel.addRow(new Object[] {
                        i.getUserID(),
                        i.getName(),
                        i.getRole()
                });
            }
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, new Color(0xE0E0E0)));

        JButton addAdminBtn = new JButton("+ Add Admin");
        addAdminBtn.setPreferredSize(new Dimension(200, 40));
        addAdminBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        addAdminBtn.setBackground(new Color(0x005A2D));
        addAdminBtn.setForeground(Color.WHITE);

        bottomPanel.add(addAdminBtn);

        addAdminBtn.addActionListener(e -> {
            JPanel form = getAddAdminForm();
            openPopup((JFrame) SwingUtilities.getWindowAncestor(panel), form, "Add Admin");
        });

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    public JPanel getAddCourseForm() {

        JPanel addCoursePanel = new JPanel(new GridBagLayout());
        addCoursePanel.setBackground(Color.WHITE);
        addCoursePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new BorderLayout(10, 5));
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));

            JLabel lbl = creatLabel(label, "SansSerif", 15);
            lbl.setForeground(new Color(0x005A2D));
            p.add(lbl, BorderLayout.NORTH);
            p.add(input, BorderLayout.CENTER);
            return p;
        };

        JTextField courseCodeField = new JTextField(15);
        JTextField courseTitleField = new JTextField(15);
        JTextField courseCreditsField = new JTextField(15);

        JPanel p1 = makeField.apply("Course Code *", courseCodeField);
        JPanel p2 = makeField.apply("Course Title *", courseTitleField);
        JPanel p3 = makeField.apply("Course Credits *", courseCreditsField);

        gbc.gridx = 1;
        gbc.gridy = 0;
        addCoursePanel.add(p1, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        addCoursePanel.add(p2, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addCoursePanel.add(p3, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(0x005A2D));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(120, 40));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(120, 40));

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addCoursePanel.add(btnPanel, gbc);

        cancelBtn.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(addCoursePanel).dispose();
        });

        saveBtn.addActionListener(e -> {

            String code = courseCodeField.getText().trim();
            String title = courseTitleField.getText().trim();
            String credits = courseCreditsField.getText().trim();

            if (code.isEmpty() || title.isEmpty() || credits.isEmpty()) {
                JOptionPane.showMessageDialog(addCoursePanel,
                        "All fields marked * are mandatory!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int cr;
            try {
                cr = Integer.parseInt(credits);
                if (cr <= 0)
                    throw new Exception();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addCoursePanel,
                        "Credits must be a positive number.",
                        "Invalid Credits",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Course c = new Course(code, title, cr);

            boolean ok = CourseDao.addCourse(c);

            if (!ok) {
                JOptionPane.showMessageDialog(addCoursePanel,
                        "Failed to add course! Code already exists or database issue.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            loadAllCoursesIntoTable(CourseModel);

            JOptionPane.showMessageDialog(addCoursePanel,
                    "Course added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            SwingUtilities.getWindowAncestor(addCoursePanel).dispose();
        });

        return addCoursePanel;
    }

    public JPanel getEditCourseForm(String courseCode, String courseTitle, String courseCredits) {

        JPanel editCoursePanel = new JPanel(new GridBagLayout());
        editCoursePanel.setBackground(Color.WHITE);
        editCoursePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new BorderLayout(10, 5));
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));

            JLabel lbl = creatLabel(label, "SansSerif", 15);
            lbl.setForeground(new Color(0x005A2D));
            p.add(lbl, BorderLayout.NORTH);
            p.add(input, BorderLayout.CENTER);
            return p;
        };

        JTextField codeField = new JTextField(15);
        codeField.setText(courseCode);
        codeField.setEditable(false);
        codeField.setBackground(Color.LIGHT_GRAY);

        JTextField titleField = new JTextField(15);
        titleField.setText(courseTitle);

        JTextField creditsField = new JTextField(15);
        creditsField.setText(courseCredits);

        JPanel p1 = makeField.apply("Course Code", codeField);
        JPanel p2 = makeField.apply("Course Title *", titleField);
        JPanel p3 = makeField.apply("Course Credits *", creditsField);

        gbc.gridx = 1;
        gbc.gridy = 0;
        editCoursePanel.add(p1, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        editCoursePanel.add(p2, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        editCoursePanel.add(p3, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(0x005A2D));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(120, 40));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(120, 40));

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        editCoursePanel.add(btnPanel, gbc);

        cancelBtn.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(editCoursePanel).dispose();
        });
        saveBtn.addActionListener(e -> {

            String title = titleField.getText().trim();
            String credits = creditsField.getText().trim();

            if (title.isEmpty() || credits.isEmpty()) {
                JOptionPane.showMessageDialog(editCoursePanel,
                        "Fields marked * are mandatory!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int cr;
            try {
                cr = Integer.parseInt(credits);
                if (cr <= 0)
                    throw new Exception();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editCoursePanel,
                        "Credits must be a positive number!",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean ok = CourseDao.updateCourse(title, cr, courseCode);

            if (!ok) {
                JOptionPane.showMessageDialog(editCoursePanel,
                        "Failed to update course!",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            loadAllCoursesIntoTable(CourseModel);
            JOptionPane.showMessageDialog(editCoursePanel,
                    "Course updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            SwingUtilities.getWindowAncestor(editCoursePanel).dispose();
        });

        return editCoursePanel;
    }

    private java.time.DayOfWeek mapDay(String shortDay) {
        switch (shortDay.toUpperCase()) {
            case "MON":
                return java.time.DayOfWeek.MONDAY;
            case "TUE":
                return java.time.DayOfWeek.TUESDAY;
            case "WED":
                return java.time.DayOfWeek.WEDNESDAY;
            case "THU":
                return java.time.DayOfWeek.THURSDAY;
            case "FRI":
                return java.time.DayOfWeek.FRIDAY;
            case "SAT":
                return java.time.DayOfWeek.SATURDAY;
            case "SUN":
                return java.time.DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException("Invalid day: " + shortDay);
        }
    }

    private Map<java.time.DayOfWeek, domain.TimeSlot> parseDayTime(String input) {
        Map<java.time.DayOfWeek, domain.TimeSlot> table = new HashMap<>();

        try {

            String[] parts = input.split(" ", 2);

            if (parts.length == 2) {
                String shortDay = parts[0].trim();

                java.time.DayOfWeek day = mapDay(shortDay);

                String timeStr = parts[1].trim();
                domain.TimeSlot slot = domain.TimeSlot.parse(timeStr);

                table.put(day, slot);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    // private String convertTimeTableToString(Map<java.time.DayOfWeek, TimeSlot>
    // map) {
    // if (map == null || map.isEmpty())
    // return "";
    // StringBuilder sb = new StringBuilder();
    // for (var entry : map.entrySet()) {
    // sb.append(entry.getKey().name())
    // .append(":")
    // .append(entry.getValue().toString())
    // .append(",");
    // }
    // return sb.substring(0, sb.length() - 1); // remove last comma
    // }

    public JPanel getAddSectionForm(String courseCode, JTable sectionTable) {

        JPanel addSecPanel = new JPanel(new GridBagLayout());
        addSecPanel.setBackground(Color.WHITE);
        addSecPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);
        Color primary = new Color(0x005A2D);

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new BorderLayout(10, 5));
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));

            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
            lbl.setForeground(primary);

            p.add(lbl, BorderLayout.NORTH);
            p.add(input, BorderLayout.CENTER);
            return p;
        };

        JLabel courseCodeLabel = new JLabel(courseCode);
        courseCodeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JTextField dayTimeField = new JTextField(15);
        JTextField roomField = new JTextField(15);
        JTextField capacityField = new JTextField(15);
        JTextField semesterField = new JTextField(15);
        JTextField yearField = new JTextField(15);
        JTextField currentEnrollField = new JTextField(15);

        JPanel p0 = makeField.apply("Course Code", courseCodeLabel);
        JPanel p1 = makeField.apply("Day & Time *", dayTimeField);
        JPanel p2 = makeField.apply("Room *", roomField);
        JPanel p3 = makeField.apply("Capacity *", capacityField);
        JPanel p4 = makeField.apply("Semester *", semesterField);
        JPanel p5 = makeField.apply("Year *", yearField);
        JPanel p6 = makeField.apply("Current Enrollment", currentEnrollField);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        addSecPanel.add(p0, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        addSecPanel.add(p1, gbc);
        gbc.gridx = 1;
        addSecPanel.add(p2, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        addSecPanel.add(p3, gbc);
        gbc.gridx = 1;
        addSecPanel.add(p4, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        addSecPanel.add(p5, gbc);
        gbc.gridx = 1;
        addSecPanel.add(p6, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(primary);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(120, 40));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(120, 40));

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        addSecPanel.add(btnPanel, gbc);

        cancelBtn.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(addSecPanel).dispose();
        });

        saveBtn.addActionListener(e -> {

            if (dayTimeField.getText().trim().isEmpty() ||
                    roomField.getText().trim().isEmpty() ||
                    capacityField.getText().trim().isEmpty() ||
                    semesterField.getText().trim().isEmpty() ||
                    yearField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(addSecPanel,
                        "Fields marked * are mandatory!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int capacity, year, currentEnroll = 0;
            try {
                capacity = Integer.parseInt(capacityField.getText().trim());
                year = Integer.parseInt(yearField.getText().trim());

                if (!currentEnrollField.getText().trim().isEmpty())
                    currentEnroll = Integer.parseInt(currentEnrollField.getText().trim());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addSecPanel,
                        "Numbers must be valid positive integers.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String dayTimeInput = dayTimeField.getText().trim();
            Map<DayOfWeek, TimeSlot> timetable = parseDayTime(dayTimeInput);
            System.out.println(timetable);
            Map<String, Integer> gradingMap = new HashMap<>();
            String room = roomField.getText().trim();
            int semester = Integer.parseInt(semesterField.getText().trim());
            String sectionid = String.valueOf(CourseDao.getCourseIdByCode(courseCode));
            int courseId = CourseDao.getCourseIdByCode(courseCode);
            Section sec = new Section(
                    courseId,
                    null,
                    sectionid,
                    room,
                    semester,
                    year,
                    capacity,
                    null);

            sec.setCurrentEnrollment(currentEnroll);
            sec.setGradingMap(gradingMap);
            sec.setTimeTable(timetable);

            boolean ok = sectionDoa.addSection(sec);

            if (!ok) {
                JOptionPane.showMessageDialog(addSecPanel,
                        "Failed to add section!",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(addSecPanel,
                    "Section added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadsectiondataincourse(courseCode);
            sectionTable.setModel(sectionincoursemodel);

            SwingUtilities.getWindowAncestor(addSecPanel).dispose();

        });

        return addSecPanel;
    }

    public DefaultTableModel sectionincoursemodel;

    public void loadsectiondataincourse(String courseCode) {
        String[] columns = {
                "Section ID",
                "Day/Time",
                "Room",
                "Semester",
                "Year",
                "Capacity",
                "Current Enrollment",
                "Instructor"
        };

        int courseId = CourseDao.getCourseIdByCode(courseCode);
        List<Section> sections = sectionDoa.getSectionBycouseid(courseId);

        Object[][] sectionsData = new Object[sections.size()][8];

        for (int i = 0; i < sections.size(); i++) {
            Section s = sections.get(i);
            sectionsData[i][0] = s.getSectionID();
            sectionsData[i][1] = s.getTimeTable();
            sectionsData[i][2] = s.getRoom();
            sectionsData[i][3] = s.getSemester();
            sectionsData[i][4] = s.getYear();
            sectionsData[i][5] = s.getSectionCapacity();
            sectionsData[i][6] = s.getCurrentEnrollment();
            int instructorId = Integer.parseInt(s.getInstructorID());
            sectionsData[i][7] = instructordao.getInstructorNameById(instructorId);
        }

        sectionincoursemodel = new DefaultTableModel(sectionsData, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public void ManageSections(String courseCode, String courseTitle, String courseCredits) {
        JDialog dialog = new JDialog((Frame) null, "Manage Sections", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        Color primary = new Color(0x005A2D);
        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel courseInfoPanel = new JPanel(new GridBagLayout());
        courseInfoPanel.setBackground(Color.WHITE);
        courseInfoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        BiFunction<String, String, JPanel> makeInfo = (label, value) -> {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));

            JLabel l1 = creatLabel(label, "SansSerif", 15);
            l1.setForeground(primary);

            JLabel l2 = new JLabel(value);
            l2.setFont(new Font("SansSerif", Font.PLAIN, 15));

            p.add(l1, BorderLayout.NORTH);
            p.add(l2, BorderLayout.CENTER);
            return p;
        };

        JPanel c1 = makeInfo.apply("Course Code", courseCode);
        JPanel c2 = makeInfo.apply("Course Title", courseTitle);
        JPanel c3 = makeInfo.apply("Credits", courseCredits);

        gbc.gridx = 0;
        gbc.gridy = 0;
        courseInfoPanel.add(c1, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        courseInfoPanel.add(c2, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        courseInfoPanel.add(c3, gbc);

        dialog.add(courseInfoPanel, BorderLayout.NORTH);

        loadsectiondataincourse(courseCode);

        JTable sectionTable = new JTable(sectionincoursemodel);
        sectionTable.setRowHeight(28);
        sectionTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(sectionTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(Color.WHITE);

        JButton addSectionBtn = new JButton("+ Add New Section");
        addSectionBtn.setPreferredSize(new Dimension(200, 40));
        addSectionBtn.setBackground(primary);
        addSectionBtn.setForeground(Color.WHITE);

        JButton closeBtn = new JButton("Close");
        closeBtn.setPreferredSize(new Dimension(150, 40));
        closeBtn.setBackground(Color.GRAY);
        closeBtn.setForeground(Color.WHITE);

        bottomPanel.add(addSectionBtn);
        bottomPanel.add(closeBtn);

        dialog.add(bottomPanel, BorderLayout.SOUTH);
        addSectionBtn.addActionListener(e -> {
            JDialog dialog1 = new JDialog((Frame) null, "Add Section", true);
            dialog1.getContentPane().add(getAddSectionForm(courseCode, sectionTable));
            dialog1.pack();
            dialog1.setLocationRelativeTo(null);
            dialog1.setVisible(true);
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    public DefaultTableModel CourseModel;

    public void loadAllCoursesIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Course> all = CourseDao.getAllCourses();

        for (Course c : all) {
            model.addRow(new Object[] {
                    c.getCode(),
                    c.getTitle(),
                    c.getCredits(),
            });
        }
    }

    public JPanel createManageCourses() {
        JPanel manageCoursesPanel = new JPanel(new BorderLayout());
        manageCoursesPanel.setBackground(Color.WHITE);

        JPanel filterDataPanel = new JPanel(new GridBagLayout());
        filterDataPanel.setBackground(Color.WHITE);
        filterDataPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        filterDataPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel rolePanel = new JPanel();
        rolePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        rolePanel.setBackground(cardBg);
        rolePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel roleLabel = creatLabel("All Courses", "SansSerif", 16);
        roleLabel.setForeground(new Color(0x005A2D));
        rolePanel.add(roleLabel);

        JPanel idPanel = new JPanel();
        idPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        idPanel.setBackground(cardBg);
        idPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel idLabel = creatLabel("Course Code:", "SansSerif", 16);
        idLabel.setForeground(new Color(0x005A2D));

        JTextField idField = new JTextField(15);
        idField.setPreferredSize(new Dimension(150, 30));
        idField.setFont(new Font("SansSerif", Font.PLAIN, 15));

        idPanel.add(idLabel);
        idPanel.add(idField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(0x005A2D));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.setPreferredSize(new Dimension(120, 40));
        searchBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterDataPanel.add(rolePanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        filterDataPanel.add(idPanel, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        filterDataPanel.add(searchBtn, gbc);

        JScrollPane userDataPanel = new JScrollPane();
        String[] tableColumns = { "Course Code", "Course Title", "Credits", "Sections", "Edit Course" };
        Object[][] getData = {};
        CourseModel = new DefaultTableModel(getData, tableColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };
        loadAllCoursesIntoTable(CourseModel);

        JTable dataTable = new JTable(CourseModel);
        dataTable.setRowHeight(30);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        String sec = "Manage Sections";

        TableCellRenderer editRenderer = (tbl, val, sel, focus, row, col) -> {
            JButton btn = new JButton(sec);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(0x006B3C));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return btn;
        };

        DefaultCellEditor editEditor = new DefaultCellEditor(new JTextField()) {
            JButton btn = new JButton(sec);

            {
                btn.setForeground(Color.WHITE);
                btn.setBackground(new Color(0x006B3C));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                btn.addActionListener(e -> {
                    int row = dataTable.getSelectedRow();

                    String code = dataTable.getValueAt(row, 0).toString();
                    String title = dataTable.getValueAt(row, 1).toString();
                    String credits = dataTable.getValueAt(row, 2).toString();

                    ManageSections(code, title, credits);
                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable tbl, Object v, boolean s, int r, int c) {
                return btn;
            }
        };

        dataTable.getColumn("Sections").setCellRenderer(editRenderer);
        dataTable.getColumn("Sections").setCellEditor(editEditor);

        String edit = "Edit";

        TableCellRenderer editRenderer_2 = (tbl, val, sel, focus, row, col) -> {
            JButton edit_btn = new JButton(edit);
            edit_btn.setForeground(Color.WHITE);
            edit_btn.setBackground(new Color(0x006B3C));
            edit_btn.setFocusPainted(false);
            edit_btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return edit_btn;
        };

        DefaultCellEditor editEditor_2 = new DefaultCellEditor(new JTextField()) {
            JButton edit_btn = new JButton(edit);

            {
                edit_btn.setForeground(Color.WHITE);
                edit_btn.setBackground(new Color(0x006B3C));
                edit_btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                edit_btn.addActionListener(e -> {
                    int row = dataTable.getSelectedRow();
                    String code = dataTable.getValueAt(row, 0).toString();
                    String title = dataTable.getValueAt(row, 1).toString();
                    String credits = dataTable.getValueAt(row, 2).toString();

                    JDialog dialog = new JDialog((Frame) null, "Edit Course", true);
                    dialog.getContentPane().add(getEditCourseForm(code, title, credits));
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable tbl, Object v, boolean s, int r, int c) {
                return edit_btn;
            }
        };

        dataTable.getColumn("Edit Course").setCellRenderer(editRenderer_2);
        dataTable.getColumn("Edit Course").setCellEditor(editEditor_2);

        JPanel addNewUserPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addNewUserPanel.setBackground(Color.WHITE);
        addNewUserPanel.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, new Color(0xE0E0E0)));

        JButton addCourseBtn = new JButton("+ Add New Course");
        addCourseBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        addCourseBtn.setBackground(new Color(0x005A2D));
        addCourseBtn.setForeground(Color.WHITE);
        addCourseBtn.setFocusPainted(false);
        addCourseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addCourseBtn.setPreferredSize(new Dimension(200, 40));

        addNewUserPanel.add(addCourseBtn);

        addCourseBtn.addActionListener(e -> {
            JPanel form = getAddCourseForm();
            openPopup((JFrame) SwingUtilities.getWindowAncestor(manageCoursesPanel), form, "Add Course");
        });

        searchBtn.addActionListener(e -> {
            String fetch_userID = idField.getText();
            DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
            model.setRowCount(0);
            Course courses = null;
            if (fetch_userID.isEmpty()) {
                loadAllCoursesIntoTable(model);
            } else {
                courses = CourseDao.getCourseByCode(fetch_userID);
            }

            model.addRow(new Object[] {
                    courses.getCode(),
                    courses.getTitle(),
                    courses.getCredits(),
            });

        });

        userDataPanel.setViewportView(dataTable);

        manageCoursesPanel.add(filterDataPanel, BorderLayout.NORTH);
        manageCoursesPanel.add(userDataPanel, BorderLayout.CENTER);
        manageCoursesPanel.add(addNewUserPanel, BorderLayout.SOUTH);

        return manageCoursesPanel;
    }

    public String courseCodeInput;
    public DefaultTableModel searchsectionmodel;
    public String assignText = "Assign";
    public String editText = "Edit";

    public JPanel createSearchSections() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        Color primary = new Color(0x005A2D);
        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel codePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        codePanel.setBackground(cardBg);
        codePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel codeLabel = creatLabel("Course Code:", "SansSerif", 16);
        codeLabel.setForeground(primary);
        JTextField codeField = new JTextField(15);
        codeField.setPreferredSize(new Dimension(150, 30));

        codePanel.add(codeLabel);
        codePanel.add(codeField);

        JButton searchBtn = createButton("Search", "SansSerif", Color.WHITE, primary, 16);
        searchBtn.setPreferredSize(new Dimension(120, 40));

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(codePanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridx = 2;
        gbc.gridy = 0;
        filterPanel.add(searchBtn, gbc);

        String[] cols = {
                "Section ID", "Day/Time", "Room", "Semester",
                "Year", "Capacity", "Enrolled", "Instructor",
                "Assign", "Edit"
        };

        searchsectionmodel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 8 || col == 9;
            }
        };

        JTable table = new JTable(searchsectionmodel);
        table.setRowHeight(30);

        JScrollPane scroll = new JScrollPane(table);
        TableCellRenderer assignRenderer = (tbl, val, sel, focus, row, col) -> {
            JButton b = new JButton(assignText);
            b.setBackground(new Color(0x006B3C));
            b.setForeground(Color.WHITE);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setFocusPainted(false);
            return b;
        };

        DefaultCellEditor assignEditor = new DefaultCellEditor(new JTextField()) {
            JButton btn = new JButton(assignText);

            {
                btn.setBackground(new Color(0x006B3C));
                btn.setForeground(Color.WHITE);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                btn.addActionListener(e -> {
                    int row = table.getSelectedRow();
                    String sectionId = table.getValueAt(row, 0).toString();

                    JDialog dialog = new JDialog((Frame) null, "Assign Instructor", true);
                    dialog.getContentPane().add(getAssignInstructorForm(sectionId));
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);

                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable tbl, Object v, boolean s, int r, int c) {
                return btn;
            }
        };

        table.getColumn("Assign").setCellRenderer(assignRenderer);
        table.getColumn("Assign").setCellEditor(assignEditor);

        String editText = "Edit";

        TableCellRenderer editRenderer = (tbl, val, sel, focus, row, col) -> {
            JButton b = new JButton(editText);
            b.setBackground(new Color(0x006B3C));
            b.setForeground(Color.WHITE);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        };

        DefaultCellEditor editEditor = new DefaultCellEditor(new JTextField()) {
            JButton btn = new JButton(editText);

            {
                btn.setBackground(new Color(0x006B3C));
                btn.setForeground(Color.WHITE);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                btn.addActionListener(e -> {
                    int row = table.getSelectedRow();

                    String secId = table.getValueAt(row, 0).toString();
                    String day = table.getValueAt(row, 1).toString();
                    String room = table.getValueAt(row, 2).toString();
                    String sem = table.getValueAt(row, 3).toString();
                    String year = table.getValueAt(row, 4).toString();
                    String cap = table.getValueAt(row, 5).toString();
                    String enr = table.getValueAt(row, 6).toString();

                    JDialog dialog = new JDialog((Frame) null, "Edit Section", true);
                    dialog.getContentPane().add(getEditSectionForm(secId, day, room, sem, year, cap, enr));
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);

                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable tbl, Object v, boolean s, int r, int c) {
                return btn;
            }
        };

        table.getColumn("Edit").setCellRenderer(editRenderer);
        table.getColumn("Edit").setCellEditor(editEditor);

        searchBtn.addActionListener(e -> {
            courseCodeInput = codeField.getText().trim();

            searchsectionmodel.setRowCount(0);

            int courseId = CourseDao.getCourseIdByCode(courseCodeInput);
            List<Section> sections = sectionDoa.getSectionBycouseid(courseId);
            Object[][] sectionsData = new Object[sections.size()][8];
            for (int i = 0; i < sections.size(); i++) {
                Section s = sections.get(i);
                sectionsData[i][0] = s.getSectionID();
                sectionsData[i][1] = s.getTimeTable();
                sectionsData[i][2] = s.getRoom();
                sectionsData[i][3] = s.getSemester();
                sectionsData[i][4] = s.getYear();
                sectionsData[i][5] = s.getSectionCapacity();
                sectionsData[i][6] = s.getCurrentEnrollment();
                int idInstructor = Integer.parseInt(s.getInstructorID());
                String instructorName = instructordao.getInstructorNameById(idInstructor);
                sectionsData[i][7] = instructorName;
            }

            for (Object[] r : sectionsData) {
                searchsectionmodel.addRow(new Object[] {
                        r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7],
                        assignText, editText
                });
            }
        });

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    public JPanel getAssignInstructorForm(String sectionId) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        Color primary = new Color(0x005A2D);
        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0xE0E0E0)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));

            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
            lbl.setForeground(primary);

            p.add(lbl, BorderLayout.NORTH);
            p.add(input, BorderLayout.CENTER);
            return p;
        };

        JTextField nameField = new JTextField(15);
        JPanel namePanel = makeField.apply("Instructor Name:", nameField);

        JButton searchBtn = createButton("Search", "SansSerif", Color.WHITE, primary, 16);
        searchBtn.setPreferredSize(new Dimension(120, 40));

        gbc.gridx = 0;
        gbc.gridy = 0;
        searchPanel.add(namePanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        searchPanel.add(searchBtn, gbc);

        String[] columns = { "Instructor ID", "Name", "Department" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, new Color(0xE0E0E0)));

        JButton assignBtn = createButton("Assign Instructor", "SansSerif", Color.WHITE, primary, 16);
        assignBtn.setPreferredSize(new Dimension(200, 40));

        bottomPanel.add(assignBtn);

        searchBtn.addActionListener(e -> {
            String inputName = nameField.getText().trim();

            model.setRowCount(0);

            List<Instructor> list = adminDao.getInstructorsByName(inputName);
            int instructorId;
            for (Instructor i : list) {
                instructorId = instructordao.getInstructorIdByUserId(i.getUserID());
                model.addRow(new Object[] {
                        instructorId,
                        i.getName(),
                        i.getDepartment()
                });
            }
        });

        assignBtn.addActionListener(e -> {

            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel,
                        "Please select an instructor!",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int secId = Integer.parseInt(sectionId);
            int instructorId = Integer.parseInt(table.getValueAt(row, 0).toString());
            String instructorName = table.getValueAt(row, 1).toString();

            boolean ok = adminDao.assignInstructorToSection(secId, instructorId);

            if (!ok) {
                JOptionPane.showMessageDialog(panel,
                        "Failed to assign instructor!",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(panel,
                    "Instructor " + instructorName + " successfully assigned to section " + sectionId + "!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            SwingUtilities.getWindowAncestor(panel).dispose();
        });

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    public JPanel getEditSectionForm(
            String sectionId,
            String dayTime,
            String room,
            String semester,
            String year,
            String capacity,
            String enrolled) {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Color primary = new Color(0x005A2D);
        Color cardBg = new Color(0xF4F4F4);
        Color borderColor = new Color(0xC8C8C8);

        BiFunction<String, JComponent, JPanel> makeField = (label, input) -> {
            JPanel p = new JPanel(new BorderLayout(10, 5));
            p.setBackground(cardBg);
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));

            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
            lbl.setForeground(primary);
            p.add(lbl, BorderLayout.NORTH);
            p.add(input, BorderLayout.CENTER);

            return p;
        };

        JTextField secIdField = new JTextField(15);
        secIdField.setText(sectionId);
        secIdField.setEditable(false);
        secIdField.setBackground(new Color(230, 230, 230));

        JTextField dayField = new JTextField(dayTime, 15);
        JTextField roomField = new JTextField(room, 15);
        JTextField semField = new JTextField(semester, 15);
        JTextField yearField = new JTextField(year, 15);
        JTextField capField = new JTextField(capacity, 15);
        JTextField currField = new JTextField(enrolled, 15);

        JPanel p1 = makeField.apply("Section ID", secIdField);
        JPanel p2 = makeField.apply("Day & Time *", dayField);
        JPanel p3 = makeField.apply("Room *", roomField);
        JPanel p4 = makeField.apply("Semester *", semField);
        JPanel p5 = makeField.apply("Year *", yearField);
        JPanel p6 = makeField.apply("Capacity *", capField);
        JPanel p7 = makeField.apply("Current Enrollment", currField);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(p1, gbc);

        row++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(p2, gbc);
        gbc.gridx = 1;
        panel.add(p3, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(p4, gbc);
        gbc.gridx = 1;
        panel.add(p5, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(p6, gbc);
        gbc.gridx = 1;
        panel.add(p7, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = createButton("Save", "SansSerif", Color.WHITE, primary, 16);
        saveBtn.setPreferredSize(new Dimension(130, 40));

        JButton cancelBtn = createButton("Cancel", "SansSerif", Color.WHITE, Color.GRAY, 16);
        cancelBtn.setPreferredSize(new Dimension(130, 40));

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        cancelBtn.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(panel).dispose();
        });
        saveBtn.addActionListener(e -> {

            String d = dayField.getText().trim();
            String r = roomField.getText().trim();
            String s = semField.getText().trim();
            String y = yearField.getText().trim();
            String c = capField.getText().trim();
            String ce = currField.getText().trim();

            if (d.isEmpty() || r.isEmpty() || s.isEmpty() || y.isEmpty() || c.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Fields marked * are mandatory!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Integer.parseInt(y);
                Integer.parseInt(c);
                if (!ce.isEmpty())
                    Integer.parseInt(ce);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                        "Year, Capacity and Enrollment must be valid numbers!",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean ok = sectionDoa.updateSection(
                    Integer.parseInt(sectionId),
                    d, r, s,
                    Integer.parseInt(y),
                    Integer.parseInt(c),
                    ce.isEmpty() ? 0 : Integer.parseInt(ce));
            if (!ok) {
                JOptionPane.showMessageDialog(panel,
                        "Failed to update section!",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(panel,
                    "Section updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadsectiondataincourse(c);
            SwingUtilities.getWindowAncestor(panel).dispose();
        });

        return panel;
    }

}