public class StudentInfo {
  private String major;
  private char gender;
  private int studentId;

  public StudentInfo(int studentId, String major, char gender) {
    this.major = major;
    this.gender = gender;
    this.studentId = studentId;
  }

  public void setMajor(String major) {
    this.major = major;
  }

  public void setGender(char gender) {
    this.gender = gender;
  }

  public void setStudentId(char studentId) {
    this.studentId = studentId;
  }

  public String getMajor() {
    return major;
  }

  public char getGender() {
    return gender;
  }

  public int getStudentId() {
    return studentId;
  }

}
