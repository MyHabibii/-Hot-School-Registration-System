package hotschool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author bilaa
 */
public class HotSchool {

    // Outside of main so that all methods can use it
    public static Scanner scanner = new Scanner(System.in);
    public static Connection connection = DBConnector.getConnection();
    public static PreparedStatement statement = null;
    public static ResultSet resultSet = null;
    private static StudentList studentList = new StudentList();
    private static CourseList courseList = new CourseList();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {

        menu();

    }

    // This method adds courses to list
    public static void courseList() throws SQLException {
        // Refreshing the course list
        courseList.clear();

        // Writing SQL query that selects the everything from the course table 
        resultSet = connection.createStatement().executeQuery("SELECT * FROM course");

        // Adding courses from database to list
        while (resultSet.next()) {
            courseList.add(new Course(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("time")));
        }
    }

    // This method adds students to list
    public static void studentList() throws SQLException {
        // Refreshing the student list
        studentList.clear();

        // Writing SQL query that selects everything from the student table 
        resultSet = connection.createStatement().executeQuery("SELECT * FROM student");

        // Adding students from database to list
        while (resultSet.next()) {
            studentList.add(new Student(resultSet.getInt("id"), resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getInt("age")));
        }
    }

    // This method displays all the available students
    public static void displayAllStudents() throws SQLException {
        // Formating output for console 
        System.out.printf("%2s %15s %15s %5s\n", "ID", "First Name", "Last Name", "Age");
        System.out.println("------------------------------------------");

        // Loop that goes through studentList
        for (int i = 0; i < studentList.getStudentList().size(); i++) {
            int id = studentList.getStudentList().get(i).getStudentID();
            String firstName = studentList.getStudentList().get(i).getFirstName();
            String lastName = studentList.getStudentList().get(i).getLastName();
            int age = studentList.getStudentList().get(i).getStudentAge();

            System.out.printf("%2d %15s %15s %5d\n", id, firstName, lastName, age);
        }

        // Formating output for console
        System.out.println("------------------------------------------");
    }

    public static void displayAllCourses() throws SQLException {
        // Formating output for console 
        System.out.printf("%2s %15s %15s\n", "ID", "Course Name", "Time");
        System.out.println("-----------------------------------");

        // Loop that goes through courseList
        for (int i = 0; i < courseList.getCourseList().size(); i++) {
            int id = courseList.getCourseList().get(i).getCourseID();
            String courseName = courseList.getCourseList().get(i).getCourseName();
            String courseTime = courseList.getCourseList().get(i).getCourseTime();

            System.out.printf("%2d %15s %15s\n", id, courseName, courseTime);
        }

        // Formating output for console
        System.out.println("-----------------------------------");
    }

    public static void listCoursesByStudentName(String firstname, String lastName) throws SQLException {
        statement = connection.prepareStatement("SELECT c.name, c.time FROM student s, course c, studentcourse sc "
                + "WHERE sc.studentId = s.id AND sc.courseId = c.id AND s.firstname = ? AND s.lastname = ?");
        statement.setString(1, firstname);
        statement.setString(2, lastName);
        resultSet = statement.executeQuery();

        // Checks if given student is enrolled in any courses
        if (!resultSet.next()) {
            System.out.printf("\n%s %s is not enrolled in any courses\n", firstname, lastName);
        } else {
            // Formating output for console 
            System.out.printf("\nList Of Courses That %s %s Is Enrolled In\n", firstname, lastName);
            System.out.println("--------------------------------");
            System.out.printf("%13s %15s\n", "Course Name", "Time");
            System.out.println("--------------------------------");
            do {
                String courseName = resultSet.getString("name");
                String time = resultSet.getString("time");
                System.out.printf("%13s %15s\n", courseName, time);
            } while (resultSet.next());
            System.out.println("--------------------------------");
        }
    }

    public static String courseNameValidation() {
        String userCourseName = "";
        do {
            // Asking user to enter course name
            System.out.print("Course Name: ");
            userCourseName = scanner.next();
            if (!Course.isValidCourseName(userCourseName)) {
                System.out.println("\nCourse name must be a letter\n");
            }
        } while (!Course.isValidCourseName(userCourseName));
        return userCourseName;
    }

