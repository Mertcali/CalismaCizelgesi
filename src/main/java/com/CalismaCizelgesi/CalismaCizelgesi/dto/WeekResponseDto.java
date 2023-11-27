package com.CalismaCizelgesi.CalismaCizelgesi.dto;

import com.CalismaCizelgesi.CalismaCizelgesi.entities.EmployeeWorkingDay;
import com.CalismaCizelgesi.CalismaCizelgesi.entities.MatchedEmployee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeekResponseDto {
    private List<EmployeeWorkingDay> employeeWorkingDayListGroup1;
    private List<EmployeeWorkingDay> employeeWorkingDayListGroup2;
    private List<MatchedEmployee> matchedEmployeeList;
    private String week;
}
