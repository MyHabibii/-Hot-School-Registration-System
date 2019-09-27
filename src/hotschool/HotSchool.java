package hotschool;

// Imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Bilaal & Zain
 */
public class HotSchool {

    // Outside of main so that all methods can use it
    private static final Scanner scanner = new Scanner(System.in);
    public static Connection connection = DBConnector.getConnection();
    private static PreparedStatement statement = null;
    private static ResultSet resultSet = null;
    private static final StudentList studentList = new StudentList();
    private static final CourseList courseList = new CourseList();
    private static int numRowsAffected = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        menu();
    }

    // This method adds courses to list
    private static void courseList() throws SQLException {
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
    private static void studentList() throws SQLException {
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
    private static void displayAllStudents() {
        // Formatting output for console
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

        // Formatting output for console
        System.out.println("------------------------------------------");
    }

    private static void displayAllCourses() {
        // Formatting output for console
        System.out.printf("%2s %15s %15s\n", "ID", "Course Name", "Time");
        System.out.println("-----------------------------------");

        // Loop that goes through courseList
        for (int i = 0; i < courseList.getCourseList().size(); i++) {
            int id = courseList.getCourseList().get(i).getCourseID();
            String courseName = courseList.getCourseList().get(i).getCourseName();
            String courseTime = courseList.getCourseList().get(i).getCourseTime();

            System.out.printf("%2d %15s %15s\n", id, courseName, courseTime);
        }

        // Formatting output for console
        System.out.println("-----------------------------------");
    }

    private static void listCoursesByStudentName(String firstName, String lastName) throws SQLException {
        statement = connection.prepareStatement("SELECT c.name, c.time FROM student s, course c, studentcourse sc "
                + "WHERE sc.studentId = s.id AND sc.courseId = c.id AND s.firstname = ? AND s.lastname = ?");
        statement.setString(1, firstName);
        statement.setString(2, lastName);
        resultSet = statement.executeQuery();

        // Checks if given student is enrolled in any courses
        if (!resultSet.next()) {
            System.out.printf("\n%s %s is not enrolled in any courses\n", firstName, lastName);
        } else {
            // Formatting output for console
            System.out.printf("\nList Of Courses That %s %s Is Enrolled In\n", firstName, lastName);
            System.out.println("--------------------------------");
            System.out.printf("%13s %15s\n", "Course Name", "Time");
            System.out.println("--------------------------------");
            do {
                System.out.printf("%13s %15s\n", resultSet.getString("name"), resultSet.getString("time"));
            } while (resultSet.next());

            // Formatting output for console
            System.out.println("--------------------------------");
        }
    }

    private static boolean listCoursesByStudentID(int studentID) throws SQLException {
        statement = connection.prepareStatement("SELECT c.id, c.name, c.time "
                + "FROM student s, course c, studentcourse sc "
                + "WHERE sc.studentId = s.id AND sc.courseId = c.id AND s.id= ?");
        statement.setInt(1, studentID);
        resultSet = statement.executeQuery();

        // Getting student's first and last name
        String firstName = studentList.getStudentList().get(studentID - 1).getFirstName();
        String lastName = studentList.getStudentList().get(studentID - 1).getLastName();

        // Checks if given student is enrolled in any courses
        if (!resultSet.next()) {
            System.out.printf("\n%s %s is not enrolled in any courses\n", firstName, lastName);
            return false;
        } else {
            // Formatting output for console
            System.out.printf("\nList Of Courses That %s %s Is Enrolled In\n", firstName, lastName);
            System.out.println("--------------------------");
            System.out.printf("%2s %10s %10s\n", "ID", "Course", "Time");
            System.out.println("--------------------------");
            do {
                int id = resultSet.getInt(1);
                String course = resultSet.getString(2);
                String time = resultSet.getString(3);
                System.out.printf("%2d %10s %10s\n", id, course, time);
            } while (resultSet.next());
            // Formatting output for console
            System.out.println("--------------------------");
            return true;
        }
    }

    private static boolean listCoursesStudentNotIn(int studentID) throws SQLException {
        statement = connection.prepareStatement("SELECT * FROM course "
                + "WHERE id NOT IN (SELECT courseId FROM studentcourse WHERE studentId = ?)");
        statement.setInt(1, studentID);
        resultSet = statement.executeQuery();

        // Getting student's first and last name
        String firstName = studentList.getStudentList().get(studentID - 1).getFirstName();
        String lastName = studentList.getStudentList().get(studentID - 1).getLastName();
        
        if (!resultSet.next()) {
            System.out.printf("\n%s %s is enrolled in all the courses.", firstName, lastName);
            return false;
        } else {
            // Formatting output for console
            System.out.printf("\nList Of Courses That %s %s Is Not Enrolled In\n", firstName, lastName);
            System.out.println("--------------------------");
            System.out.printf("%2s %10s %10s\n", "ID", "Course", "Time");
            System.out.println("--------------------------");
            do {
                int id = resultSet.getInt(1);
                String course = resultSet.getString(2);
                String time = resultSet.getString(3);
                System.out.printf("%2d %10s %10s\n", id, course, time);
            } while (resultSet.next());
        }

        // Formatting output for console
        System.out.println("--------------------------");
        return true;
    }

    private static void listStudentsByCourse(String courseName) throws SQLException {
        statement = connection.prepareStatement("SELECT s.firstname, s.lastname, s.age "
                + "FROM student s, course c, studentcourse sc "
                + "WHERE sc.studentId = s.id AND sc.courseId = c.id AND c.name = ?");
        statement.setString(1, courseName);
        resultSet = statement.executeQuery();

        if (!resultSet.next()) {
            System.out.printf("\nThere are no students enrolled in %s\n", courseName);
        } else {
            // Formatting output for console
            System.out.printf("\nList Of Students That Are Enrolled in %s\n", courseName);
            System.out.println("--------------------------------------");
            System.out.printf("%15s %15s %5s\n", "First Name", "Last Name", "Age");
            System.out.println("--------------------------------------");
            do {
                System.out.printf("%15s %15s %5d\n", resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getInt("age"));
            } while (resultSet.next());

            // Formatting output for console
            System.out.println("--------------------------------------");
        }
    }

    private static void addCourse(String courseName, String courseTime) throws SQLException {
        statement = connection.prepareStatement("INSERT INTO course (name, time)"
                + "VALUES (?, ?)");
        statement.setString(1, courseName);
        statement.setString(2, courseTime);

        numRowsAffected = statement.executeUpdate();
        System.out.println(numRowsAffected + " row(s) affected");
        System.out.printf("\n%s was successfully added to system", courseName);
    }

    private static void addStudent(String firstName, String lastName, int age) throws SQLException {
        statement = connection.prepareStatement("INSERT INTO student (firstname, lastname, age)"
                + "VALUES (?, ?, ?)");

        statement.setString(1, firstName);
        statement.setString(2, lastName);
        statement.setInt(3, age);

        numRowsAffected = statement.executeUpdate();
        System.out.println(numRowsAffected + " row(s) affected");
        System.out.printf("\n%s %s was successfully added to system", firstName, lastName);
    }

    private static void enrollStudent(int studentID, int courseID) throws SQLException {
        statement = connection.prepareStatement("INSERT INTO studentcourse (studentId, courseId)"
                + "VALUES (?, ?)");

        statement.setInt(1, studentID);
        statement.setInt(2, courseID);

        // Getting course name
        String courseName = courseList.getCourseList().get(courseID - 1).getCourseName();

        // Getting student's first and last name
        String firstName = studentList.getStudentList().get(studentID - 1).getFirstName();
        String lastName = studentList.getStudentList().get(studentID - 1).getLastName();

        numRowsAffected = statement.executeUpdate();
        System.out.println(numRowsAffected + " row(s) affected");
        System.out.printf("\n%s %s was successfully enrolled into %s", firstName, lastName, courseName);
    }

    private static void unEnrollStudent(int studentID, int courseID) throws SQLException {
        statement = connection.prepareStatement("DELETE FROM studentcourse "
                + "WHERE studentId = ? AND courseId = ?");

        statement.setInt(1, studentID);
        statement.setInt(2, courseID);

        // Getting course name
        String courseName = courseList.getCourseList().get(courseID - 1).getCourseName();

        // Getting student's first and last name
        String firstName = studentList.getStudentList().get(studentID - 1).getFirstName();
        String lastName = studentList.getStudentList().get(studentID - 1).getLastName();

        numRowsAffected = statement.executeUpdate();
        System.out.println(numRowsAffected + " row(s) affected");
        System.out.printf("\n%s %s was successfully un-enrolled from %s", firstName, lastName, courseName);

        courseList();
    }

    private static void removeCourse(int courseID) throws SQLException {
        statement = connection.prepareStatement("SELECT COUNT(*) AS count FROM studentcourse "
                + "WHERE courseId = ?");

        statement.setInt(1, courseID);
        resultSet = statement.executeQuery();
        resultSet.next();

        // Getting course name
        String courseName = courseList.getCourseList().get(courseID - 1).getCourseName();

        if (resultSet.getInt("count") != 0) {
            System.out.println("\nThere are students enrolled in " + courseList.getCourseList().get(courseID - 1).getCourseName());

            statement = connection.prepareStatement("SELECT s.firstname, s.lastname, c.name "
                    + "FROM student s, course c, studentcourse sc "
                    + "WHERE sc.studentId = s.id AND sc.courseId = c.id AND c.id = ?");

            statement.setInt(1, courseID);
            resultSet = statement.executeQuery();
            resultSet.next();

            // Formatting output for console
            System.out.printf("\nStudents That Are Enrolled In %s\n", courseName);
            System.out.println("---------------------------------------------------");
            System.out.printf("%15s %15s %15s\n", "First Name", "Last Name", "Course");
            System.out.println("---------------------------------------------------");
            do {
                String firstName = resultSet.getString(1);
                String lastName = resultSet.getString(2);
                courseName = resultSet.getString(3);
                System.out.printf("%15s %15s %15s\n", firstName, lastName, courseName);
            } while (resultSet.next());
            System.out.println("---------------------------------------------------");

        } else {
            statement = connection.prepareStatement("DELETE FROM course "
                    + "WHERE id = ?");

            statement.setInt(1, courseID);

            numRowsAffected = statement.executeUpdate();
            System.out.println(numRowsAffected + " row(s) affected");
            System.out.printf("\n%s was successfully removed from system", courseName);

            // Updating course list
            courseList();
        }
    }

    private static String nameValidation(String type) {
        String name;
        do {
            // Asking user to enter course name
            System.out.print(type + " Name: ");
            name = scanner.next();
            if (!Course.isValidCourseName(name)) {
                System.out.println("\n" + type + " name must be a letter\n");
            }
        } while (!Course.isValidCourseName(name));
        return name;
    }

    private static String timeValidation() {
        String courseTime;
        do {
            // Asking user to enter time
            System.out.print("Time: ");
            courseTime = scanner.next();
            if (!Course.isValidCourseTime(courseTime)) {
                System.out.println("\nCourse time must be in HH:MM format\n");
            }
        } while (!Course.isValidCourseTime(courseTime));
        return courseTime;
    }

    private static int ageValidation() {
        int age;
        do {
            // Asking user to enter age
            System.out.print("Age: ");
            age = scanner.nextInt();
            if (!(age > 0 && age < 110)) {
                System.out.println("Please enter a valid age");
            }
        } while (!(age > 0 && age < 110));
        return age;
    }

    private static void menu() throws SQLException {
        int selection;

        boolean keepAsking = true;

        // Loading students
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

                // Menu options
                switch (selection) {
                    case 1: // List Courses given a student first name and last name

                        // Calling method that displays all the students available
                        displayAllStudents();

                        // Validating first name
                        String firstName = nameValidation("First");

                        // Validating last name
                        String lastName = nameValidation("Last");

                        // Calling method that displays the courses that the student is in
                        listCoursesByStudentName(firstName, lastName);
                        break;
                    case 2: // List Students given a course name    

                        // Calling method that displays all the courses available
                        displayAllCourses();

                        // Validating course name
                        String userCourseName = nameValidation("Course");

                        // Calling method that displays the students that are in the course
                        listStudentsByCourse(userCourseName);
                        break;
                    case 3: // Add Course

                        // Calling method that displays all the courses available
                        displayAllCourses();

                        // Validating course name
                        userCourseName = nameValidation("Course");

                        // Validating course time
                        String courseTime = timeValidation();

                        // Calling method that adds course to list
                        addCourse(userCourseName, courseTime);

                        // Updating the course list
                        courseList();
                        break;
                    case 4: // Add Student

                        // Validating first name
                        firstName = nameValidation("First");

                        // Validating last name
                        lastName = nameValidation("Last");

                        // Validating Age
                        int age = ageValidation();

                        // Calling method that adds student to list
                        addStudent(firstName, lastName, age);

                        // Updating the course list
                        studentList();
                        break;
                    case 5: // Enroll a Student in a Course

                        // Calling method that displays all the students
                        displayAllStudents();

                        // Asking user to enter student id
                        System.out.print("Select Student By ID: ");
                        int studentID = scanner.nextInt();

                        int courseID;

                        // Calling method that displays courses that student is not in
                        if (listCoursesStudentNotIn(studentID)) {
                            // Asking user to enter course id
                            System.out.print("Select Course By ID: ");
                            courseID = scanner.nextInt();

                            // Calling method that enrolls student to a course
                            enrollStudent(studentID, courseID);
                        }
                        break;
                    case 6: // Un-enroll a Student from a Course

                        // Calling method that displays all the students
                        displayAllStudents();

                        // Asking user to enter student id
                        System.out.print("Select Student By ID: ");
                        studentID = scanner.nextInt();

                        // Calling method that displays the students that are in the course
                        if (listCoursesByStudentID(studentID)) {
                            // Asking user to enter course id
                            System.out.print("Select Course By ID: ");
                            courseID = scanner.nextInt();

                            // Calling method that un-enrolls student from course
                            unEnrollStudent(studentID, courseID);
                        }
                        break;
                    case 7: // Remove a Course

                        // Calling method that displays all the courses
                        displayAllCourses();

                        // Asking user to enter course id
                        System.out.print("Select Course By ID: ");
                        courseID = scanner.nextInt();

                        // Calling method that removes course
                        removeCourse(courseID);
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
            } catch (IndexOutOfBoundsException ex) {
                System.out.println("\nTry again. (Incorrect input: Out of bounds)");
            } catch (SQLException ignored) {

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
