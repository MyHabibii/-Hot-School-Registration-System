package hotschool;

/**
 * This class implements a course
 *
 * @author Bilaal & Zain
 */
public class Course {

    // Data fields
    private int courseID;
    private String courseName, courseTime;

    /**
     * 3 parameter constructor to create a course
     *
     * @param courseID The ID of course
     * @param courseName The name of course
     * @param courseTime The time of course
     */
    public Course(int courseID, String courseName, String courseTime) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.courseTime = courseTime;
    }

    // Getter for courseID
    public int getCourseID() {
        return courseID;
    }

    // Setter for courseID
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    // Getter for courseName
    public String getCourseName() {
        return courseName;
    }

    // Setter for courseName
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Getter for courseTime
    public String getCourseTime() {
        return courseTime;
    }

    // Setter for courseTime
    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }
    
    // Validating course time
    public static boolean isValidCourseTime(String courseTime) {
        String validCourseTime = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]";
        if (courseTime.matches(validCourseTime)) {
            return true;
        }
        return false;
    }
    
    // Validating course time
    public static boolean isValidCourseName(String courseTime) {
        String validCourseTime = "[a-zA-Z]+";
        if (courseTime.matches(validCourseTime)) {
            return true;
        }
        return false;
    }

}