    public static void menu() throws SQLException {
        int selection;
        int numRowsAffected = 0;
        boolean keepAsking = true;
        boolean continueInput = true;

        // Loading students
        //List<Student> students = studentList();
        studentList();
        // Loading courses
        courseList();

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

                        String userFirstName = "";
                        String userLastName = "";

                        // Validating first name
                        do {
                            // Asking user to enter first name
                            System.out.print("First Name: ");
                            userFirstName = scanner.next();
                            if (!Student.isValidStudentName(userFirstName)) {
                                System.out.println("\nFirst name must a letter\n");
                            }
                        } while (!Student.isValidStudentName(userFirstName));

                        do {
                            /// Asking user to enter last name
                            System.out.print("Last Name: ");
                            userLastName = scanner.next();
                            if (!Student.isValidStudentName(userLastName)) {
                                System.out.println("\nLast name must be a letter\n");
                            }
                        } while (!Student.isValidStudentName(userLastName));

                        listCoursesByStudentName(userFirstName, userLastName);
                        break;
                    case 2: // List Students given a course name    

                        // Calling method that dispalys all the courses available 
                        displayAllCourses();
                        
                        // Validating for course name
                        String userCourseName = courseNameValidation();
                        
                        String firstLastNameSQL = "SELECT s.firstname, s.lastname "
                                + "FROM student s, course c, studentcourse sc "
                                + "WHERE sc.studentId = s.id AND sc.courseId = c.id AND c.name = ?;";

                        statement = connection.prepareStatement(firstLastNameSQL);
                        statement.setString(1, userCourseName);
                        resultSet = statement.executeQuery();

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
                        break;
                    case 3: // Add Course

                        // Calling method that dispalys all the courses available 
                        displayAllCourses();

                        String userTime = "";

                        do {
                            // Asking user to enter course name
                            System.out.print("Course Name: ");
                            userCourseName = scanner.next();
                            if (!Course.isValidCourseName(userCourseName)) {
                                System.out.println("\nCourse name must be a letter\n");
                            }
                        } while (!Course.isValidCourseName(userCourseName));

                        do {
                            // Asking user to enter time
                            System.out.print("Time: ");
                            userTime = scanner.next();
                            if (!Course.isValidCourseTime(userTime)) {
                                System.out.println("\nCourse time must be in HH:MM format\n");
                            }
                        } while (!Course.isValidCourseTime(userTime));

                        String insertCourse = "INSERT INTO course (name, time)"
                                + "VALUES (?, ?)";

                        statement = connection.prepareStatement(insertCourse);
                        statement.setString(1, userCourseName);
                        statement.setString(2, userTime);

                        numRowsAffected = statement.executeUpdate();
                        System.out.println(numRowsAffected + " row(s) affeted");

                        // Updating the course list
                        courseList();
                        break;
                    case 4: // Add Student

                        int userAge = 0;
                        // Validating first name
                        do {
                            // Asking user to enter first name
                            System.out.print("First Name: ");
                            userFirstName = scanner.next();
                            if (!Student.isValidStudentName(userFirstName)) {
                                System.out.println("\nFirst name must a letter\n");
                            }
                        } while (!Student.isValidStudentName(userFirstName));

                        do {
                            /// Asking user to enter last name
                            System.out.print("Last Name: ");
                            userLastName = scanner.next();
                            if (!Student.isValidStudentName(userLastName)) {
                                System.out.println("\nLast name must be a letter\n");
                            }
                        } while (!Student.isValidStudentName(userFirstName));

                        // Validating Age
                        do {
                            // Asking user to enter age
                            System.out.print("Age: ");
                            userAge = scanner.nextInt();
                            if (!(userAge < 0 || userAge < 110)) {
                                System.out.println("Please enter a valid age");
                            }
                        } while (!(userAge < 0 || userAge < 110));

                        String insertStudent = "INSERT INTO student (firstname, lastname, age)"
                                + "VALUES (?, ?, ?)";

                        statement = connection.prepareStatement(insertStudent);
                        statement.setString(1, userFirstName);
                        statement.setString(2, userLastName);
                        statement.setInt(3, userAge);

                        numRowsAffected = statement.executeUpdate();
                        System.out.println(numRowsAffected + " row(s) affeted");

                        // Updating the course list
                        studentList();
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

                            // Updating course list
                            courseList();
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
        if (connection
                != null) {
            connection.close();
        }
        if (statement
                != null) {
            statement.close();
        }
    }
}
