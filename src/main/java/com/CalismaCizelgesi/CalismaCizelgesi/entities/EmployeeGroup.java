package com.CalismaCizelgesi.CalismaCizelgesi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "EMPLOYEE_GROUP")
public class EmployeeGroup {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "GROUP_NAME")
    private String groupName;
    @Column(name = "EMPLOYEE_LIST")
    @JsonIgnore
    @OneToMany(mappedBy = "employeeGroup")
    private List<Employee> employeeList;

}
