package com.mysuperproject.atelier.service;

import com.mysuperproject.atelier.entity.Employee;
import com.mysuperproject.atelier.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee create(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void update(Employee employee) {
        employeeRepository.update(employee);
    }

    public boolean delete(Integer id) {
        return employeeRepository.delete(id);
    }

    public Optional<Employee> getById(Integer id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }
}
