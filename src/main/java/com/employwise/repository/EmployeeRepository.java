package com.employwise.repository;

import com.employwise.model.Employee;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

public interface EmployeeRepository extends CouchbaseRepository<Employee, String> {
}
