package main;

import dao.OrderProcessor;
import dao.OrderManagementRepository;
import entity.Product;
import entity.User;
import exception.InvalidProductException;
import exception.OrderNotFoundException;
import exception.UserNotFoundException;

import java.util.*;

public class mainModule {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        OrderManagementRepository repo = new OrderProcessor();

        while (true) {
            System.out.println("\n**** Order Management System ****");
            System.out.println("1. Create User");
            System.out.println("2. Create Product");
            System.out.println("3. Create Order");
            System.out.println("4. Cancel Order");
            System.out.println("5. Get All Products");
            System.out.println("6. Get Orders By User");
            System.out.println("7. Get Total Amount Spent By User");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();
            try {
                switch (choice) {
                    case 1: {
                        System.out.print("Enter userId: ");
                        int userId = sc.nextInt(); sc.nextLine();
                        System.out.print("Enter username: ");
                        String username = sc.nextLine();
                        System.out.print("Enter password: ");
                        String password = sc.nextLine();
                        System.out.print("Enter role (Admin/User): ");
                        String role = sc.nextLine();
                        repo.createUser(new User(userId, username, password, role));
                        System.out.println("User created successfully.");
                        break;
                    }
                    case 2: {
                        System.out.print("Enter Admin userId: ");
                        int userId = sc.nextInt(); sc.nextLine();
                        System.out.print("Enter role: ");
                        String role = sc.nextLine();
                        User admin = new User(userId, null, null, role);

                        System.out.print("Enter productId: ");
                        int pid = sc.nextInt(); sc.nextLine();
                        System.out.print("Enter productName: ");
                        String pname = sc.nextLine();
                        System.out.print("Enter description: ");
                        String desc = sc.nextLine();
                        System.out.print("Enter price: ");
                        double price = sc.nextDouble();
                        System.out.print("Enter quantity: ");
                        int qty = sc.nextInt(); sc.nextLine();
                        System.out.print("Enter type (Electronics/Clothing): ");
                        String type = sc.nextLine();

                        Product product = new Product(pid, pname, desc, price, qty, type);
                        repo.createProduct(admin, product);
                        System.out.println("Product created successfully.");
                        break;
                    }
                    case 3: {
                        System.out.print("Enter userId: ");
                        int uid = sc.nextInt();
                        sc.nextLine();
                        User user = new User(uid, null, null, null);

                        List<Product> orderList = new ArrayList<>();
                        while (true) {
                            System.out.print("Enter productId to add to order (or 0 to finish): ");
                            int pid = sc.nextInt();
                            sc.nextLine();
                            if (pid == 0) break;
                            orderList.add(new Product(pid, null, null, 0, 0, null));
                        }
                        repo.createOrder(user, orderList);
                        System.out.println("Order created successfully.");
                        break;
                    }
                    case 4: {
                        System.out.print("Enter userId: ");
                        int uid = sc.nextInt();
                        System.out.print("Enter orderId: ");
                        int oid = sc.nextInt();
                        repo.cancelOrder(uid, oid);
                        System.out.println("Order cancelled successfully.");
                        break;
                    }
                    case 5: {
                        List<Product> products = repo.getAllProducts();
                        for (Product p : products) {
                            System.out.println(p);
                        }
                        break;
                    }
                    case 6: {
                        System.out.print("Enter userId: ");
                        int uid = sc.nextInt();
                        User user = new User(uid, null, null, null);
                        List<Product> ordered = repo.getOrderByUser(user);
                        for (Product p : ordered) {
                            System.out.println(p);
                        }
                        break;
                    }
                    case 7: {
                        System.out.print("Enter userId: ");
                        int uid = sc.nextInt();
                        User user = new User(uid, null, null, null);
                        double total = repo.getTotalAmountSpentByUser(user);
                        System.out.println("Total Amount Spent: Rs." + total);
                        break;
                    }
                    case 8:
                        System.out.println("Exiting... Bye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (InvalidProductException | UserNotFoundException | OrderNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected Error: " + e.getMessage());
            }
        }
    }
}
