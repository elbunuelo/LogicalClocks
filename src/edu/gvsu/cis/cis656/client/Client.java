package edu.gvsu.cis.cis656.client;

public class Client {
    private static final String COLOR_PURPLE = "[38;5;165m";
    public static final String COLOR_BLUE = "[38;5;63m";

    private static MessageHandler messageHandler;
    private static OutputWriter writer;
    private static String username;

    /**
     * Displays the help message
     */
    public static void printHelp() {
        System.out.println("Invalid parameters specified. The correct invocation is: ");
        System.out.println("user");
    }


    /**
     * Gets the username from the command line parameters and kicks off all of the functionality by initializing the
     * message handler, registering the user and configuring the ui.
     *
     * @param args
     */
    public static void main(String args[]) {

        if (args.length == 0) {
            printHelp();
            System.exit(1);
        }

        username = args[0];

        initUser();
        startIO();
        welcomeUser();

    }

    /**
     * Prints a welcome message for the user
     */
    private static void welcomeUser() {
        output("Welcome to the chat, all messages you send will be broadcast to all of the connected users");
    }

    /**
     * Initializes the user interface by configuring the output writer and starting the input thread
     */
    private static void startIO() {
        writer = new OutputWriter(username);
        Thread inputThread = new Thread(new InputReader());
        inputThread.start();

    }

    /**
     * Configures the message handler, registers the user with the server and starts the message listener thread.
     */
    private static void initUser() {
        messageHandler = new MessageHandler(username);
        messageHandler.registerUser();
        Thread messageListener = new Thread(messageHandler);
        messageListener.start();
    }

    /**
     * Default output method, prints the content of the message passed in purple.
     *
     * @param message Message to be printed to the console.
     */
    public static void output(String message) {
        output(message, COLOR_PURPLE);
    }

    /**
     * Specific output method, prints the content of the message in the specified color.
     *
     * @param message Message to be printed to the console.
     * @param color   String that determines the color of the output.
     */
    public static void output(String message, String color) {
        writer.addEvent((char) 27 + color + message + (char) 27 + "[0m");
    }

    /**
     * Prints out a message received from the server.
     *
     * @param messageText Text of the message that will be shown in the terminal.
     */
    public static void receiveMessage(String messageText) {
        output(messageText, COLOR_BLUE);
    }

    /**
     * Sends a message to the server to be broadcast
     *
     * @param messageText Text of the message that will be broadcast.
     */
    public static void sendMessage(String messageText) {
        output("You: " + messageText);
        messageHandler.sendChatMessage(messageText);
    }

    /**
     * Finishes the execution with an error message
     *
     * @param message error message to be shown when exiting
     */
    public static void terminate(String message) {
        System.out.println(message);
        System.exit(1);
    }
}
