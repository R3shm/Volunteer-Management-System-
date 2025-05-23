// Volunteer Management System - All-in-One Java Program Using OOP Concepts

import java.util.*;

class User { String username, password, role;

User(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.role = role;
}

boolean authenticate(String user, String pass) {
    return username.equals(user) && password.equals(pass);
}

}

class Log { Date time; String message;

Log(String message) {
    this.time = new Date();
    this.message = message;
}

void displayLog() {
    System.out.println(time + " - " + message);
}

}

class EntryExitLog { static List<Log> logs = new ArrayList<>();

static void logEntry(String user) {
    logs.add(new Log(user + " logged in."));
}

static void logExit(String user) {
    logs.add(new Log(user + " logged out."));
}

static void showLogs() {
    for (Log log : logs) {
        log.displayLog();
    }
}

}

class Event { String title, date; List<Volunteer> participants = new ArrayList<>();

Event(String title, String date) {
    this.title = title;
    this.date = date;
}

void addParticipant(Volunteer v) {
    participants.add(v);
    v.eventHistory.add(this);
}

void showEventDetails() {
    System.out.println("Event: " + title + " | Date: " + date);
    System.out.println("Participants:");
    for (Volunteer v : participants) {
        System.out.println("- " + v.name);
    }
}

}

class Volunteer { String name, id; List<Event> eventHistory = new ArrayList<>(); List<String> tasks = new ArrayList<>(); List<String> feedbacks = new ArrayList<>();

Volunteer(String id, String name) {
    this.id = id;
    this.name = name;
}

void viewProfile() {
    System.out.println("Volunteer ID: " + id);
    System.out.println("Name: " + name);
}

void viewEventHistory() {
    System.out.println("Event History for " + name + ":");
    for (Event e : eventHistory) {
        System.out.println("- " + e.title + " on " + e.date);
    }
}

void viewTasks() {
    System.out.println("Tasks for " + name + ":");
    for (String task : tasks) {
        System.out.println("- " + task);
    }
}

void giveFeedback(String feedback) {
    feedbacks.add(feedback);
    System.out.println("Thank you for your feedback!");
}

}

class Admin extends User { static List<Volunteer> volunteers = new ArrayList<>(); static List<User> users = new ArrayList<>(); static List<Event> events = new ArrayList<>();

Admin(String u, String p) {
    super(u, p, "admin");
}

void registerVolunteer(String id, String name) {
    Volunteer v = new Volunteer(id, name);
    volunteers.add(v);
    System.out.println("Volunteer registered: " + name);
}

void createEvent(String title, String date) {
    events.add(new Event(title, date));
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

void generateReport() {
    System.out.println("--- System Report ---");
    System.out.println("Total Volunteers: " + volunteers.size());
    System.out.println("Total Events: " + events.size());
    System.out.println("System Logs:");
    EntryExitLog.showLogs();
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

}

public class VolunteerManagementSystem { static Scanner scanner = new Scanner(System.in); static Admin defaultAdmin = new Admin("admin", "1234");

public static void main(String[] args) {
    Admin.users.add(defaultAdmin);

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

        EntryExitLog.logEntry(loginUser.username);

        if (loginUser.role.equals("admin")) {
            adminMenu((Admin) loginUser);
        }

        EntryExitLog.logExit(loginUser.username);
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
                admin.registerVolunteer(id, name);
                break;
            case 2:
                admin.viewAllVolunteers();
                break;
            case 3:
                System.out.print("Enter Event Title: ");
                scanner.nextLine(); // consume newline
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

}

