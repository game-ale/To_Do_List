import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

/**
 * A simple To-Do List application using Java Swing.
 * This application allows users to add, check off, delete, and clear tasks,
 * with task data saved to a CSV file for persistence across sessions.
 * The app provides a simple and user-friendly graphical interface.
 */
public class ToDoListApp {

    private JFrame frame; // Main frame of the application
    private JPanel tasksPanel; // Panel that holds task checkboxes
    private JTextField taskField; // Text field for entering new tasks
    private ArrayList<JCheckBox> taskCheckBoxes = new ArrayList<>(); // List of task checkboxes
    private final String CSV_FILE = "tasks.csv"; // Path to the CSV file for persistent storage

    /**
     * Constructs the ToDoListApp and initializes the user interface.
     * It also loads any previously saved tasks from the CSV file.
     */
    public ToDoListApp() {
        initializeUI();
        loadTasks();
    }

    /**
     * Initializes the graphical user interface (GUI) components.
     * Sets up the frame, task input panel, task list panel, and action buttons.
     */
    private void initializeUI() {
        // Frame setup
        frame = new JFrame("To-Do List with Checkboxes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        // Task input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(new Color(240, 240, 240));

        JLabel taskLabel = new JLabel("Task:");
        taskLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(taskLabel);

        taskField = new JTextField(20);
        taskField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(taskField);

        JButton addButton = new JButton("Add Task");
        styleButton(addButton, new Color(76, 175, 80)); // Green button
        addButton.addActionListener(e -> addTask());
        inputPanel.add(addButton);

        // Add Enter key listener for the text field
        taskField.addActionListener(e -> addTask());

        frame.add(inputPanel, BorderLayout.NORTH);

        // Tasks panel
        tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(240, 240, 240));
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton deleteButton = new JButton("Delete Selected");
        styleButton(deleteButton, new Color(255, 87, 51)); // Red button
        deleteButton.addActionListener(e -> deleteSelectedTasks());
        buttonsPanel.add(deleteButton);

        JButton clearAllButton = new JButton("Clear All");
        styleButton(clearAllButton, new Color(244, 67, 54)); // Dark red button
        clearAllButton.addActionListener(e -> clearAllTasks());
        buttonsPanel.add(clearAllButton);

        frame.add(buttonsPanel, BorderLayout.SOUTH);

        // Make frame visible
        frame.setVisible(true);
    }

    /**
     * Styles a given button with the specified background color and adds a hover effect.
     *
     * @param button the button to be styled
     * @param backgroundColor the background color to apply
     */
    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Hover effect for button
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
    }

    /**
     * Adds a new task to the task list if the input field is not empty.
     * The task is also saved to the CSV file for persistence.
     */
    private void addTask() {
        String taskText = taskField.getText().trim();
        if (!taskText.isEmpty()) {
            JCheckBox taskCheckBox = new JCheckBox(taskText);
            taskCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
            taskCheckBox.setBackground(Color.WHITE);

            taskCheckBoxes.add(taskCheckBox);
            tasksPanel.add(taskCheckBox);
            tasksPanel.revalidate();
            tasksPanel.repaint();

            taskField.setText(""); // Clear the input field after adding the task
            saveTasks(); // Save tasks to CSV file
        } else {
            // Show a warning if the task is empty
            JOptionPane.showMessageDialog(frame, "Task cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Deletes the selected tasks from the task list.
     * Also updates the task list and saves the updated tasks to the CSV file.
     */
    private void deleteSelectedTasks() {
        taskCheckBoxes.removeIf(taskCheckBox -> {
            if (taskCheckBox.isSelected()) {
                tasksPanel.remove(taskCheckBox);
                return true;
            }
            return false;
        });

        tasksPanel.revalidate();
        tasksPanel.repaint();
        saveTasks(); // Save the updated task list to the CSV file
    }

    /**
     * Clears all tasks from the task list and resets the task data.
     * The task list is saved to the CSV file after clearing.
     */
    private void clearAllTasks() {
        tasksPanel.removeAll();
        taskCheckBoxes.clear();

        tasksPanel.revalidate();
        tasksPanel.repaint();
        saveTasks(); // Save the empty task list to the CSV file
    }

    /**
     * Saves the current tasks and their completion status to a CSV file.
     * Each task is written as a line with task text and completion status.
     */
    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (JCheckBox taskCheckBox : taskCheckBoxes) {
                writer.write(taskCheckBox.getText() + "," + (taskCheckBox.isSelected() ? "1" : "0"));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the tasks from the CSV file and adds them to the task list.
     * Each task is restored with its completion status from the CSV file.
     */
    private void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    JCheckBox taskCheckBox = new JCheckBox(parts[0]);
                    taskCheckBox.setSelected(parts[1].equals("1"));
                    taskCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
                    taskCheckBox.setBackground(Color.WHITE);

                    taskCheckBoxes.add(taskCheckBox);
                    tasksPanel.add(taskCheckBox);
                }
            }
        } catch (IOException e) {
            // File may not exist initially, no action needed
        }

        tasksPanel.revalidate();
        tasksPanel.repaint();
    }

    /**
     * The main method that runs the application.
     * It initializes the ToDoListApp and starts the user interface.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListApp::new);
    }
}
