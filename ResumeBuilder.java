import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.Vector;
import java.util.logging.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ResumeBuilder {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            SwingUtilities.invokeLater(ResumeBuilder::showStartPage);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!");
        }
    }

    private static void showStartPage() {
        JFrame frame = new JFrame("Welcome to Resume Builder");
        frame.setSize(350, 200);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel label = new JLabel("Choose Mode:", SwingConstants.CENTER);
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));

        JButton userBtn = new JButton("User");
        JButton adminBtn = new JButton("Admin");

        userBtn.addActionListener(_ -> {
            frame.dispose();
            new ResumeForm();
        });

        adminBtn.addActionListener(_ -> {
            String pass = JOptionPane.showInputDialog(frame, "Enter Admin Password:");
            if ("admin123".equals(pass)) {
                frame.dispose();
                new AdminViewer();
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect password!");
            }
        });

        panel.add(label);
        panel.add(userBtn);
        panel.add(adminBtn);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class ResumeForm extends JFrame {
    JTextField nameField, emailField, phoneField;
    JTextArea addressArea, skillArea, eduArea, expArea;
    Logger logger = Logger.getLogger(ResumeForm.class.getName());
    String generatedPdfPath;  // To store the generated PDF path

    public ResumeForm() {
        setTitle("Resume Builder - User");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        nameField = new JTextField(30);
        emailField = new JTextField(30);
        phoneField = new JTextField(30);
        addressArea = new JTextArea(3, 30);
        skillArea = new JTextArea(3, 30);
        eduArea = new JTextArea(4, 30);
        expArea = new JTextArea(4, 30);

        panel.add(createFieldPanel("Name:", nameField));
        panel.add(createFieldPanel("Email:", emailField));
        panel.add(createFieldPanel("Phone:", phoneField));
        panel.add(createFieldPanel("Address:", new JScrollPane(addressArea)));
        panel.add(createFieldPanel("Skills (comma-separated):", new JScrollPane(skillArea)));
        panel.add(createFieldPanel("Education (Degree, Institution, Year):", new JScrollPane(eduArea)));
        panel.add(createFieldPanel("Experience (Company, Role, Duration):", new JScrollPane(expArea)));

        JButton saveBtn = new JButton("Save Resume");
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.addActionListener(_ -> saveToDatabase());
        panel.add(Box.createVerticalStrut(10));
        panel.add(saveBtn);

        JButton downloadBtn = new JButton("Download PDF");
        downloadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        downloadBtn.addActionListener(_ -> downloadPDF());
        panel.add(Box.createVerticalStrut(10));
        panel.add(downloadBtn);

        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generatePDF(String name, String email, String phone, String address, String[] skills, String[] educations, String[] experiences) {
        try {
            Document document = new Document();
            generatedPdfPath = "Resume_" + name.replaceAll("\\s+", "_") + ".pdf";  // Store PDF path
            PdfWriter.getInstance(document, new FileOutputStream(generatedPdfPath));
            document.open();

            com.itextpdf.text.Font titleFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18);
            com.itextpdf.text.Font sectionFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 14);
            com.itextpdf.text.Font contentFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Resume", titleFont));
            document.add(new Paragraph("Name: " + name, contentFont));
            document.add(new Paragraph("Email: " + email, contentFont));
            document.add(new Paragraph("Phone: " + phone, contentFont));
            document.add(new Paragraph("Address: " + address, contentFont));
            document.add(new Paragraph("\nSkills:", sectionFont));
            for (String skill : skills) {
                if (!skill.trim().isEmpty()) {
                    document.add(new Paragraph("- " + skill.trim(), contentFont));
                }
            }

            document.add(new Paragraph("\nEducation:", sectionFont));
            for (String edu : educations) {
                if (!edu.trim().isEmpty()) {
                    document.add(new Paragraph("- " + edu.trim(), contentFont));
                }
            }

            document.add(new Paragraph("\nExperience:", sectionFont));
            for (String exp : experiences) {
                if (!exp.trim().isEmpty()) {
                    document.add(new Paragraph("- " + exp.trim(), contentFont));
                }
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Failed to generate PDF: " + e.getMessage());
        }
    }

    private void downloadPDF() {
        if (generatedPdfPath == null || generatedPdfPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❗ No resume generated yet. Please save your resume first.");
            return;
        }

        try {
            // Open the generated PDF file using the default system PDF viewer
            Desktop.getDesktop().open(new File(generatedPdfPath));
        } catch (IOException e) {
            e.printStackTrace();  // This will print the stack trace
            JOptionPane.showMessageDialog(this, "❌ Failed to open PDF: " + e.getMessage());  // This will show the error message
        }
    }


    private JPanel createFieldPanel(String label, Component comp) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        JLabel l = new JLabel(label);
        l.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        p.setBorder(new EmptyBorder(5, 0, 5, 0));
        return p;
    }

    private boolean validateFields() {
        boolean valid = true;
        resetFieldColors();

        if (nameField.getText().trim().isEmpty()) {
            nameField.setBackground(Color.PINK);
            nameField.requestFocus();
            valid = false;
        } else if (!emailField.getText().trim().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            emailField.setBackground(Color.PINK);
            emailField.requestFocus();
            valid = false;
        } else if (!phoneField.getText().trim().matches("^\\d{10}$")) {
            phoneField.setBackground(Color.PINK);
            phoneField.requestFocus();
            valid = false;
        } else if (addressArea.getText().trim().isEmpty()) {
            addressArea.setBackground(Color.PINK);
            addressArea.requestFocus();
            valid = false;
        } else if (skillArea.getText().trim().isEmpty()) {
            skillArea.setBackground(Color.PINK);
            skillArea.requestFocus();
            valid = false;
        } else if (eduArea.getText().trim().isEmpty()) {
            eduArea.setBackground(Color.PINK);
            eduArea.requestFocus();
            valid = false;
        } else {
            String[] educations = eduArea.getText().split("\n");
            for (String edu : educations) {
                String[] parts = edu.split(",", 3);
                if (parts.length != 3 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty() || parts[2].trim().isEmpty()) {
                    eduArea.setBackground(Color.PINK);
                    eduArea.requestFocus();
                    JOptionPane.showMessageDialog(this, "❗ Each education entry must be in the format: Degree, Institution, Year");
                    valid = false;
                    break;
                }
            }
        }

        return valid;
    }

    private void resetFieldColors() {
        nameField.setBackground(Color.WHITE);
        emailField.setBackground(Color.WHITE);
        phoneField.setBackground(Color.WHITE);
        addressArea.setBackground(Color.WHITE);
        skillArea.setBackground(Color.WHITE);
        eduArea.setBackground(Color.WHITE);
        expArea.setBackground(Color.WHITE);
    }

    private void saveToDatabase() {
        if (!validateFields()) {
            JOptionPane.showMessageDialog(this, "❗ Please correct the highlighted fields.");
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        String[] skills = skillArea.getText().split(",");
        String[] educations = eduArea.getText().split("\n");
        String[] experiences = expArea.getText().split("\n");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/resume_db", "root", "Divya@12")) {
            String userQuery = "INSERT INTO users (name, email, phone, address) VALUES (?, ?, ?, ?)";
            try (PreparedStatement userPs = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS)) {
                userPs.setString(1, name);
                userPs.setString(2, email);
                userPs.setString(3, phone);
                userPs.setString(4, address);
                int userRowsInserted = userPs.executeUpdate();

                if (userRowsInserted > 0) {
                    ResultSet generatedKeys = userPs.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        // Insert skills
                        String skillQuery = "INSERT INTO skills (user_id, skill_name) VALUES (?, ?)";
                        try (PreparedStatement skillPs = conn.prepareStatement(skillQuery)) {
                            for (String skill : skills) {
                                if (!skill.trim().isEmpty()) {
                                    skillPs.setInt(1, userId);
                                    skillPs.setString(2, skill.trim());
                                    skillPs.addBatch();
                                }
                            }
                            skillPs.executeBatch();
                        }

                        // Insert education
                        String eduQuery = "INSERT INTO education (user_id, degree, institution, year) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement eduPs = conn.prepareStatement(eduQuery)) {
                            for (String edu : educations) {
                                String[] parts = edu.split(",", 3);
                                if (parts.length == 3) {
                                    eduPs.setInt(1, userId);
                                    eduPs.setString(2, parts[0].trim());
                                    eduPs.setString(3, parts[1].trim());
                                    eduPs.setString(4, parts[2].trim());
                                    eduPs.addBatch();
                                }
                            }
                            eduPs.executeBatch();
                        }

                        // Insert experience
                        String expQuery = "INSERT INTO experience (user_id, company, role, duration) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement expPs = conn.prepareStatement(expQuery)) {
                            for (String exp : experiences) {
                                String[] parts = exp.split(",", 3);
                                if (parts.length == 3) {
                                    expPs.setInt(1, userId);
                                    expPs.setString(2, parts[0].trim());
                                    expPs.setString(3, parts[1].trim());
                                    expPs.setString(4, parts[2].trim());
                                    expPs.addBatch();
                                }
                            }
                            expPs.executeBatch();
                        }

                        // Generate PDF
                        generatePDF(name, email, phone, address, skills, educations, experiences);

                        JOptionPane.showMessageDialog(this, "✅ Resume saved successfully!");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Database error: " + e.getMessage());
        }
    }
}
class AdminViewer extends JFrame {
    JTable resumeTable;
    DefaultTableModel model;

