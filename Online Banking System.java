import java.io.Console;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Online Banking System
 * A comprehensive banking application with user and admin functionality
 */
public class OnlineBankingSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static UserManager userManager = new UserManager();
    private static AccountManager accountManager = new AccountManager();
    private static TransactionManager transactionManager = new TransactionManager();
    private static User currentUser = null;
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("$#,##0.00");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        // Initialize sample data
        initializeSampleData();

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                displayLoginMenu();
            } else if (currentUser.getRole() == UserRole.ADMIN) {
                displayAdminMenu();
            } else {
                displayCustomerMenu();
            }
        }
    }

    private static void initializeSampleData() {
        // Create admin user
        User admin = new User("admin", "admin123", "Admin", "User", "admin@example.com", UserRole.ADMIN);
        userManager.addUser(admin);

        // Create regular users
        User user1 = new User("user1", "password", "John", "Doe", "john@example.com", UserRole.CUSTOMER);
        User user2 = new User("user2", "password", "Jane", "Smith", "jane@example.com", UserRole.CUSTOMER);
        userManager.addUser(user1);
        userManager.addUser(user2);

        // Create accounts
        Account checking1 = new Account("CHK-001", "Checking Account", user1.getUsername(), 2500.0);
        Account savings1 = new Account("SAV-001", "Savings Account", user1.getUsername(), 10000.0);
        Account checking2 = new Account("CHK-002", "Checking Account", user2.getUsername(), 3500.0);
        Account savings2 = new Account("SAV-002", "Savings Account", user2.getUsername(), 15000.0);
        
        accountManager.addAccount(checking1);
        accountManager.addAccount(savings1);
        accountManager.addAccount(checking2);
        accountManager.addAccount(savings2);

        // Create some sample transactions
        transactionManager.addTransaction(new Transaction("TRX-001", "CHK-001", "SAV-001", 500.0, "Transfer to savings", new Date()));
        transactionManager.addTransaction(new Transaction("TRX-002", "CHK-002", "SAV-002", 1000.0, "Transfer to savings", new Date()));
        transactionManager.addTransaction(new Transaction("TRX-003", "CHK-001", "CHK-002", 250.0, "Payment for dinner", new Date()));
        
        // Add some older transactions
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -5);
        transactionManager.addTransaction(new Transaction("TRX-004", "SAV-001", "CHK-001", 300.0, "Transfer to checking", cal.getTime()));
        
        cal.add(Calendar.DAY_OF_MONTH, -10);
        transactionManager.addTransaction(new Transaction("TRX-005", "CHK-002", "CHK-001", 125.0, "Split bill payment", cal.getTime()));
    }

    // ==================== MENU METHODS ====================

    private static void displayLoginMenu() {
        System.out.println("\n===== SECURE BANK SYSTEM =====");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                System.out.println("Thank you for using Secure Bank. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void displayCustomerMenu() {
        System.out.println("\n===== CUSTOMER DASHBOARD =====");
        System.out.println("Welcome, " + currentUser.getFirstName() + " " + currentUser.getLastName() + "!");
        System.out.println("1. View Accounts");
        System.out.println("2. View Transaction History");
        System.out.println("3. Transfer Funds");
        System.out.println("4. Update Profile");
        System.out.println("5. Logout");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1:
                viewAccounts();
                break;
            case 2:
                viewTransactionHistory();
                break;
            case 3:
                transferFunds();
                break;
            case 4:
                updateProfile();
                break;
            case 5:
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void displayAdminMenu() {
        System.out.println("\n===== ADMIN DASHBOARD =====");
        System.out.println("Welcome, Administrator!");
        System.out.println("1. View All Users");
        System.out.println("2. View All Accounts");
        System.out.println("3. View All Transactions");
        System.out.println("4. Create New Account");
        System.out.println("5. System Statistics");
        System.out.println("6. Logout");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1:
                viewAllUsers();
                break;
            case 2:
                viewAllAccounts();
                break;
            case 3:
                viewAllTransactions();
                break;
            case 4:
                createNewAccount();
                break;
            case 5:
                showSystemStatistics();
                break;
            case 6:
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    // ==================== AUTHENTICATION METHODS ====================

    private static void login() {
        System.out.println("\n===== LOGIN =====");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        User user = userManager.authenticateUser(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    private static void register() {
        System.out.println("\n===== REGISTER =====");
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        String username;
        boolean usernameExists;
        do {
            System.out.print("Username: ");
            username = scanner.nextLine();
            usernameExists = userManager.getUserByUsername(username) != null;
            if (usernameExists) {
                System.out.println("Username already exists. Please choose another one.");
            }
        } while (usernameExists);
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        User newUser = new User(username, password, firstName, lastName, email, UserRole.CUSTOMER);
        userManager.addUser(newUser);
        
        System.out.println("Registration successful! You can now login.");
    }

    private static void logout() {
        currentUser = null;
        System.out.println("You have been logged out successfully.");
    }

    // ==================== CUSTOMER METHODS ====================

    private static void viewAccounts() {
        System.out.println("\n===== YOUR ACCOUNTS =====");
        List<Account> userAccounts = accountManager.getAccountsByUsername(currentUser.getUsername());
        
        if (userAccounts.isEmpty()) {
            System.out.println("You don't have any accounts yet.");
            return;
        }
        
        double totalBalance = 0;
        for (Account account : userAccounts) {
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Account Type: " + account.getAccountName());
            System.out.println("Balance: " + MONEY_FORMAT.format(account.getBalance()));
            System.out.println("-----------------------------");
            totalBalance += account.getBalance();
        }
        
        System.out.println("Total Balance: " + MONEY_FORMAT.format(totalBalance));
    }

    private static void viewTransactionHistory() {
        System.out.println("\n===== TRANSACTION HISTORY =====");
        List<Account> userAccounts = accountManager.getAccountsByUsername(currentUser.getUsername());
        
        if (userAccounts.isEmpty()) {
            System.out.println("You don't have any accounts to view transactions for.");
            return;
        }
        
        System.out.println("Select an account to view transactions:");
        for (int i = 0; i < userAccounts.size(); i++) {
            System.out.println((i + 1) + ". " + userAccounts.get(i).getAccountName() + " (" + userAccounts.get(i).getAccountNumber() + ")");
        }
        System.out.print("Enter your choice (or 0 to view all): ");
        
        int choice = getIntInput();
        List<Transaction> transactions;
        
        if (choice == 0) {
            // View all transactions for all user accounts
            transactions = new ArrayList<>();
            for (Account account : userAccounts) {
                transactions.addAll(transactionManager.getTransactionsByAccountNumber(account.getAccountNumber()));
            }
        } else if (choice >= 1 && choice <= userAccounts.size()) {
            // View transactions for a specific account
            Account selectedAccount = userAccounts.get(choice - 1);
            transactions = transactionManager.getTransactionsByAccountNumber(selectedAccount.getAccountNumber());
        } else {
            System.out.println("Invalid choice.");
            return;
        }
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        // Sort transactions by date (newest first)
        transactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        
        System.out.println("\nTransaction History:");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-12s %-10s %-20s %-10s %-15s\n", "Date", "Type", "Description", "Amount", "Balance");
        System.out.println("------------------------------------------------------------");
        
        for (Transaction transaction : transactions) {
            String type;
            String amount;
            
            if (isUserAccount(transaction.getFromAccount()) && isUserAccount(transaction.getToAccount())) {
                // Internal transfer
                type = "Transfer";
                amount = MONEY_FORMAT.format(transaction.getAmount());
            } else if (isUserAccount(transaction.getFromAccount())) {
                // Money going out
                type = "Debit";
                amount = "-" + MONEY_FORMAT.format(transaction.getAmount());
            } else {
                // Money coming in
                type = "Credit";
                amount = "+" + MONEY_FORMAT.format(transaction.getAmount());
            }
            
            System.out.printf("%-12s %-10s %-20s %-10s\n", 
                    new SimpleDateFormat("yyyy-MM-dd").format(transaction.getDate()),
                    type,
                    transaction.getDescription(),
                    amount);
        }
        System.out.println("------------------------------------------------------------");
    }

    private static boolean isUserAccount(String accountNumber) {
        List<Account> userAccounts = accountManager.getAccountsByUsername(currentUser.getUsername());
        for (Account account : userAccounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return true;
            }
        }
        return false;
    }

    private static void transferFunds() {
        System.out.println("\n===== TRANSFER FUNDS =====");
        List<Account> userAccounts = accountManager.getAccountsByUsername(currentUser.getUsername());
        
        if (userAccounts.size() < 1) {
            System.out.println("You need at least one account to make transfers.");
            return;
        }
        
        // Select source account
        System.out.println("Select source account:");
        for (int i = 0; i < userAccounts.size(); i++) {
            Account account = userAccounts.get(i);
            System.out.println((i + 1) + ". " + account.getAccountName() + " (" + account.getAccountNumber() + ") - Balance: " + MONEY_FORMAT.format(account.getBalance()));
        }
        System.out.print("Enter your choice: ");
        int sourceChoice = getIntInput();
        
        if (sourceChoice < 1 || sourceChoice > userAccounts.size()) {
            System.out.println("Invalid account selection.");
            return;
        }
        
        Account sourceAccount = userAccounts.get(sourceChoice - 1);
        
        // Select destination account or enter external account
        System.out.println("\nSelect destination:");
        System.out.println("1. Transfer to my account");
        System.out.println("2. Transfer to another account");
        System.out.print("Enter your choice: ");
        int transferType = getIntInput();
        
        String destinationAccountNumber;
        
        if (transferType == 1) {
            // Transfer to own account
            if (userAccounts.size() < 2) {
                System.out.println("You need at least two accounts to make an internal transfer.");
                return;
            }
            
            System.out.println("Select destination account:");
            for (int i = 0; i < userAccounts.size(); i++) {
                Account account = userAccounts.get(i);
                if (!account.getAccountNumber().equals(sourceAccount.getAccountNumber())) {
                    System.out.println((i + 1) + ". " + account.getAccountName() + " (" + account.getAccountNumber() + ")");
                }
            }
            System.out.print("Enter your choice: ");
            int destChoice = getIntInput();
            
            if (destChoice < 1 || destChoice > userAccounts.size() || userAccounts.get(destChoice - 1).getAccountNumber().equals(sourceAccount.getAccountNumber())) {
                System.out.println("Invalid account selection.");
                return;
            }
            
            destinationAccountNumber = userAccounts.get(destChoice - 1).getAccountNumber();
        } else if (transferType == 2) {
            // Transfer to external account
            System.out.print("Enter destination account number: ");
            destinationAccountNumber = scanner.nextLine();
            
            Account destinationAccount = accountManager.getAccountByNumber(destinationAccountNumber);
            if (destinationAccount == null) {
                System.out.println("Destination account not found.");
                return;
            }
        } else {
            System.out.println("Invalid choice.");
            return;
        }
        
        // Enter amount and description
        System.out.print("Enter amount to transfer: $");
        double amount = getDoubleInput();
        
        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }
        
        if (amount > sourceAccount.getBalance()) {
            System.out.println("Insufficient funds in source account.");
            return;
        }
        
        System.out.print("Enter description (optional): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) {
            description = "Fund Transfer";
        }
        
        // Perform the transfer
        boolean success = accountManager.transferFunds(sourceAccount.getAccountNumber(), destinationAccountNumber, amount);
        
        if (success) {
            // Create transaction record
            String transactionId = "TRX-" + String.format("%03d", transactionManager.getAllTransactions().size() + 1);
            Transaction transaction = new Transaction(transactionId, sourceAccount.getAccountNumber(), destinationAccountNumber, amount, description, new Date());
            transactionManager.addTransaction(transaction);
            
            System.out.println("Transfer completed successfully!");
            System.out.println("Transaction ID: " + transactionId);
            System.out.println("Amount: " + MONEY_FORMAT.format(amount));
            System.out.println("New Balance: " + MONEY_FORMAT.format(sourceAccount.getBalance() - amount));
        } else {
            System.out.println("Transfer failed. Please try again.");
        }
    }

    private static void updateProfile() {
        System.out.println("\n===== UPDATE PROFILE =====");
        System.out.println("1. Update Name");
        System.out.println("2. Update Email");
        System.out.println("3. Change Password");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1:
                System.out.print("Enter new first name: ");
                String firstName = scanner.nextLine();
                System.out.print("Enter new last name: ");
                String lastName = scanner.nextLine();
                currentUser.setFirstName(firstName);
                currentUser.setLastName(lastName);
                System.out.println("Name updated successfully!");
                break;
            case 2:
                System.out.print("Enter new email: ");
                String email = scanner.nextLine();
                currentUser.setEmail(email);
                System.out.println("Email updated successfully!");
                break;
            case 3:
                System.out.print("Enter current password: ");
                String currentPassword = scanner.nextLine();
                if (!currentUser.getPassword().equals(currentPassword)) {
                    System.out.println("Incorrect password.");
                    return;
                }
                System.out.print("Enter new password: ");
                String newPassword = scanner.nextLine();
                System.out.print("Confirm new password: ");
                String confirmPassword = scanner.nextLine();
                if (!newPassword.equals(confirmPassword)) {
                    System.out.println("Passwords do not match.");
                    return;
                }
                currentUser.setPassword(newPassword);
                System.out.println("Password changed successfully!");
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    // ==================== ADMIN METHODS ====================

    private static void viewAllUsers() {
        System.out.println("\n===== ALL USERS =====");
        List<User> allUsers = userManager.getAllUsers();
        
        System.out.println("Total Users: " + allUsers.size());
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-25s %-10s\n", "Username", "Name", "Email", "Role");
        System.out.println("------------------------------------------------------------");
        
        for (User user : allUsers) {
            System.out.printf("%-15s %-20s %-25s %-10s\n", 
                    user.getUsername(), 
                    user.getFirstName() + " " + user.getLastName(),
                    user.getEmail(),
                    user.getRole());
        }
        System.out.println("------------------------------------------------------------");
        
        System.out.println("\n1. View User Details");
        System.out.println("2. Back to Admin Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        if (choice == 1) {
            System.out.print("Enter username to view details: ");
            String username = scanner.nextLine();
            User user = userManager.getUserByUsername(username);
            
            if (user != null) {
                System.out.println("\n===== USER DETAILS =====");
                System.out.println("Username: " + user.getUsername());
                System.out.println("Name: " + user.getFirstName() + " " + user.getLastName());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Role: " + user.getRole());
                
                List<Account> userAccounts = accountManager.getAccountsByUsername(user.getUsername());
                System.out.println("\nAccounts:");
                if (userAccounts.isEmpty()) {
                    System.out.println("No accounts found for this user.");
                } else {
                    for (Account account : userAccounts) {
                        System.out.println("- " + account.getAccountName() + " (" + account.getAccountNumber() + "): " + MONEY_FORMAT.format(account.getBalance()));
                    }
                }
            } else {
                System.out.println("User not found.");
            }
        }
    }

    private static void viewAllAccounts() {
        System.out.println("\n===== ALL ACCOUNTS =====");
        List<Account> allAccounts = accountManager.getAllAccounts();
        
        System.out.println("Total Accounts: " + allAccounts.size());
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-15s %-15s\n", "Account #", "Type", "Owner", "Balance");
        System.out.println("------------------------------------------------------------");
        
        for (Account account : allAccounts) {
            User owner = userManager.getUserByUsername(account.getOwnerUsername());
            String ownerName = owner != null ? owner.getFirstName() : "Unknown";
            
            System.out.printf("%-15s %-20s %-15s %-15s\n", 
                    account.getAccountNumber(), 
                    account.getAccountName(),
                    ownerName,
                    MONEY_FORMAT.format(account.getBalance()));
        }
        System.out.println("------------------------------------------------------------");
        
        double totalBalance = allAccounts.stream().mapToDouble(Account::getBalance).sum();
        System.out.println("Total Balance Across All Accounts: " + MONEY_FORMAT.format(totalBalance));
    }

    private static void viewAllTransactions() {
        System.out.println("\n===== ALL TRANSACTIONS =====");
        List<Transaction> allTransactions = transactionManager.getAllTransactions();
        
        // Sort transactions by date (newest first)
        allTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        
        System.out.println("Total Transactions: " + allTransactions.size());
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-12s %-12s %-15s %-15s %-10s %-20s\n", 
                "Transaction", "Date", "From", "To", "Amount", "Description");
        System.out.println("------------------------------------------------------------");
        
        for (Transaction transaction : allTransactions) {
            System.out.printf("%-12s %-12s %-15s %-15s %-10s %-20s\n", 
                    transaction.getTransactionId(),
                    new SimpleDateFormat("yyyy-MM-dd").format(transaction.getDate()),
                    transaction.getFromAccount(),
                    transaction.getToAccount(),
                    MONEY_FORMAT.format(transaction.getAmount()),
                    transaction.getDescription());
        }
        System.out.println("------------------------------------------------------------");
        
        System.out.println("\n1. Filter Transactions");
        System.out.println("2. Back to Admin Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        if (choice == 1) {
            System.out.println("\n===== FILTER TRANSACTIONS =====");
            System.out.println("1. Filter by Account");
            System.out.println("2. Filter by Date Range");
            System.out.println("3. Filter by Amount");
            System.out.print("Choose an option: ");
            
            int filterChoice = getIntInput();
            List<Transaction> filteredTransactions = new ArrayList<>();
            
            switch (filterChoice) {
                case 1:
                    System.out.print("Enter account number: ");
                    String accountNumber = scanner.nextLine();
                    filteredTransactions = transactionManager.getTransactionsByAccountNumber(accountNumber);
                    break;
                case 2:
                    System.out.println("Enter start date (yyyy-MM-dd): ");
                    String startDateStr = scanner.nextLine();
                    System.out.println("Enter end date (yyyy-MM-dd): ");
                    String endDateStr = scanner.nextLine();
                    
                    try {
                        Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
                        Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
                        
                        filteredTransactions = allTransactions.stream()
                                .filter(t -> t.getDate().after(startDate) && t.getDate().before(endDate))
                                .collect(Collectors.toList());
                    } catch (Exception e) {
                        System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                        return;
                    }
                    break;
                case 3:
                    System.out.print("Enter minimum amount: $");
                    double minAmount = getDoubleInput();
                    System.out.print("Enter maximum amount: $");
                    double maxAmount = getDoubleInput();
                    
                    filteredTransactions = allTransactions.stream()
                            .filter(t -> t.getAmount() >= minAmount && t.getAmount() <= maxAmount)
                            .collect(Collectors.toList());
                    break;
                default:
                    System.out.println("Invalid option.");
                    return;
            }
            
            if (filteredTransactions.isEmpty()) {
                System.out.println("No transactions found matching the filter criteria.");
                return;
            }
            
            // Sort filtered transactions by date (newest first)
            filteredTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
            
            System.out.println("\n===== FILTERED TRANSACTIONS =====");
            System.out.println("Transactions Found: " + filteredTransactions.size());
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-12s %-12s %-15s %-15s %-10s %-20s\n", 
                    "Transaction", "Date", "From", "To", "Amount", "Description");
            System.out.println("------------------------------------------------------------");
            
            for (Transaction transaction : filteredTransactions) {
                System.out.printf("%-12s %-12s %-15s %-15s %-10s %-20s\n", 
                        transaction.getTransactionId(),
                        new SimpleDateFormat("yyyy-MM-dd").format(transaction.getDate()),
                        transaction.getFromAccount(),
                        transaction.getToAccount(),
                        MONEY_FORMAT.format(transaction.getAmount()),
                        transaction.getDescription());
            }
            System.out.println("------------------------------------------------------------");
        }
    }

    private static void createNewAccount() {
        System.out.println("\n===== CREATE NEW ACCOUNT =====");
        System.out.print("Enter username of account owner: ");
        String username = scanner.nextLine();
        
        User user = userManager.getUserByUsername(username);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        
        System.out.println("Select account type:");
        System.out.println("1. Checking Account");
        System.out.println("2. Savings Account");
        System.out.println("3. Credit Card");
        System.out.println("4. Loan Account");
        System.out.print("Enter your choice: ");
        
        int typeChoice = getIntInput();
        String accountType;
        
        switch (typeChoice) {
            case 1:
                accountType = "Checking Account";
                break;
            case 2:
                accountType = "Savings Account";
                break;
            case 3:
                accountType = "Credit Card";
                break;
            case 4:
                accountType = "Loan Account";
                break;
            default:
                System.out.println("Invalid account type.");
                return;
        }
        
        System.out.print("Enter initial balance: $");
        double initialBalance = getDoubleInput();
        
        if (initialBalance < 0 && (typeChoice == 1 || typeChoice == 2)) {
            System.out.println("Initial balance cannot be negative for checking or savings accounts.");
            return;
        }
        
        // Generate account number
        String accountPrefix;
        switch (typeChoice) {
            case 1:
                accountPrefix = "CHK";
                break;
            case 2:
                accountPrefix = "SAV";
                break;
            case 3:
                accountPrefix = "CRD";
                break;
            case 4:
                accountPrefix = "LN";
                break;
            default:
                accountPrefix = "ACC";
        }
        
        int accountCount = accountManager.getAllAccounts().size() + 1;
        String accountNumber = accountPrefix + "-" + String.format("%03d", accountCount);
        
        Account newAccount = new Account(accountNumber, accountType, username, initialBalance);
        accountManager.addAccount(newAccount);
        
        System.out.println("Account created successfully!");
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Type: " + accountType);
        System.out.println("Initial Balance: " + MONEY_FORMAT.format(initialBalance));
    }

    private static void showSystemStatistics() {
        System.out.println("\n===== SYSTEM STATISTICS =====");
        
        List<User> allUsers = userManager.getAllUsers();
        List<Account> allAccounts = accountManager.getAllAccounts();
        List<Transaction> allTransactions = transactionManager.getAllTransactions();
        
        int customerCount = (int) allUsers.stream().filter(u -> u.getRole() == UserRole.CUSTOMER).count();
        int adminCount = (int) allUsers.stream().filter(u -> u.getRole() == UserRole.ADMIN).count();
        
        double totalBalance = allAccounts.stream().mapToDouble(Account::getBalance).sum();
        double avgBalance = totalBalance / allAccounts.size();
        
        // Get account types
        Map<String, Long> accountTypeCount = allAccounts.stream()
                .collect(Collectors.groupingBy(Account::getAccountName, Collectors.counting()));
        
        // Get transaction statistics
        double totalTransactionAmount = allTransactions.stream().mapToDouble(Transaction::getAmount).sum();
        double avgTransactionAmount = totalTransactionAmount / allTransactions.size();
        
        // Find the largest transaction
        Transaction largestTransaction = allTransactions.stream()
                .max(Comparator.comparing(Transaction::getAmount))
                .orElse(null);
        
        // Print statistics
        System.out.println("Total Users: " + allUsers.size() + " (" + customerCount + " customers, " + adminCount + " admins)");
        System.out.println("Total Accounts: " + allAccounts.size());
        System.out.println("Total Transactions: " + allTransactions.size());
        System.out.println("Total Balance: " + MONEY_FORMAT.format(totalBalance));
        System.out.println("Average Account Balance: " + MONEY_FORMAT.format(avgBalance));
        
        System.out.println("\nAccount Types:");
        for (Map.Entry<String, Long> entry : accountTypeCount.entrySet()) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue());
        }
        
        System.out.println("\nTransaction Statistics:");
        System.out.println("Total Transaction Amount: " + MONEY_FORMAT.format(totalTransactionAmount));
        System.out.println("Average Transaction Amount: " + MONEY_FORMAT.format(avgTransactionAmount));
        
        if (largestTransaction != null) {
            System.out.println("Largest Transaction: " + MONEY_FORMAT.format(largestTransaction.getAmount()) + 
                    " (" + largestTransaction.getTransactionId() + ")");
        }
        
        // Get recent activity
        System.out.println("\nRecent Activity:");
        allTransactions.stream()
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .limit(5)
                .forEach(t -> System.out.println("- " + DATE_FORMAT.format(t.getDate()) + ": " + 
                        t.getDescription() + " - " + MONEY_FORMAT.format(t.getAmount())));
    }

    // ==================== UTILITY METHODS ====================

    private static int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double getDoubleInput() {
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ==================== MODEL CLASSES ====================

    enum UserRole {
        CUSTOMER,
        ADMIN
    }

    static class User {
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String email;
        private UserRole role;

        public User(String username, String password, String firstName, String lastName, String email, UserRole role) {
            this.username = username;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public UserRole getRole() {
            return role;
        }
    }

    static class Account {
        private String accountNumber;
        private String accountName;
        private String ownerUsername;
        private double balance;

        public Account(String accountNumber, String accountName, String ownerUsername, double balance) {
            this.accountNumber = accountNumber;
            this.accountName = accountName;
            this.ownerUsername = ownerUsername;
            this.balance = balance;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getAccountName() {
            return accountName;
        }

        public String getOwnerUsername() {
            return ownerUsername;
        }

        public double getBalance() {
            return balance;
        }

        public void deposit(double amount) {
            if (amount > 0) {
                this.balance += amount;
            }
        }

        public boolean withdraw(double amount) {
            if (amount > 0 && amount <= this.balance) {
                this.balance -= amount;
                return true;
            }
            return false;
        }
    }

    static class Transaction {
        private String transactionId;
        private String fromAccount;
        private String toAccount;
        private double amount;
        private String description;
        private Date date;

        public Transaction(String transactionId, String fromAccount, String toAccount, double amount, String description, Date date) {
            this.transactionId = transactionId;
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
            this.amount = amount;
            this.description = description;
            this.date = date;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getFromAccount() {
            return fromAccount;
        }

        public String getToAccount() {
            return toAccount;
        }

        public double getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }

        public Date getDate() {
            return date;
        }
    }

    // ==================== MANAGER CLASSES ====================

    static class UserManager {
        private List<User> users = new ArrayList<>();

        public void addUser(User user) {
            users.add(user);
        }

        public User getUserByUsername(String username) {
            return users.stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);
        }

        public User authenticateUser(String username, String password) {
            User user = getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
            return null;
        }

        public List<User> getAllUsers() {
            return new ArrayList<>(users);
        }
    }

    static class AccountManager {
        private List<Account> accounts = new ArrayList<>();

        public void addAccount(Account account) {
            accounts.add(account);
        }

        public Account getAccountByNumber(String accountNumber) {
            return accounts.stream()
                    .filter(a -> a.getAccountNumber().equals(accountNumber))
                    .findFirst()
                    .orElse(null);
        }

        public List<Account> getAccountsByUsername(String username) {
            return accounts.stream()
                    .filter(a -> a.getOwnerUsername().equals(username))
                    .collect(Collectors.toList());
        }

        public List<Account> getAllAccounts() {
            return new ArrayList<>(accounts);
        }

        public boolean transferFunds(String fromAccountNumber, String toAccountNumber, double amount) {
            Account fromAccount = getAccountByNumber(fromAccountNumber);
            Account toAccount = getAccountByNumber(toAccountNumber);
            
            if (fromAccount == null || toAccount == null) {
                return false;
            }
            
            if (amount <= 0 || amount > fromAccount.getBalance()) {
                return false;
            }
            
            boolean withdrawSuccess = fromAccount.withdraw(amount);
            if (withdrawSuccess) {
                toAccount.deposit(amount);
                return true;
            }
            
            return false;
        }
    }

    static class TransactionManager {
        private List<Transaction> transactions = new ArrayList<>();

        public void addTransaction(Transaction transaction) {
            transactions.add(transaction);
        }

        public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
            return transactions.stream()
                    .filter(t -> t.getFromAccount().equals(accountNumber) || t.getToAccount().equals(accountNumber))
                    .collect(Collectors.toList());
        }

        public List<Transaction> getAllTransactions() {
            return new ArrayList<>(transactions);
        }
    }
}
