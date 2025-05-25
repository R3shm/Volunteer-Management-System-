import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

class User {
    String username, passwordHash, role;

    User(String username, String password, String role) {
        this.username = username;
        this.passwordHash = hashPassword(password);
        this.role = role;
    }

    boolean authenticate(String user, String pass) {
        return username.equals(user) && passwordHash.equals(hashPassword(pass));
    }

    String hashPassword(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(pass.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

class Volunteer extends User {
    String name, id;
    List<String> tasks = new ArrayList<>();
    List<String> eventHistory = new ArrayList<>();

    Volunteer(String id, String name, String username, String password) {
        super(username, password, "volunteer");
        this.id = id;
        this.name = name;
    }

    void viewProfile() {
        System.out.println("Volunteer ID: " + id);
        System.out.println("Name: " + name);
    }

    void viewTasks() {
        System.out.println("Tasks for " + name + ":");
        for (String task : tasks) System.out.println("- " + task);
    }

    void viewEventHistory() {
        System.out.println("Event History for " + name + ":");
        for (String e : eventHistory) System.out.println("- " + e);
    }

    void giveFeedback(String feedback) {
        try (FileWriter fw = new FileWriter("feedback.txt", true)) {
            fw.write(name + ": " + feedback + "\n");
            System.out.println("Thank you for your feedback!");
        } catch (IOException e) {
            System.out.println("Error saving feedback: " + e);
        }
    }

    void saveToFile() {
        try (FileWriter fw = new FileWriter("volunteers.txt", true)) {
            fw.write(id + "," + name + "," + username + "," + passwordHash + "\n");
        } catch (IOException e) {
            System.out.println("Error saving volunteer: " + e);
        }
    }
}

class Event {
    String title, date;
    List<String> participants = new ArrayList<>();

    Event(String title, String date) {
        this.title = title;
        this.date = date;
    }

    void addParticipant(Volunteer v) {
        participants.add(v.name);
        v.eventHistory.add(title);
    }

    void showEventDetails() {
        System.out.println("Event: " + title + " | Date: " + date);
        System.out.println("Participants:");
        for (String p : participants) System.out.println("- " + p);
    }

    void saveToFile() {
        try (FileWriter fw = new FileWriter("events.txt", true)) {
            fw.write(title + "," + date + "\n");
        } catch (IOException e) {
            System.out.println("Error saving event: " + e);
        }
    }
}

class Admin extends User {
    static List<Volunteer> volunteers = new ArrayList<>();
    static List<Event> events = new ArrayList<>();
    static List<User> users = new ArrayList<>();

    Admin(String u, String p) {
        super(u, p, "admin");
    }

    void registerVolunteer(String id, String name, String username, String password) {
        Volunteer v = new Volunteer(id, name, username, password);
        volunteers.add(v);
        users.add(v);
        v.saveToFile();
        System.out.println("Volunteer registered: " + name);
    }

    void createEvent(String title, String date) {
        Event e = new Event(title, date);
        events.add(e);
        e.saveToFile();
        System.out.println("Event created: " + title);
    }

    void assignTask(String volunteerId, String task) {
        for (Volunteer v : volunteers) {
            if (v.id.equals(volunteerId)) {
                v.tasks.add(task);
                System.out.println("Task assigned to " + v.name);
                return;
            }
        }
        System.out.println("Volunteer not found.");
    }

    void viewAllVolunteers() {
        for (Volunteer v : volunteers) {
            v.viewProfile();
        }
    }

    void viewAllEvents() {
        for (Event e : events) {
            e.showEventDetails();
        }
    }

    void addVolunteerToEvent(String volunteerId, String eventTitle) {
        Volunteer foundVolunteer = null;
        Event foundEvent = null;

        for (Volunteer v : volunteers) {
            if (v.id.equals(volunteerId)) {
                foundVolunteer = v;
                break;
            }
        }
        for (Event e : events) {
            if (e.title.equals(eventTitle)) {
                foundEvent = e;
                break;
            }
        }

        if (foundVolunteer != null && foundEvent != null) {
            foundEvent.addParticipant(foundVolunteer);
            System.out.println("Volunteer added to event successfully.");
        } else {
            System.out.println("Volunteer or event not found.");
        }
    }

    void generateReport() {
        System.out.println("--- System Report ---");
        System.out.println("Total Volunteers: " + volunteers.size());
        System.out.println("Total Events: " + events.size());
        System.out.println("Volunteers:");
        for (Volunteer v : volunteers) System.out.println("- " + v.name);
        System.out.println("Events:");
        for (Event e : events) System.out.println("- " + e.title);
    }
}

public class VolunteerManagementSystem {
    static Scanner scanner = new Scanner(System.in);
    static Admin defaultAdmin = new Admin("admin", "1234");

    public static void main(String[] args) {
        Admin.users.add(defaultAdmin);
        loadVolunteersFromFile();
        loadEventsFromFile();

        while (true) {
            System.out.println("\n--- Volunteer Management System ---");
            System.out.print("Enter username: ");
            String user = scanner.next();
            System.out.print("Enter password: ");
            String pass = scanner.next();

            User loginUser = null;
            for (User u : Admin.users) {
                if (u.authenticate(user, pass)) {
                    loginUser = u;
                    break;
                }
            }

            if (loginUser == null) {
                System.out.println("Invalid credentials.");
                continue;
            }

            System.out.println("Welcome, " + loginUser.username + "!");
            if (loginUser.role.equals("admin")) {
                adminMenu((Admin) loginUser);
            } else if (loginUser.role.equals("volunteer")) {
                volunteerMenu((Volunteer) loginUser);
            }

            System.out.println("Logged out successfully.");
        }
    }

    static void adminMenu(Admin admin) {
        while (true) {
            System.out.println("\nAdmin Dashboard:");
            System.out.println("1. Register Volunteer");
            System.out.println("2. View All Volunteers");
            System.out.println("3. Create Event");
            System.out.println("4. View All Events");
            System.out.println("5. Assign Task to Volunteer");
            System.out.println("6. Add Volunteer to Event");
            System.out.println("7. Generate Report");
            System.out.println("8. Logout");
            System.out.print("Select: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter Volunteer ID: ");
                    String id = scanner.next();
                    System.out.print("Enter Volunteer Name: ");
                    String name = scanner.next();
                    System.out.print("Set Username: ");
                    String uname = scanner.next();
                    System.out.print("Set Password: ");
                    String pass = scanner.next();
                    admin.registerVolunteer(id, name, uname, pass);
                    break;
                case 2:
                    admin.viewAllVolunteers();
                    break;
                case 3:
                    System.out.print("Enter Event Title: ");
                    scanner.nextLine();
                    String title = scanner.nextLine();
                    System.out.print("Enter Event Date: ");
                    String date = scanner.next();
                    admin.createEvent(title, date);
                    break;
                case 4:
                    admin.viewAllEvents();
                    break;
                case 5:
                    System.out.print("Enter Volunteer ID: ");
                    String vid = scanner.next();
                    System.out.print("Enter Task: ");
                    scanner.nextLine();
                    String task = scanner.nextLine();
                    admin.assignTask(vid, task);
                    break;
                case 6:
                    System.out.print("Enter Volunteer ID: ");
                    String vID = scanner.next();
                    System.out.print("Enter Event Title: ");
                    scanner.nextLine();
                    String eTitle = scanner.nextLine();
                    admin.addVolunteerToEvent(vID, eTitle);
                    break;
                case 7:
                    admin.generateReport();
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    static void volunteerMenu(Volunteer v) {
        while (true) {
            System.out.println("\nVolunteer Dashboard:");
            System.out.println("1. View Profile");
            System.out.println("2. View Tasks");
            System.out.println("3. View Event History");
            System.out.println("4. Give Feedback");
            System.out.println("5. Logout");
            System.out.print("Select: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    v.viewProfile();
                    break;
                case 2:
                    v.viewTasks();
                    break;
                case 3:
                    v.viewEventHistory();
                    break;
                case 4:
                    System.out.print("Enter feedback: ");
                    scanner.nextLine();
                    String feedback = scanner.nextLine();
                    v.giveFeedback(feedback);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    static void loadVolunteersFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("volunteers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    Volunteer v = new Volunteer(data[0], data[1], data[2], "temp");
                    v.passwordHash = data[3];
                    Admin.volunteers.add(v);
                    Admin.users.add(v);
                }
            }
        } catch (IOException e) {
            System.out.println("volunteers.txt not found.");
        }
    }

    static void loadEventsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("events.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    Event e = new Event(data[0], data[1]);
                    Admin.events.add(e);
                }
            }
        } catch (IOException e) {
            System.out.println("events.txt not found.");
        }
    }
}