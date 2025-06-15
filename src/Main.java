import config.DataLoader;
import model.Admin;
import model.Customer;
import service.ReservationService;
import service.WorkspaceService;
import ui.AdminUI;
import ui.CustomerUI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        WorkspaceService workspaceService = new WorkspaceService();
        ReservationService reservationService = new ReservationService(workspaceService);

        boolean loaded = DataLoader.loadData(workspaceService, reservationService);
        if (!loaded) {
            System.out.println("Failed to load data. Application will start with no data.");
        }

        Admin admin = new Admin("AdminUser", 1);
        Customer customer = new Customer("CustomerUser", 2);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nWelcome to the Coworking Reservation System!");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as Customer");
            System.out.println("3. Exit");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                AdminUI adminUI = new AdminUI(workspaceService, reservationService, admin);
                adminUI.showMenu();
            } else if (choice.equals("2")) {
                CustomerUI customerUI = new CustomerUI(workspaceService, reservationService, customer);
                customerUI.showMenu();
            } else if (choice.equals("3")) {
                System.out.println("Exiting the system. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
    }
}
