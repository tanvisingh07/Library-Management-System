import java.io.*;
import java.util.*;

// Base class for books
class Book {
    protected int id;
    protected String title;
    protected String author;
    protected boolean isAvailable;

    public Book(int id, String title, String author, boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public String toFileString() {
        return id + "," + title + "," + author + "," + isAvailable + ",General";
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " by " + author + " - " + (isAvailable ? "Available" : "Borrowed");
    }
}

// Inherited class for programming books
class ProgrammingBook extends Book {
    private String language;

    public ProgrammingBook(int id, String title, String author, boolean isAvailable, String language) {
        super(id, title, author, isAvailable);
        this.language = language;
    }

    @Override
    public String toFileString() {
        return id + "," + title + "," + author + "," + isAvailable + "," + language;
    }

    @Override
    public String toString() {
        return super.toString() + " (Programming - " + language + ")";
    }
}

// Main class
public class LibraryReview1 {
    private static final String FILE_NAME = "books.txt";
    private static List<Book> books = new ArrayList<>();

    public static void main(String[] args) {
        loadBooks();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Library Menu ===");
            System.out.println("1. View Books");
            System.out.println("2. Exit");
            System.out.print("Enter your choice: ");
            String input = sc.nextLine();

            if (input.equals("1")) {
                books.forEach(System.out::println);
            } else if (input.equals("2")) {
                saveBooks();
                System.out.println("Exiting. Thank you!");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Load or initialize books
    private static void loadBooks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            createSampleBooks();
            saveBooks();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                String author = parts[2];
                boolean isAvailable = Boolean.parseBoolean(parts[3]);
                String genre = parts[4];

                if (genre.equalsIgnoreCase("Java") || genre.equalsIgnoreCase("Python") || genre.equalsIgnoreCase("C")) {
                    books.add(new ProgrammingBook(id, title, author, isAvailable, genre));
                } else {
                    books.add(new Book(id, title, author, isAvailable));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Save books to file
    private static void saveBooks() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Book book : books) {
                bw.write(book.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // Sample books
    private static void createSampleBooks() {
        books.add(new ProgrammingBook(1, "Let Us C", "Yashavant Kanetkar", true, "C"));
        books.add(new ProgrammingBook(2, "Java: The Complete Reference", "Herbert Schildt", true, "Java"));
        books.add(new ProgrammingBook(3, "Head First Python", "Paul Barry", true, "Python"));
        books.add(new Book(4, "Computer Networking", "Kurose & Ross", true));
        books.add(new Book(5, "Operating System Concepts", "Silberschatz", true));
    }
}
