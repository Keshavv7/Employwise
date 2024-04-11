package com.employwise.service;

import com.employwise.model.Employee;
import com.employwise.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmailService emailService;

    public Employee addEmployee(Employee employee) {
        
        Employee savedEmployee = employeeRepository.save(employee);

        // Send email to Level 1 manager
        Optional<Employee> level1ManagerOptional = employeeRepository.findById(savedEmployee.getReportsTo());
        level1ManagerOptional.ifPresent(level1Manager ->
                emailService.sendEmail(
                        level1Manager.getEmail(),
                        "New Employee Addition",
                        savedEmployee.getEmployeeName() + " has been added under your supervision."
                                + "\nMobile number is " + savedEmployee.getPhoneNumber()
                                + " and email is " + savedEmployee.getEmail()
                )
        );

        return savedEmployee;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(String id) {
        return employeeRepository.findById(id);
    }

    public void deleteEmployee(String id) {
        employeeRepository.deleteById(id);
    }

    public Employee updateEmployee(String id, Employee employee) {
        employee.setId(id);
        return employeeRepository.save(employee);
    }

    public Optional<Employee> getNthLevelManager(String employeeId, int level) {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);

        if (employeeOptional.isPresent()) {
            Employee currentEmployee = employeeOptional.get();
            String reportsTo = currentEmployee.getReportsTo();

            for (int i = 0; i < level; i++) {
                if (reportsTo == null || reportsTo.isEmpty()) {
                    return Optional.empty();
                }

                Optional<Employee> managerOptional = employeeRepository.findById(reportsTo);
                if (managerOptional.isPresent()) {
                    currentEmployee = managerOptional.get();
                    reportsTo = currentEmployee.getReportsTo();
                } else {
                    return Optional.empty();
                }
            }

            return Optional.of(currentEmployee);
        }

        return Optional.empty();
    }

    public Page<Employee> getAllEmployeesWithPaginationAndSorting(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return employeeRepository.findAll(pageable);
    }

    public sendEmailToLevel1Manager(Employee employee) {
        if (employee.reportsTo) {
            Optional<Employee> managerOptional = employeeRepository.findById(employee.reportsTo)
            if (managerOptional.isPresent() && managerOptional.get().reportsTo == null) {
                String managerEmail = managerOptional.get().email
                String subject = "New Employee Addition"
                String text = "${employee.employeeName} will now work under you. Mobile number is ${employee.phoneNumber} and email is ${employee.email}"
                
                SimpleMailMessage message = new SimpleMailMessage()
                message.setTo(managerEmail)
                message.setSubject(subject)
                message.setText(text)

                javaMailSender.send(message)
            }
        }
    }
}
