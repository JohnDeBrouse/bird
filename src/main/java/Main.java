import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Main {

  public static void main (String[] args) throws IOException {


    Workbook testScoresExcel = new XSSFWorkbook(Constants.RESOURCESPATH +(Constants.TESTSCORE));
    Workbook testRetakeExcel = new XSSFWorkbook(Constants.RESOURCESPATH +(Constants.TESTRETAKE));
    Workbook studentInfoExcel = new XSSFWorkbook(Constants.RESOURCESPATH +(Constants.STUDENTINFO));

    // Assumes all excel files only have 1 worksheet
    Sheet testScores = testScoresExcel.getSheetAt(0);
    Sheet testRetake = testRetakeExcel.getSheetAt(0);
    Sheet studentInfo = studentInfoExcel.getSheetAt(0);
    ArrayList<StudentInfo> studentInfos = new ArrayList<StudentInfo>();
    String[] femaleCompSci;

    HashMap<Integer, Integer> finalTestScores = new HashMap<Integer, Integer>();
    DataFormatter dataFormatter = new DataFormatter();


    // Skips first row
    for (int i = 1; i < testScores.getLastRowNum()+1; i++) {
      Row row = testScores.getRow(i);
        int studentId = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(0)));
        int studentScore = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(1)));

        finalTestScores.put(studentId, studentScore);
    }

    for (int i = 1; i < testRetake.getLastRowNum()+1; i++) {
      Row row = testRetake.getRow(i);
      int studentId = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(0)));
      int studentScore = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(1)));

      //Checks if new score is greater than their old score
      if (finalTestScores.containsKey(studentId)){
        if (studentScore > finalTestScores.get(studentId)){
          finalTestScores.put(studentId, studentScore);
        }
      }
    }

    femaleCompSci = new String[studentInfo.getLastRowNum()+1];
    for (int i = 1; i < studentInfo.getLastRowNum()+1; i++) {
      Row row = studentInfo.getRow(i);
      int studentId = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(0)));
      String major = dataFormatter.formatCellValue(row.getCell(1));
      char gender = dataFormatter.formatCellValue(row.getCell(2)).charAt(0);
      studentInfos.add(new StudentInfo(studentId, major, gender));
    }

    JSONObject json = new JSONObject();
    json.put("id", "soken100@gmail.com");
    json.put("name", "John DeBrouse");
    json.put("average", calcMean(finalTestScores.values().toArray(new Integer[0])));
    json.put("studentIds",findFemaleCompSci(studentInfos));

    System.out.print(json.toString());

  }


  private static String[] findFemaleCompSci(ArrayList<StudentInfo> array){
    ArrayList<Integer> list = new ArrayList<Integer>();

    for (int i = 0; i < array.size(); i++){
      StudentInfo student = array.get(i);
      if (student.getMajor().equals("computer science") && student.getGender() == 'F'){
        list.add(student.getStudentId());
      }
    }
    Collections.sort(list);
    String[] finalArray = new String[list.size()];
    for (int i = 0; i < list.size(); i++){
      finalArray[i] = list.get(i).toString();
    }
    return finalArray;
  }

  private static int calcMean(Integer[] array){
    int total = 0;
    if (array.length == 1){
      return array[0];
    }
    else{
     for (int i = 0; i < array.length; i++){
       total = total + array[i];
     }
    }

    float roundMe = total/array.length;
    return Math.round(roundMe);
  }
}
