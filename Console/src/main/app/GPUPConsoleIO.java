package main.app;

import java.util.InputMismatchException;
import java.util.Scanner;

public class GPUPConsoleIO {

    public static void welcome() {
        System.out.println("Hello and welcome to G.P.U.P - Generic system for streamlining and improving processes");
    }

    public static UserInput mainMenu() {
        int input;
        System.out.println("\n=========== G.P.U.P System ===========");
        System.out.println(" 1. Load GPUP system file\n" +
                " 2. Display Target-Graph information\n" +
                " 3. Display target information\n" +
                " 4. Find dependency between 2 targets\n" +
                " 5. Run a task\n" +
                " 6. Find Circuit\n" +
                " 7. Exit\n");
        System.out.println("(-- At each stage of the program, pressing '0' will return you to the main menu --)");
        System.out.print("Please choose an option: ");
        input = getIntegerInRange(1, 7);
        return UserInput.values()[input];
    }

    private static int getIntegerInRange(int startRange, int endRange) {
        boolean validRange = false;
        int input;
        do {
            input = getIntegerInput();
            if (input >= startRange && input <= endRange) {
                validRange = true;
            } else {
                System.out.println("Wrong input - you entered a invalid choice, try again: ");
            }
        } while (!validRange);

        return input;
    }

    public static int getIntegerInput() {
        Scanner scanner = new Scanner(System.in);
        int value = 0;
        boolean validInput;

        do {
            try {
                value = scanner.nextInt();
                if (value == 0) { // quit
                    break;
                }
                validInput = true;
            } catch (InputMismatchException ex) {
                System.out.println("Wrong input - this is not a number, try again:");
                validInput = false;
                scanner.nextLine();
            }
        } while (!validInput);

        return value;
    }

    public static String getXmlPath() {
        String path;
        System.out.println("Enter the full path of the XML file you want to load:");
        path = getStringInput("path"); // return a valid path, yet no promise of existing file
        if (path.matches(".*[א-ת]+.*")) {
            return null;
        }
        if (path.equals("0")) {
            return UserInput.QUIT.toString();
        }
        return path;
    }

    public static String getStringInput(String msg) {
        Scanner scanner = new Scanner(System.in);
        boolean valid;
        String input = null;
        do {
            try {
                input = scanner.nextLine();
                valid = true;
                if(input.equals("0")){
                    input = UserInput.QUIT.toString();
                }
            } catch (Exception ex) {
                System.out.println("Wrong input - this is not a valid " + msg + ", try again.");
                valid = false;
                scanner.nextLine();
            }
        } while (!valid);

        return input;
    }

    public static void failedLoadSystem(String s) {
        System.out.println("Loading the file ended with a failure:");
        System.out.println(s);
    }

    public static void successLoading() {
        System.out.println("The File loaded successfully!");
    }

    public static void targetNameRequest() {
        System.out.println("Please enter a target's name: (to return to the main menu, press '0')");
    }

    public static void printMsg(String s) {
        System.out.println(s);
    }

    public static void unloadedSystem() {
        System.out.println("The GPUP System is not loaded yet, please load a Xml file first.");
    }

    public static int getProcessingTime() {
        int processingTime;
        System.out.println("Please Enter The Task Processing Time (for single target) in ms : ");
        processingTime = getIntegerInput();
        return processingTime;
    }

    public static int getTwoOptionsResult(String msg, String firstOpt, String secondOpt) {
        System.out.println(msg);
        System.out.println("  1. " + firstOpt);
        System.out.println("  2. " + secondOpt);
        return getIntegerInRange(1, 2);
    }

    public static float getFloatInRange(int startRange, int endRange) {
        Scanner scanner = new Scanner(System.in);
        float value = 0;
        boolean validInput = false;

        do {
            System.out.print("Enter a float number between " + startRange + " to " + endRange + " (included):");
            try {
                value = scanner.nextFloat();
                if (value >= startRange && value <= endRange) {
                    validInput = true;
                } else {
                    System.out.println("The number is out of range");
                }
            } catch (InputMismatchException ex) {
                System.out.println("Wrong input - this is not a float number, try again.");
                scanner.nextLine();
            }
        } while (!validInput);

        return value;
    }

    public static float getProbability(String msg) {
        System.out.println(msg);
        return getFloatInRange(0, 1);
    }

    public static void continueApp() {
        System.out.print("\nPress ENTER to continue ...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public static boolean isQuit(String name) {
        if (name.equals(UserInput.QUIT.toString())) {
            GPUPConsoleIO.printMsg("Going back to main menu.");
            return true;
        }
        return false;
    }

    public static UserInput exit() {
        System.out.println("The System is about the end");
        do {
            System.out.println("Are you sure you want to exit? (Y/N)");
            String answer = getStringInput("answer");
            switch (answer) {
                case "Y":
                    return UserInput.QUIT;
                case "N":
                    return UserInput.INIT;
                default:
                    System.out.println("This is not a valid answer, try again");
            }
        } while (true);
    }
}
