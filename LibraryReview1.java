
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Library Management Application
 * Features:
 * - Add, Delete, View, Borrow, Return Books
 * - Check if book is available
 * - Validate student admission number (GUYYYYNNNNN)
 * - Different fields for books: Forensic, Law, Programming, Maths, Engineering, Nursing, Commerce
 * - Input validations
 * - Persistent file storage (books.txt)
 */
public class LibraryApp {
    private static final String FILE_NAME = "books.txt";
    private static final Scanner sc = new Scanner(System.in);
    private static List<Book> books = new ArrayList<>();
    // Map admission number to borrowed book ID
    private static Map<String, Integer> borrowedMap = new HashMap<>();

    // Base Book class
    static class Book {
        protected int id;
        protected String title;
        protected String author;
        protected boolean isAvailable;
        protected String field;          // Field like Programming, Law, etc.
        protected String borrowedBy;     // Admission number of borrower

        public Book(int id, String title, String author, boolean isAvailable, String field) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.isAvailable = isAvailable;
            this.field = field;
            this.borrowedBy = null;
        }

        // Getters
        public int getId() { return id; }
        public String getTitle() { return title; }
        public boolean isAvailable() { return isAvailable; }
        public String getField() { return field; }
        public String getBorrowedBy() { return borrowedBy; }

        // Setters
        public void setAvailable(boolean available) { this.isAvailable = available; }
        public void setBorrowedBy(String admNo) { this.borrowedBy = admNo; }

        // Convert book data to file string
        public String toFileString() {
            return id + "," + title + "," + author + "," + isAvailable + "," + field + "," + (borrowedBy == null ? "" : borrowedBy);
        }

