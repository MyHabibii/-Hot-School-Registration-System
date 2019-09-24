package hotschool;

import java.util.ArrayList;

/**
 *
 * @author bilaa
 */
public class CourseList {

    // ArrayList to store course information
    private ArrayList<Course> courseList;

    public CourseList() {
        this.courseList = new ArrayList<>();
    }

    public ArrayList<Course> getCourseList() {
        return courseList;
    }

    public void add(Course course) {
        courseList.add(course);
    }
    
    public void remove(Course course) {
        courseList.remove(course);
    }
    
    public void clear() {
        courseList.clear();
    }

}