    public AdminViewer() {
        setTitle("Admin - Resume Viewer");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Address", "Skills", "Education", "Experience"}, 0);
        resumeTable = new JTable(model);
        loadResumes();

        JScrollPane scrollPane = new JScrollPane(resumeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Update button
        JButton updateBtn = new JButton("Update Resume");
        updateBtn.addActionListener(e -> updateResume());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateBtn);

        // Delete button
        JButton deleteBtn = new JButton("Delete Resume");
        deleteBtn.addActionListener(e -> deleteResume());
        buttonPanel.add(deleteBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadResumes() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/resume_db", "root", "Divya@12")) {
            String query = "SELECT u.id, u.name, u.email, u.phone, u.address, " +
                    "GROUP_CONCAT(DISTINCT s.skill_name) AS skills, " +
                    "GROUP_CONCAT(DISTINCT CONCAT(e.degree, ' at ', e.institution, ' (', e.year, ')')) AS education, " +
                    "GROUP_CONCAT(DISTINCT CONCAT(ex.company, ' - ', ex.role, ' (', ex.duration, ')')) AS experience " +
                    "FROM users u " +
                    "LEFT JOIN skills s ON u.id = s.user_id " +
                    "LEFT JOIN education e ON u.id = e.user_id " +
                    "LEFT JOIN experience ex ON u.id = ex.user_id " +
                    "GROUP BY u.id";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("email"));
                    row.add(rs.getString("phone"));
                    row.add(rs.getString("address"));
                    row.add(rs.getString("skills"));
                    row.add(rs.getString("education"));
                    row.add(rs.getString("experience"));
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateResume() {
        int selectedRow = resumeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❗ Please select a resume to update.");
            return;
        }

        // Fetch the selected resume's data
        int userId = (int) resumeTable.getValueAt(selectedRow, 0);
        String name = (String) resumeTable.getValueAt(selectedRow, 1);
        String email = (String) resumeTable.getValueAt(selectedRow, 2);
        String phone = (String) resumeTable.getValueAt(selectedRow, 3);
        String address = (String) resumeTable.getValueAt(selectedRow, 4);
        String skills = (String) resumeTable.getValueAt(selectedRow, 5);
        String education = (String) resumeTable.getValueAt(selectedRow, 6);
        String experience = (String) resumeTable.getValueAt(selectedRow, 7);

        // Open a dialog with the fetched data
        ResumeEditDialog dialog = new ResumeEditDialog(this, userId, name, email, phone, address, skills, education, experience, this::reloadTable);
        dialog.setVisible(true);
    }