        @Override
        public String toString() {
            return "[" + id + "] " + title + " by " + author + " - " + field + " - " + (isAvailable ? "Available" : "Borrowed by " + borrowedBy);
        }
    }

    // ProgrammingBook subclass with language info
    static class ProgrammingBook extends Book {
        private String language;

        public ProgrammingBook(int id, String title, String author, boolean isAvailable, String language) {
            super(id, title, author, isAvailable, "Programming");
            this.language = language;
        }

        @Override
        public String toFileString() {
            return id + "," + title + "," + author + "," + isAvailable + "," + language + "," + (borrowedBy == null ? "" : borrowedBy);
        }

        @Override
        public String toString() {
            return super.toString() + " (Language: " + language + ")";
        }
    }

    // Entry point
    public static void main(String[] args) {
        loadBooks();

        while (true) {
            System.out.println("\n=== Library Menu ===");
            System.out.println("1. View All Books");
            System.out.println("2. Add Book");
            System.out.println("3. Delete Book");
            System.out.println("4. Borrow Book");
            System.out.println("5. Return Book");
            System.out.println("6. Search Book by Title");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": viewBooks(); break;
                case "2": addBookMenu(); break;
                case "3": deleteBookMenu(); break;
                case "4": borrowBook(); break;
                case "5": returnBook(); break;
                case "6": searchBookByTitle(); break;
                case "7":
                    saveBooks();
                    System.out.println("Exiting. Thank you!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    /**
     * Loads books from the file. If file doesn't exist, creates sample books.
     */
    private static void loadBooks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            createSampleBooks();
            saveBooks();
        }
        books.clear();
        borrowedMap.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1); // -1 to keep empty trailing
                if (parts.length < 6) continue; // invalid line

                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                String author = parts[2];
                boolean isAvailable = Boolean.parseBoolean(parts[3]);
                String field = parts[4];
                String borrowedBy = parts[5].isEmpty() ? null : parts[5];

                Book b;
                if (field.equalsIgnoreCase("Java") || field.equalsIgnoreCase("Python") || field.equalsIgnoreCase("C")) {
                    b = new ProgrammingBook(id, title, author, isAvailable, field);
                } else if (field.equalsIgnoreCase("Programming")) {
                    // Default to a generic Programming book with no specific language if field is 'Programming'
                    b = new ProgrammingBook(id, title, author, isAvailable, "Unknown");
                } else {
                    b = new Book(id, title, author, isAvailable, field);
                }
                b.setBorrowedBy(borrowedBy);
                books.add(b);

                if (borrowedBy != null && !borrowedBy.isEmpty()) {
                    borrowedMap.put(borrowedBy, id);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Saves current book list to the file.
     */
    private static void saveBooks() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Book b : books) {
                bw.write(b.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    /**
     * Creates sample books for the library.
     */
    private static void createSampleBooks() {
        books.clear();
        books.add(new ProgrammingBook(1, "Let Us C", "Yashavant Kanetkar", true, "C"));
        books.add(new ProgrammingBook(2, "Java: The Complete Reference", "Herbert Schildt", true, "Java"));
        books.add(new ProgrammingBook(3, "Head First Python", "Paul Barry", true, "Python"));
        books.add(new Book(4, "Computer Networking", "Kurose & Ross", true, "Engineering"));
        books.add(new Book(5, "Operating System Concepts", "Silberschatz", true, "Engineering"));
        books.add(new Book(6, "Introduction to Forensic Science", "Saferstein", true, "Forensic"));
        books.add(new Book(7, "Principles of Marketing", "Philip Kotler", true, "Commerce"));
        books.add(new Book(8, "Fundamentals of Nursing", "Barbara Kozier", true, "Nursing"));
        books.add(new Book(9, "Legal Environment of Business", "Bagley", true, "Law"));
        books.add(new Book(10, "Calculus: Early Transcendentals", "James Stewart", true, "Maths"));
    }

    /**
     * Displays all books in the library.
     */
    private static void viewBooks() {
        System.out.println("\n--- Library Books ---");
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        for (Book b : books) {
            System.out.println(b);
        }
    }

    /**
     * Menu to add a new book.
     */
    private static void addBookMenu() {
        try {
            System.out.print("Enter new book ID (number): ");
            int id = Integer.parseInt(sc.nextLine().trim());
            if (findBookById(id) != null) {
                System.out.println("Book ID already exists. Please choose a different ID.");
                return;
            }
            System.out.print("Enter book title: ");
            String title = sc.nextLine().trim();
            System.out.print("Enter author name: ");
            String author = sc.nextLine().trim();

            System.out.println("Select field/category of the book:");
            System.out.println("1. Forensic");
            System.out.println("2. Law");
            System.out.println("3. Programming");
            System.out.println("4. Maths");
            System.out.println("5. Engineering");
            System.out.println("6. Nursing");
            System.out.println("7. Commerce");
            System.out.print("Enter choice (1-7): ");
            String fieldChoice = sc.nextLine().trim();

            String field = null;
            switch (fieldChoice) {
                case "1": field = "Forensic"; break;
                case "2": field = "Law"; break;
                case "3": field = "Programming"; break;
                case "4": field = "Maths"; break;
                case "5": field = "Engineering"; break;
                case "6": field = "Nursing"; break;
                case "7": field = "Commerce"; break;
                default:
                    System.out.println("Invalid field choice.");
                    return;
            }

            Book newBook;
            if (field.equals("Programming")) {
                System.out.print("Enter programming language: ");
                String lang = sc.nextLine().trim();
                newBook = new ProgrammingBook(id, title, author, true, lang);
            } else {
                newBook = new Book(id, title, author, true, field);
            }
            books.add(newBook);
            saveBooks();
            System.out.println("Book added successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. ID must be a number.");
        }
    }

    /**
     * Menu to delete a book.
     */
    private static void deleteBookMenu() {
        try {
            System.out.print("Enter book ID to delete: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            Book b = findBookById(id);
            if (b == null) {
                System.out.println("No book found with that ID.");
                return;
            }
            if (!b.isAvailable()) {
                System.out.println("Cannot delete. Book is currently borrowed.");
                return;
            }
            books.remove(b);
            saveBooks();
            System.out.println("Book deleted successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. ID must be a number.");
        }
    }

    /**
     * Allows student to borrow a book if available.
     */
    private static void borrowBook() {
        System.out.print("Enter your admission number (Format: GUYYYYNNNNN): ");
        String admNo = sc.nextLine().trim();

        // Validate admission number
        if (!Pattern.matches("GU\\d{4}\\d{5}", admNo)) {
            System.out.println("Invalid admission number format. Example: GU202300123");
            return;
        }
        if (borrowedMap.containsKey(admNo)) {
            System.out.println("You have already borrowed a book. Return it before borrowing another.");
            return;
        }

        System.out.print("Enter book ID to borrow: ");
        try {
            int bookId = Integer.parseInt(sc.nextLine().trim());
            Book b = findBookById(bookId);
            if (b == null) {
                System.out.println("No book found with that ID.");
                return;
            }
            if (!b.isAvailable()) {
                System.out.println("Sorry, the book is currently borrowed by someone else.");
                return;
            }
            System.out.print("Enter your field of study (Forensic, Law, Programming, Maths, Engineering, Nursing, Commerce): ");
            String studentField = sc.nextLine().trim();
            // Validate field matches book field for borrowing
            if (!b.getField().equalsIgnoreCase(studentField)) {
                System.out.println("You can only borrow books from your field of study.");
                return;
            }
            b.setAvailable(false);
            b.setBorrowedBy(admNo);
            borrowedMap.put(admNo, bookId);
            saveBooks();
            System.out.println("Book borrowed successfully: " + b.getTitle());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Book ID must be a number.");
        }
    }

    /**
     * Allows student to return a borrowed book.
     */
    private static void returnBook() {
        System.out.print("Enter your admission number (Format: GUYYYYNNNNN): ");
        String admNo = sc.nextLine().trim();

        // Validate admission number format
        if (!Pattern.matches("GU\\d{4}\\d{5}", admNo)) {
            System.out.println("Invalid admission number format. Example: GU202300123");
            return;
        }

        if (!borrowedMap.containsKey(admNo)) {
            System.out.println("You have not borrowed any book.");
            return;
        }

        int bookId = borrowedMap.get(admNo);
        Book borrowedBook = findBookById(bookId);
        if (borrowedBook == null) {
            System.out.println("Borrowed book record not found. Removing your borrow record.");
            borrowedMap.remove(admNo);
            return;
        }

        // Return the book: make it available and clear borrower info
        borrowedBook.setAvailable(true);
        borrowedBook.setBorrowedBy(null);
        borrowedMap.remove(admNo);
        saveBooks();
        System.out.println("You have successfully returned: " + borrowedBook.title);
    }

    /**
     * Search for books by title keyword.
     */
    private static void searchBookByTitle() {
        System.out.print("Enter book title or keyword to search: ");
        String keyword = sc.nextLine().trim().toLowerCase();

        boolean found = false;
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(keyword)) {
                System.out.println(b);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books found with the title containing: " + keyword);
        }
    }

    /**
     * Finds a book by its ID.
     * @param id Book ID
     * @return Book object or null if not found
     */
    private static Book findBookById(int id) {
        for (Book b : books) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }
}
