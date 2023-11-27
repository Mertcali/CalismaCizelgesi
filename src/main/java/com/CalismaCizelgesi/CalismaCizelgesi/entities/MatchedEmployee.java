package com.CalismaCizelgesi.CalismaCizelgesi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchedEmployee {

    private Employee employee1;
    private Employee employee2;
    private String matchingDay;

}
