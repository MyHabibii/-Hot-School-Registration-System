package hotschool;

// Imports
import java.util.ArrayList;

/**
 *
 * @author Bilaal & Zain
 */
public class StudentList {

    // ArrayList to store student information
    private ArrayList<Student> studentList;

    public StudentList() {
        this.studentList = new ArrayList<>();
    }

    public ArrayList<Student> getStudentList() {
        return studentList;
    }

    public void add(Student student) {
        studentList.add(student);
    }

    public void remove(Student student) {
        studentList.remove(student);
    }

    public void clear() {
        studentList.clear();
    }
}
