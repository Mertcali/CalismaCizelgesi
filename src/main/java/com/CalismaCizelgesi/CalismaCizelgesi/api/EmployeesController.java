package com.CalismaCizelgesi.CalismaCizelgesi.api;

import com.CalismaCizelgesi.CalismaCizelgesi.dto.WeekResponseDto;
import com.CalismaCizelgesi.CalismaCizelgesi.service.ResultManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeesController {
    @Autowired
    private ResultManager resultManager;

    @GetMapping("/getFirstWeek")
    public List<WeekResponseDto> getFirstWeek(){
        return this.resultManager.calculateFirstWeeksWorkingDays();
    }

}