    private void reloadTable() {
        model.setRowCount(0);
        loadResumes();
        model.fireTableDataChanged(); // 👈 Add this line

    }

    private void deleteResume() {
        int selectedRow = resumeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❗ Please select a resume to delete.");
            return;
        }

        int userId = (int) resumeTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this resume?", "Delete Resume", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/resume_db", "root", "Divya@12")) {
                // Deleting related records in the experience, education, and skills tables first
                String deleteSkillsQuery = "DELETE FROM skills WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteSkillsQuery)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                String deleteEducationQuery = "DELETE FROM education WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteEducationQuery)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                String deleteExperienceQuery = "DELETE FROM experience WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteExperienceQuery)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                // Now delete the user
                String deleteUserQuery = "DELETE FROM users WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteUserQuery)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                // Reload the table data after deletion
                model.setRowCount(0);
                loadResumes();

                JOptionPane.showMessageDialog(this, "✅ Resume deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Failed to delete resume: " + e.getMessage());
            }
        }
    }
}
class ResumeEditDialog extends JDialog {
    private JTextField nameField, emailField, phoneField;
    private JTextArea addressArea, skillArea, eduArea, expArea;
    private int userId;
    private Runnable onUpdateCallback; // Callback to refresh admin panel

    public ResumeEditDialog(JFrame parent, int userId, String name, String email, String phone, String address,
                            String skills, String education, String experience, Runnable onUpdateCallback) {
        super(parent, "Edit Resume", true);
        this.userId = userId;
        this.onUpdateCallback = onUpdateCallback;

        setSize(600, 500); // More space
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        nameField = new JTextField(name);
        emailField = new JTextField(email);
        phoneField = new JTextField(phone);
        addressArea = new JTextArea(address, 2, 30);
        skillArea = new JTextArea(skills, 2, 30);
        eduArea = new JTextArea(education, 3, 30);
        expArea = new JTextArea(experience, 3, 30);

        panel.add(createFieldPanel("Name:", nameField));
        panel.add(createFieldPanel("Email:", emailField));
        panel.add(createFieldPanel("Phone:", phoneField));
        panel.add(createFieldPanel("Address:", new JScrollPane(addressArea)));
        panel.add(createFieldPanel("Skills (comma-separated):", new JScrollPane(skillArea)));
        panel.add(createFieldPanel("Education (Degree at Institution):", new JScrollPane(eduArea)));
        panel.add(createFieldPanel("Experience (Company - Role):", new JScrollPane(expArea)));

        JButton saveBtn = new JButton("💾 Save Changes");
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.addActionListener(e -> saveChanges());

        panel.add(Box.createVerticalStrut(15));
        panel.add(saveBtn);

        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane);
        setLocationRelativeTo(parent);
    }

    private void saveChanges() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        String[] skills = skillArea.getText().split(",");
        String[] educations = eduArea.getText().split("\n");
        String[] experiences = expArea.getText().split("\n");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/resume_db", "root", "Divya@12")) {
            // Always update core user info
            try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?")) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setString(4, address);
                ps.setInt(5, userId);
                ps.executeUpdate();
            }

            // Only update other fields if there's data
            if (!skillArea.getText().trim().isEmpty()) {
                updateSkills(conn, skills);
            }

            if (!eduArea.getText().trim().isEmpty()) {
                updateEducation(conn, educations);
            }

            if (!expArea.getText().trim().isEmpty()) {
                updateExperience(conn, experiences);
            }

            JOptionPane.showMessageDialog(this, "✅ Resume updated successfully!");
            if (onUpdateCallback != null) onUpdateCallback.run();
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Update failed: " + e.getMessage());
        }
    }

    private void updateSkills(Connection conn, String[] skills) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM skills WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO skills (user_id, skill_name) VALUES (?, ?)")) {
            for (String skill : skills) {
                if (!skill.trim().isEmpty()) {
                    ps.setInt(1, userId);
                    ps.setString(2, skill.trim());
                    ps.executeUpdate();
                }
            }
        }
    }
    private void updateExperience(Connection conn, String[] experience) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM experience WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO experience (user_id, company, role, duration) VALUES (?, ?, ?, ?)")) {
            for (String exp : experience) {
                if (!exp.trim().isEmpty() && exp.contains(" - ") && exp.contains("(") && exp.contains(")")) {
                    String[] mainParts = exp.split(" - ");
                    String company = mainParts[0].trim();
                    String[] roleAndDuration = mainParts[1].split(" \\(");
                    String role = roleAndDuration[0].trim();
                    String duration = roleAndDuration[1].replace(")", "").trim();

                    ps.setInt(1, userId);
                    ps.setString(2, company);
                    ps.setString(3, role);
                    ps.setString(4, duration);
                    ps.executeUpdate();
                }
            }
        }
    }
    private void updateEducation(Connection conn, String[] education) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM education WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }

        String insertQuery = "INSERT INTO education (user_id, degree, institution, year) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            for (String edu : education) {
                if (!edu.trim().isEmpty() && edu.contains(" at ")) {
                    String[] degreeInstitution = edu.split(" at ");
                    String degree = degreeInstitution[0].trim();
                    String institutionAndYear = degreeInstitution[1].trim();

                    String institution = institutionAndYear;
                    String year = "";

                    // Extract year if present in parentheses
                    if (institutionAndYear.matches(".*\\(\\d{4}\\).*")) {
                        int start = institutionAndYear.lastIndexOf('(');
                        int end = institutionAndYear.lastIndexOf(')');
                        if (start != -1 && end != -1 && end > start) {
                            year = institutionAndYear.substring(start + 1, end);
                            institution = institutionAndYear.substring(0, start).trim();
                        }
                    }

                    ps.setInt(1, userId);
                    ps.setString(2, degree);
                    ps.setString(3, institution);
                    ps.setString(4, year);
                    ps.executeUpdate();
                }
            }
        }
    }

    private JPanel createFieldPanel(String labelText, Component component) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(200, 30));
        panel.add(label, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        return panel;
    }
}
