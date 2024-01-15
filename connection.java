import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class connection extends JFrame {

    private JTextField patientNameField, ageField, dateField, timeField;
    private JTextArea appointmentTextArea;
    private JRadioButton maleRadioButton, femaleRadioButton;
    private ButtonGroup genderGroup;
    private List<String> appointments;

    // JDBC connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/patient";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "64152737";

    public connection() {
        appointments = new ArrayList<>();

        setTitle("Appointment Scheduler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        createUI();

        pack();
    }

    private void createUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));

        patientNameField = new JTextField();
        ageField = new JTextField();
        dateField = new JTextField();
        timeField = new JTextField();

        maleRadioButton = new JRadioButton("Male");
        femaleRadioButton = new JRadioButton("Female");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadioButton);
        genderGroup.add(femaleRadioButton);

        inputPanel.add(new JLabel("Patient Name:"));
        inputPanel.add(patientNameField);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Time:"));
        inputPanel.add(timeField);
        inputPanel.add(new JLabel("Gender:"));
        inputPanel.add(createGenderPanel());

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        appointmentTextArea = new JTextArea();
        appointmentTextArea.setEditable(false);

        mainPanel.add(new JScrollPane(appointmentTextArea), BorderLayout.CENTER);

        JButton scheduleButton = new JButton("Schedule Appointment");
        scheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
					scheduleAppointment();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });

        mainPanel.add(scheduleButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createGenderPanel() {
        JPanel genderPanel = new JPanel();
        genderPanel.setLayout(new FlowLayout());

        genderPanel.add(maleRadioButton);
        genderPanel.add(femaleRadioButton);

        return genderPanel;
    }

    private void scheduleAppointment() throws ClassNotFoundException {
        String patientName = patientNameField.getText();
        String age = ageField.getText();
        String date = dateField.getText();
        String time = timeField.getText();
        String gender = maleRadioButton.isSelected() ? "Male" : "Female";

        if (!patientName.isEmpty() && !age.isEmpty() && !date.isEmpty() && !time.isEmpty()) {
            String appointmentDetails = "Patient Name: " + patientName + " | Age: " + age + " | Date: " + date + " | Time: " + time + " | Gender: " + gender;
            appointments.add(appointmentDetails);

            // Insert into the database
            saveAppointmentToDatabase(patientName, age, date, time, gender);

            updateAppointmentTextArea();

            // Clear input fields
            patientNameField.setText("");
            ageField.setText("");
            dateField.setText("");
            timeField.setText("");
            genderGroup.clearSelection();
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateAppointmentTextArea() {
        appointmentTextArea.setText("");
        for (String appointment : appointments) {
            appointmentTextArea.append(appointment + "\n");
        }
    }

    private void saveAppointmentToDatabase(String patientName, String age, String date, String time, String gender) throws ClassNotFoundException {
    	try {
    	    Class.forName("com.mysql.cj.jdbc.Driver");
    	    try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
    	        String insertSQL = "INSERT INTO details (patientName, age, date, time, gender) VALUES (?, ?, ?, ?, ?)";
    	        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
    	            preparedStatement.setString(1, patientName);
    	            preparedStatement.setString(2, age);
    	            preparedStatement.setString(3, date);
    	            preparedStatement.setString(4, time);
    	            preparedStatement.setString(5, gender);

    	            preparedStatement.executeUpdate();
    	        }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while saving appointment to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new connection().setVisible(true);
            }
        });
    }
}
