package hotschool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author bilaa
 */
public class HotSchool {

    // Outside of main so that all methods can use it
    public static Scanner scanner = new Scanner(System.in);
    public static Connection connection = null;
    public static PreparedStatement statement = null;
    public static ResultSet resultSet = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String dbURL = "jdbc:mysql://localhost:3306/hotsummer";
        String user = "root";
        String password = "Muhaarib21";

        try {
            connection = DriverManager.getConnection(dbURL, user, password);
            menu();
        } catch (SQLException e) {
            System.out.println("Error getting connection!");
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(e.getSQLState());
            System.out.println(e.getErrorCode());
        }
    }

    /**
     * This method displays all the available students
     *
     * @throws SQLException
     */
    public static void displayAllStudents() throws SQLException {
        // SQL query that selects the id, firstname, lastname from the student table 
        String allStudentsSQL = "SELECT id, firstname, lastname "
                + "FROM student";

        // Prepares the statment
        statement = connection.prepareStatement(allStudentsSQL);

        // Executing the statement that just got prepared
        resultSet = statement.executeQuery();

        // Printing the id, first name, last name
        System.out.println("\nList Of All Students");
        System.out.println("------------------------------------");
        System.out.printf("%2s %15s %15s\n", "ID", "First Name", "Last Name");
        System.out.println("------------------------------------");

        // Goes through each row
        while (resultSet.next()) {
            System.out.printf("%2d %15s %15s\n", resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
        }
        System.out.println("------------------------------------");
    }

    public static void displayAllCourses() throws SQLException {
        // SQL query that selects everything from the course table
        String allCoursesSQL = "SELECT * "
                + "FROM course";

        // Prepares the statment
        statement = connection.prepareStatement(allCoursesSQL);

        // Executing the statement that just got prepared
        resultSet = statement.executeQuery();

        // Printing the id, course name, time
        System.out.println("\nList Of All Courses");
        System.out.println("----------------------------");
        System.out.printf("%2s %10s %10s\n", "ID", "Course", "Time");
        System.out.println("----------------------------");

        // Goes through each row
        while (resultSet.next()) {
            System.out.printf("%2d %10s %10s\n", resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
        }
        System.out.println("----------------------------");
    }

    public static void menu() throws SQLException {
        int selection;
        boolean keepAsking = true;
        boolean continueInput = true;

        int numRowsAffected = 0;

        // This will keep asking the user to select from the options given and will catch any wrong inputs
        while (keepAsking) {
            try {
                System.out.print("\nMenu"
                        + "\n1: List Courses given a student first name and last name"
                        + "\n2: List Students given a course name"
                        + "\n3: Add Course"
                        + "\n4: Add Student"
                        + "\n5. Enroll a Student in a Course"
                        + "\n6. Un-enroll a Student from a Course"
                        + "\n7. Remove a Course"
                        + "\n8. Exit"
                        + "\n\nPlease choose what you'd like to do: ");
                selection = scanner.nextInt();
                // Making the user enter a positive number
                if (selection < 0) {
                    throw new IllegalArgumentException("\nSelection must be positive.");
                }
                continueInput = false;

                // Menu options
                switch (selection) {
                    case 1: // List Courses given a student first name and last name

                        // Calling method that dispalys all the students available 
                        displayAllStudents();

                        // Asking user to enter first name
                        System.out.print("First Name: ");
                        String userFirstName = scanner.next();

                        // Asking user to enter last name
                        System.out.print("Last Name: ");
                        String userLastName = scanner.next();

                        // Checking if user enters a letter for first name and last name
                        if (!(userFirstName.matches("[a-zA-Z]+") && userLastName.matches("[a-zA-Z]+"))) {
                            System.out.println("\nFirst name and last name must be a letter");
                        } else {
                            String courseNameSQL = "SELECT c.name, c.time "
                                    + "FROM student s, course c, studentcourse sc "
                                    + "WHERE sc.studentId = s.id AND sc.courseId = c.id AND s.firstname = ? AND s.lastname = ?";

                            statement = connection.prepareStatement(courseNameSQL);
                            statement.setString(1, userFirstName);
                            statement.setString(2, userLastName);
                            resultSet = statement.executeQuery();

                            // Checks if given student is enrolled in any courses
                            if (!resultSet.next()) {
                                System.out.printf("\n%s %s is not enrolled in any courses\n", userFirstName, userLastName);
                            } else {
                                // Printing out result
                                System.out.printf("\nList Of Courses That %s %s Is Enrolled In\n", userFirstName, userLastName);
                                System.out.println("--------------------------");
                                System.out.printf("%10s %10s\n", "Course", "Time");
                                System.out.println("--------------------------");
                                do {
                                    String course = resultSet.getString(1);
                                    String time = resultSet.getString(2);
                                    System.out.printf("%10s %10s\n", course, time);
                                } while (resultSet.next());
                                System.out.println("--------------------------");
                            }
                        }
                        break;
                    case 2: // List Students given a course name    

                        // Calling method that dispalys all the courses available 
                        displayAllCourses();

                        // Asking user to enter course name
                        System.out.print("Course Name: ");
                        String userCourseName = scanner.next();

                        // Checking if user enters a letter for course name
                        if (!userCourseName.matches("[a-zA-Z]+")) {
                            System.out.println("\nCourse name must be a letter");
                        } else {
                            String firstLastNameSQL = "SELECT s.firstname, s.lastname "
                                    + "FROM student s, course c, studentcourse sc "
                                    + "WHERE sc.studentId = s.id AND sc.courseId = c.id AND c.name = ?;";

                            statement = connection.prepareStatement(firstLastNameSQL);
                            statement.setString(1, userCourseName);
                            resultSet = statement.executeQuery();
                            resultSet.next();

                            // Checks if students are enrolled in the course given
                            if (!resultSet.next()) {
                                System.out.printf("\nThere are no students enrolled in %s\n", userCourseName);
                            } else {
                                System.out.printf("\nList Of Students That Are Enrolled in %s\n", userCourseName);
                                System.out.println("------------------------------------");
                                System.out.printf("%15s %15s\n", "First Name", "Last Name");
                                System.out.println("------------------------------------");
                                do {
                                    String firstName = resultSet.getString(1);
                                    String lastName = resultSet.getString(2);
                                    System.out.printf("%15s %15s\n", firstName, lastName);
                                } while (resultSet.next());
                                System.out.println("------------------------------------");
                            }
                        }
                        break;
                    case 3: // Add Course

                        // Calling method that dispalys all the courses available 
                        displayAllCourses();

                        // Asking user to enter course name
                        System.out.print("Course Name: ");
                        userCourseName = scanner.next();

                        // Asking user to enter time
                        System.out.print("Time: ");
                        String userTime = scanner.next();

                        // Checking if user enters a letter for course name and proper format for time
                        if (!(userCourseName.matches("[a-zA-Z]+") && userTime.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]"))) {
                            System.out.println("\nCourse name must be a letter and time must match HH:MM format");
                        } else {
                            String insertCourse = "INSERT INTO course (name, time)"
                                    + "VALUES (?, ?)";

                            statement = connection.prepareStatement(insertCourse);
                            statement.setString(1, userCourseName);
                            statement.setString(2, userTime);

                            numRowsAffected = statement.executeUpdate();
                            System.out.println(numRowsAffected + " row(s) affeted");
                        }
                        break;
                    case 4: // Add Student

                        // Asking user to enter first name, last name, age
                        System.out.print("First Name: ");
                        userFirstName = scanner.next();

                        // Asking user to enter last name
                        System.out.print("Last Name: ");
                        userLastName = scanner.next();

                        // Asking user to enter age
                        System.out.print("Age: ");
                        int userAge = scanner.nextInt();

                        // Checking if user enters a letter for first name and last name and number for age
                        if (!(userFirstName.matches("[a-zA-Z]+") && userLastName.matches("[a-zA-Z]+"))) {
                            System.out.println("\nFirst name and last name must be a letter");
                        } else {
                            String insertStudent = "INSERT INTO student (firstname, lastname, age)"
                                    + "VALUES (?, ?, ?)";

                            statement = connection.prepareStatement(insertStudent);
                            statement.setString(1, userFirstName);
                            statement.setString(2, userLastName);
                            statement.setInt(3, userAge);

                            numRowsAffected = statement.executeUpdate();
                            System.out.println(numRowsAffected + " row(s) affeted");
                        }
                        break;
                    case 5: // Enroll a Student in a Course

                        // Calling method that dispalys all the students 
                        displayAllStudents();

                        // Asking uder to enter student id
                        System.out.print("Select Student By ID: ");
                        int studentId = scanner.nextInt();

                        // Calling method that dispalys all the courses 
                        displayAllCourses();

                        // Asking user to enter course id
                        System.out.print("Select Course By ID: ");
                        int courseId = scanner.nextInt();

                        String insertStudentCourse = "INSERT INTO studentcourse (studentId, courseId)"
                                + "VALUES (?, ?)";

                        statement = connection.prepareStatement(insertStudentCourse);
                        statement.setInt(1, studentId);
                        statement.setInt(2, courseId);

                        numRowsAffected = statement.executeUpdate();
                        System.out.println(numRowsAffected + " row(s) affeted");
                        break;
                    case 6: // Un-enroll a Student from a Course

                        // Calling method that dispalys all the students 
                        displayAllStudents();

                        // Asking user to enter student id
                        System.out.print("Select Student By ID: ");
                        studentId = scanner.nextInt();

                        String courseNameSQL = "SELECT c.id, c.name, c.time "
                                + "FROM student s, course c, studentcourse sc "
                                + "WHERE sc.studentId = s.id AND sc.courseId = c.id AND s.id= ?";

                        statement = connection.prepareStatement(courseNameSQL);
                        statement.setInt(1, studentId);
                        resultSet = statement.executeQuery();

                        // Checks if given student is enrolled in any courses
                        if (!resultSet.next()) {
                            System.out.printf("\nStudent ID %s is not enrolled in any courses\n", studentId);
                        } else {
                            // Printing out result
                            System.out.printf("\nList Of Courses That Student ID %s Is Enrolled In\n", studentId);
                            System.out.println("--------------------------");
                            System.out.printf("%2s %10s %10s\n", "ID", "Course", "Time");
                            System.out.println("--------------------------");
                            do {
                                int id = resultSet.getInt(1);
                                String course = resultSet.getString(2);
                                String time = resultSet.getString(3);
                                System.out.printf("%2d %10s %10s\n", id, course, time);
                            } while (resultSet.next());
                            System.out.println("--------------------------");

                            // Asking user to enter course id
                            System.out.print("Select Course By ID: ");
                            courseId = scanner.nextInt();

                            String removeStudentCourse = "DELETE FROM studentcourse "
                                    + "WHERE studentId = ? AND courseId = ?";
                            statement = connection.prepareStatement(removeStudentCourse);
                            statement.setInt(1, studentId);
                            statement.setInt(2, courseId);

                            numRowsAffected = statement.executeUpdate();
                            System.out.println(numRowsAffected + " row(s) affeted");
                        }
                        break;
                    case 7: // Remove a Course

                        // Calling method that dispalys all the courses 
                        displayAllCourses();

                        // Asking user to enter course id
                        System.out.print("Select Course By ID: ");
                        courseId = scanner.nextInt();

                        String displayStudentCourse = "SELECT COUNT(*) AS count FROM studentcourse "
                                + "WHERE courseId = ?";
                        statement = connection.prepareStatement(displayStudentCourse);
                        statement.setInt(1, courseId);
                        resultSet = statement.executeQuery();
                        resultSet.next();

                        if (resultSet.getInt("count") != 0) {
                            System.out.println("\nThere are students enrolled in this course");

                            String studentsEnrolledSQL = "SELECT s.firstname, s.lastname, c.name FROM student s, course c, studentcourse sc "
                                    + "WHERE sc.studentId = s.id AND sc.courseId = c.id AND c.id = ?";

                            statement = connection.prepareStatement(studentsEnrolledSQL);
                            statement.setInt(1, courseId);
                            resultSet = statement.executeQuery();
                            resultSet.next();

                            System.out.print("\nStudents Are Enrolled In That Course\n");
                            System.out.println("---------------------------------------------------");
                            System.out.printf("%15s %15s %15s\n", "First Name", "Last Name", "Course");
                            System.out.println("---------------------------------------------------");
                            do {
                                String firstName = resultSet.getString(1);
                                String lastName = resultSet.getString(2);
                                String courseName = resultSet.getString(3);
                                System.out.printf("%15s %15s %15s\n", firstName, lastName, courseName);
                            } while (resultSet.next());
                            System.out.println("---------------------------------------------------");

                        } else {
                            String removeCourseSQL = "DELETE FROM course "
                                    + "WHERE id = ?";

                            statement = connection.prepareStatement(removeCourseSQL);
                            statement.setInt(1, courseId);

                            numRowsAffected = statement.executeUpdate();
                            System.out.println(numRowsAffected + " row(s) affeted");
                        }
                        break;
                    case 8:
                        // Exit
                        keepAsking = false;
                        break;
                    default:
                        System.out.println("\nInvalid entry. Enter from the options given.");
                        break;
                }
            } catch (InputMismatchException error) {
                System.out.println("\nTry again. (Incorrect input: a number is required)");
                // Discard input
                scanner.nextLine();
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
                // Discard input
                scanner.nextLine();
            }
        }
        if (connection != null) {
            connection.close();
        }
        if (statement != null) {
            statement.close();
        }
    }
}
