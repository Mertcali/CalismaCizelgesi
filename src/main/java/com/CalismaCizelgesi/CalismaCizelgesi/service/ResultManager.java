package com.CalismaCizelgesi.CalismaCizelgesi.service;

import com.CalismaCizelgesi.CalismaCizelgesi.dao.EmployeeRepository;
import com.CalismaCizelgesi.CalismaCizelgesi.dto.ResponseDto;
import com.CalismaCizelgesi.CalismaCizelgesi.dto.WeekResponseDto;
import com.CalismaCizelgesi.CalismaCizelgesi.entities.Employee;
import com.CalismaCizelgesi.CalismaCizelgesi.entities.EmployeeWorkingDay;
import com.CalismaCizelgesi.CalismaCizelgesi.entities.MatchedEmployee;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultManager {

    private final EmployeeRepository employeeRepository;
    List<String> daysOfWeek = Arrays.asList("Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma");
    List<MatchedEmployee> matchedEmployeeList = new ArrayList<>();
    List<EmployeeWorkingDay> employeeWorkingDayListForGroup1 = new ArrayList<>();
    List<EmployeeWorkingDay> employeeWorkingDayListForGroup2 = new ArrayList<>();
    List<MatchedEmployee> matchedEmployeeListForWeek = new ArrayList<>();

    public List<WeekResponseDto> calculateFirstWeeksWorkingDays() {

        WeekResponseDto firstWeek = setWeeklyWorkingDay("FIRST_WEEK");
        WeekResponseDto secondWeek = setWeeklyWorkingDay("SECOND_WEEK");
        WeekResponseDto thirdWeek = setWeeklyWorkingDay("THIRD_WEEK");
        WeekResponseDto lastWeek = setWeeklyWorkingDay("LAST_WEEK");

        List<WeekResponseDto> response = new ArrayList<>();
        response.add(firstWeek);
        response.add(secondWeek);
        response.add(thirdWeek);
        response.add(lastWeek);

        return response;

    }

    private WeekResponseDto setWeeklyWorkingDay(String week){
        List<Employee> group1Employees = this.employeeRepository.findByGroupId(1L);
        List<Employee> group2Employees = this.employeeRepository.findByGroupId(2L);
        employeeWorkingDayListForGroup1 = setRandomlyWorkingDayForAllGroupMembers(group1Employees);
        employeeWorkingDayListForGroup2 = setRandomlyWorkingDayForAllGroupMembers(group2Employees);

        matchedEmployeeListForWeek = findMatchingEmployees(employeeWorkingDayListForGroup1, employeeWorkingDayListForGroup2);
        setMatchedEmployeesIfTheyMatchedLastWeek(group1Employees,group2Employees);
        matchedEmployeeList.clear(); // eğer bir ay boyunca aynı denk gelmesin diyorsa bunu kaldırıcam
        matchedEmployeeList.addAll(matchedEmployeeListForWeek);
        System.out.println(week + " " + "SETTED");

        writeToExcel(matchedEmployeeListForWeek, week);
        return WeekResponseDto.builder()
                .employeeWorkingDayListGroup1(employeeWorkingDayListForGroup1)
                .employeeWorkingDayListGroup2(employeeWorkingDayListForGroup2)
                .matchedEmployeeList(matchedEmployeeListForWeek)
                .week(week)
                .build();

    }

    private void setMatchedEmployeesIfTheyMatchedLastWeek(List<Employee> group1Employees,List<Employee> group2Employees){
        if(checkLastWeeksMatches(matchedEmployeeListForWeek,matchedEmployeeList)){
            employeeWorkingDayListForGroup1.clear();
            employeeWorkingDayListForGroup2.clear();
            matchedEmployeeListForWeek.clear();
            employeeWorkingDayListForGroup1 = setRandomlyWorkingDayForAllGroupMembers(group1Employees);
            employeeWorkingDayListForGroup2 = setRandomlyWorkingDayForAllGroupMembers(group2Employees);
            matchedEmployeeListForWeek = findMatchingEmployees(employeeWorkingDayListForGroup1, employeeWorkingDayListForGroup2);
            setMatchedEmployeesIfTheyMatchedLastWeek(group1Employees,group2Employees);
        }
    }

    private List<EmployeeWorkingDay> setRandomlyWorkingDayForAllGroupMembers(List<Employee> employeeList) {
        Random random = new Random();
        List<EmployeeWorkingDay> employeeWorkingDayList = new ArrayList<>();

        for (Employee employee : employeeList) {
            EmployeeWorkingDay employeeWorkingDay = EmployeeWorkingDay.builder()
                    .employee(employee)
                    .employeeWorkingDay(daysOfWeek.get(random.nextInt(daysOfWeek.size())))
                    .build();
            employeeWorkingDayList.add(employeeWorkingDay);
        }
        updateDuplicateWorkingDays(employeeWorkingDayList);

        return employeeWorkingDayList;
    }

    private void updateDuplicateWorkingDays(List<EmployeeWorkingDay> employeeList) {
        Random random = new Random();
        for (int i = 0; i < employeeList.size(); i++) {
            EmployeeWorkingDay employee1 = employeeList.get(i);
            for (int j = i + 1; j < employeeList.size(); j++) {
                EmployeeWorkingDay employee2 = employeeList.get(j);
                if (employee1.getEmployeeWorkingDay().equals(employee2.getEmployeeWorkingDay())) {
                    employee2.setEmployeeWorkingDay(daysOfWeek.get(random.nextInt(daysOfWeek.size())));
                    updateDuplicateWorkingDays(employeeList);
                }
            }
        }
    }



    private List<MatchedEmployee> findMatchingEmployees(List<EmployeeWorkingDay> group1WorkingDays, List<EmployeeWorkingDay> group2WorkingDays) {
        List<MatchedEmployee> result = new ArrayList<>();
        for (EmployeeWorkingDay employee1 : group1WorkingDays) {
            for (EmployeeWorkingDay employee2 : group2WorkingDays) {
                if (employee1.getEmployeeWorkingDay().equals(employee2.getEmployeeWorkingDay())) {
                    result.add(MatchedEmployee.builder()
                            .employee1(employee1.getEmployee())
                            .employee2(employee2.getEmployee())
                            .matchingDay(employee1.getEmployeeWorkingDay())
                            .build());
                }
            }
        }

        return result;
    }

    private boolean checkLastWeeksMatches(List<MatchedEmployee> thisWeekList, List<MatchedEmployee> pastList) {
        for (MatchedEmployee thisWeekEmployee : thisWeekList) {
            for (MatchedEmployee pastEmployee : pastList) {
                if (thisWeekEmployee.getEmployee1().equals(pastEmployee.getEmployee1()) &&
                        thisWeekEmployee.getEmployee2().equals(pastEmployee.getEmployee2())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void writeToExcel(List<MatchedEmployee> matchedEmployeeList, String week) {

        // Varolan bir Excel dosyasını açın veya yeni bir tane oluşturun
        File file = new File("veriler.xlsx");
        Workbook workbook;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                workbook = WorkbookFactory.create(fis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            workbook = new XSSFWorkbook();
        }

// Çalışma kitabı içindeki sayfayı alın veya yeni bir sayfa oluşturun
        Sheet sheet = workbook.getSheet("WorkPlan");
        if (sheet == null) {
            sheet = workbook.createSheet("WorkPlan");
        }

// Verilerinizi ekleyin (mevcut verilere eklemek için var olan satırı seçin)
        int rowIndex = sheet.getLastRowNum() + 1;


        for (MatchedEmployee matchedEmployee : matchedEmployeeList) {
            Row row = sheet.createRow(rowIndex);
            rowIndex++;
            sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(week);
            row.createCell(1).setCellValue(matchedEmployee.getEmployee1().getName());
            row.createCell(2).setCellValue(matchedEmployee.getEmployee2().getName());
            row.createCell(3).setCellValue(matchedEmployee.getMatchingDay());
        }

// Dosyayı kaydedin
        try (FileOutputStream fileOut = new FileOutputStream("veriler.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
