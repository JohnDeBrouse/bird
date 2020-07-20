import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Main {

  public static void main(String[] args) throws IOException {


    Workbook testScoresExcel = new XSSFWorkbook(Constants.RESOURCESPATH + (Constants.TESTSCORE));
    Workbook testRetakeExcel = new XSSFWorkbook(Constants.RESOURCESPATH + (Constants.TESTRETAKE));
    Workbook studentInfoExcel = new XSSFWorkbook(Constants.RESOURCESPATH + (Constants.STUDENTINFO));

    // Assumes all excel files only have 1 worksheet
    Sheet testScores = testScoresExcel.getSheetAt(0);
    Sheet testRetake = testRetakeExcel.getSheetAt(0);
    Sheet studentInfo = studentInfoExcel.getSheetAt(0);
    ArrayList<StudentInfo> studentInfoList = new ArrayList<StudentInfo>();

    HashMap<Integer, Integer> finalTestScores = new HashMap<Integer, Integer>();
    DataFormatter dataFormatter = new DataFormatter();


    // Skips first row because they have field names
    for (int i = 1; i < testScores.getLastRowNum() + 1; i++) {
      Row row = testScores.getRow(i);
      int studentId = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(0)));
      int studentScore = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(1)));

      finalTestScores.put(studentId, studentScore);
    }

    for (int i = 1; i < testRetake.getLastRowNum() + 1; i++) {
      Row row = testRetake.getRow(i);
      int studentId = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(0)));
      int studentScore = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(1)));

      //Checks if new score is greater than their old score
      if (finalTestScores.containsKey(studentId)) {
        if (studentScore > finalTestScores.get(studentId)) {
          finalTestScores.put(studentId, studentScore);
        }
      }
    }

    for (int i = 1; i < studentInfo.getLastRowNum() + 1; i++) {
      Row row = studentInfo.getRow(i);
      int studentId = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(0)));
      String major = dataFormatter.formatCellValue(row.getCell(1));
      char gender = dataFormatter.formatCellValue(row.getCell(2)).charAt(0);
      studentInfoList.add(new StudentInfo(studentId, major, gender));
    }

    JSONObject json = new JSONObject();
    json.put("id", "soken100@gmail.com");
    json.put("name", "John DeBrouse");
    json.put("average", calculateRoundedMean(finalTestScores.values().toArray(new Integer[0])));
    json.put("studentIds", findFemaleCompSciStudent(studentInfoList));

    URL endpoint = new URL("http://54.90.99.192:5000/challenge");
    HttpURLConnection con = (HttpURLConnection) endpoint.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "application/json; utf-8");
    con.setRequestProperty("Accept", "application/json");
    con.setDoOutput(true);
    try (OutputStream os = con.getOutputStream()) {
      byte[] input = json.toString().getBytes("utf-8");
      os.write(input, 0, input.length);
    }
    catch (Exception e){
      System.out.print("There was a error : " + e);
    }

    try (BufferedReader br = new BufferedReader(
            new InputStreamReader(con.getInputStream(), "utf-8")))
    {
      StringBuilder response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
      System.out.println(response.toString());
    }
    catch (Exception e){
      System.out.print("There was a error : " + e);
    }

  }


  private static String[] findFemaleCompSciStudent(ArrayList<StudentInfo> array) {
    ArrayList<Integer> studentList = new ArrayList<Integer>();

    for (int i = 0; i < array.size(); i++) {
      StudentInfo student = array.get(i);
      if (student.getMajor().toLowerCase().equals("computer science") && Character.toLowerCase(student.getGender()) == 'f') {
        studentList.add(student.getStudentId());
      }
    }
    Collections.sort(studentList);
    String[] sortedStudentList = new String[studentList.size()];
    for (int i = 0; i < studentList.size(); i++) {
      sortedStudentList[i] = studentList.get(i).toString();
    }
    return sortedStudentList;
  }

  private static int calculateRoundedMean(Integer[] array) {
    int total = 0;
    if (array.length == 1) {
      return array[0];
    } else {
      for (int i = 0; i < array.length; i++) {
        total = total + array[i];
      }
    }

    float mean = total / array.length;
    return Math.round(mean);
  }
}
