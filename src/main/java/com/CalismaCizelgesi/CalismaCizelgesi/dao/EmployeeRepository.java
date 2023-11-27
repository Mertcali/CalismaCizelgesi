package com.CalismaCizelgesi.CalismaCizelgesi.dao;

import com.CalismaCizelgesi.CalismaCizelgesi.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    @Query(nativeQuery = true,value = "select * from employee e where e.group_id = :id")
    List<Employee> findByGroupId(Long id);
}
