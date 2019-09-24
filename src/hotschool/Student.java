package hotschool;

/**
 * This class implements a student
 *
 * @author bilaa
 */
public class Student {

    // Data fields
    private int studentID, studentAge;
    private String firstName, lastName;
    private CourseList courseList;

    /**
     * 4 parameter constructor to create a student
     *
     * @param studentID The ID of student
     * @param studentAge The age of student
     * @param firstName The first name of student
     * @param lastName The last name of student
     */
    public Student(int studentID, String firstName, String lastName, int studentAge) {
        this.studentID = studentID;
        this.studentAge = studentAge;
        this.firstName = firstName;
        this.lastName = lastName;
        this.courseList = new CourseList();
    }

    Student() {

    }

    // Getter for studentID
    public int getStudentID() {
        return studentID;
    }

    // Setter for studentID
    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    // Getter for studentAge
    public int getStudentAge() {
        return studentAge;
    }

    // Setter for studentAge
    public void setStudentAge(int studentAge) {
        this.studentAge = studentAge;
    }

    // Getter for firstName
    public String getFirstName() {
        return firstName;
    }

    // Setter for firstName
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Getter for lastName
    public String getLastName() {
        return lastName;
    }

    // Setter for lastName
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    // Validating student's name
    public static boolean isValidStudentName(String firstName) {
        String validName = "[a-zA-Z]+";
        if (firstName.matches(validName)) {
            return true;
        }
        return false;
    }

}
