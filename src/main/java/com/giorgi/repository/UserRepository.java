package com.giorgi.repository;

import com.giorgi.entity.Admin;
import com.giorgi.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Admin, Integer> {

    @Query("SELECT a FROM Admin a WHERE a.name = :name")
    Optional<Admin> findAdminByName(@Param("name") String name);

    @Query("SELECT c FROM Customer c WHERE c.name = :name")
    Optional<Customer> findCustomerByName(@Param("name") String name);

}