package model;

public class Admin extends User {

    public Admin(String name, int ID){
        super(name, ID);
    }


    public void showMenu() {
        System.out.println("\n--- Admin Menu ---");
        System.out.println("1. Add a new coworking space");
        System.out.println("2. Remove a coworking space");
        System.out.println("3. View all reservations");
        System.out.println("4. Exit to main menu");
    }
}
