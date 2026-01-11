package com.giorgi;

import com.giorgi.entity.Admin;
import com.giorgi.entity.Customer;
import com.giorgi.repository.CustomerRepository;
import com.giorgi.repository.UserRepository;
import com.giorgi.service.ReservationService;
import com.giorgi.service.WorkspaceService;
import com.giorgi.ui.AdminUI;
import com.giorgi.ui.CustomerUI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import java.util.Scanner;

@SpringBootApplication
public class CoworkingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoworkingApplication.class, args);
    }

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    @Transactional
    public CommandLineRunner init(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            WorkspaceService workspaceService,
            ReservationService reservationService,
            AdminUI adminUI,
            CustomerUI customerUI,
            Scanner scanner) {

        return args -> {
            Admin admin = userRepository.findAdminByName("AdminUser")
                    .orElseGet(() -> userRepository.save(new Admin("AdminUser")));

            Customer customer = customerRepository.findByName("CustomerUser")
                    .orElseGet(() -> customerRepository.save(new Customer("CustomerUser")));

            customerUI.setCustomerId(customer.getId());

            startUserInterface(adminUI, customerUI, scanner);
        };
    }

    private void startUserInterface(
            AdminUI adminUI,
            CustomerUI customerUI,
            Scanner scanner) {

        while (true) {
            System.out.println("\nWelcome to the Coworking Reservation System!");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as Customer");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    adminUI.showMenu();
                    break;
                case "2":
                    customerUI.showMenu();
                    break;
                case "3":
                    System.out.println("Exiting the system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}