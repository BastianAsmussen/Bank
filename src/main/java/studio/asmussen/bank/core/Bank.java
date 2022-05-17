package studio.asmussen.bank.core;

import studio.asmussen.bank.sql.MySQL;

import java.text.DecimalFormat;
import java.util.Scanner;

public class Bank {

    public static void main(String[] args) {

        MySQL sql = new MySQL();

        sql.connect();

        Security security = new Security();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the bank!");
        System.out.println();

        System.out.print("Log in or sign up? (l/s): ");
        boolean logIn = scanner.nextLine().equalsIgnoreCase("l");

        boolean validInput = false;

        if (logIn) {

            do {

                System.out.print("Please enter your email: ");
                String email = scanner.nextLine();

                if (!security.validateEmail(email)) {

                    System.out.println("Invalid email, please try again!");

                    continue;
                }

                System.out.print("Please enter your password: ");
                String password = scanner.nextLine();

                if (!security.validatePassword(password)) {

                    System.out.println("Invalid password, please try again!");

                    continue;
                }

                if (sql.logIn(email, password)) {

                    validInput = true;

                } else {

                    System.out.println("Invalid email or password, please try again!");
                }

            } while (!validInput);

            System.out.println("Welcome " + sql.firstName + " " + sql.lastName + "!");

        } else {

            do {

                System.out.print("Please enter your first name: ");
                String firstName = scanner.nextLine();

                System.out.print("Please enter your last name: ");
                String lastName = scanner.nextLine();

                System.out.print("Please enter your email: ");
                String email = scanner.nextLine();

                if (!security.validateEmail(email)) {

                    System.out.println("Invalid email, please try again!");

                    continue;
                }

                System.out.print("Please enter your password: ");
                String password = scanner.nextLine();

                if (!security.validatePassword(password)) {

                    System.out.println("Invalid password, please try again!");

                    continue;
                }

                if (!sql.signUp(firstName, lastName, email, password, 0.0)) {

                    System.out.println("Email already exists, please try again!");

                } else {

                    validInput = true;
                }

            } while (!validInput);

            System.out.println("Welcome " + sql.firstName + " " + sql.lastName + "!");
        }

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.##");

        System.out.println("Current balance: $" + decimalFormat.format(sql.balance));

        sql.disconnect();
    }
}
