package edu.gvsu.cis.cis656.client;

import edu.gvsu.cis.cis656.clock.VectorClock;
import edu.gvsu.cis.cis656.message.Message;
import edu.gvsu.cis.cis656.message.MessageComparator;
import edu.gvsu.cis.cis656.message.MessageTypes;
import edu.gvsu.cis.cis656.queue.PriorityQueue;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MessageHandler implements Runnable {
    private VectorClock clock;
    private String username;
    private int pid;
    private DatagramSocket socket;
    private static final int SERVER_PORT = 8000;
    private PriorityQueue<Message> messagePriorityQueue;
    private static final String SERVER = "_SERVER";
    private static final int NO_PID = -1;

    private String REGISTRATION_MESSAGE = "Hello Mr Server. I'd like to register a user if you don't mind";

    /**
     * Initializes the vector clock, the priority queue and sets the current PID to a kown (invalid) value.
     *
     * @param username username for the chat.
     */
    public MessageHandler(String username) {
        this.username = username;
        clock = new VectorClock();
        messagePriorityQueue = new PriorityQueue<Message>(new MessageComparator());
        pid = NO_PID;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the server.
     * Only ticks the clock after the user has been sucessfully registered on the server.
     *
     * @param message Message that will be sent to the server.
     */
    public void sendMessage(Message message) {
        InetAddress serverAddress = null;
        try {
            serverAddress = InetAddress.getLocalHost();
            if (pid != NO_PID) {
                clock.tick(pid);
            }
            Message.sendMessage(message, socket, serverAddress, SERVER_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    /**
     * Builds and sends a chat message to the server.
     *
     * @param text Text of the message to be sent.
     */
    public void sendChatMessage(String text) {
        Message chatMessage = new Message(
                MessageTypes.CHAT_MSG,
                username,
                pid,
                clock,
                text
        );
        sendMessage(chatMessage);
    }

    /**
     * Builds and sends the register user message to the server.
     */
    public void registerUser() {
        Message registrationMessage = new Message(
                MessageTypes.REGISTER,
                username,
                pid,
                clock,
                REGISTRATION_MESSAGE);
        sendMessage(registrationMessage);
    }

    @Override
    /**
     * Receives messages from the server.
     */
    public void run() {
        while (true) {
            Message message = Message.receiveMessage(socket);
            if (message == null) {
                continue;
            }

            if (message.sender.equals(SERVER)) {
                handleServerMessage(message);
            } else {
                messagePriorityQueue.add(message);
                outputPendingMessages();
            }
        }
    }

    /**
     * Handles the server response message after a registration attempt.
     *
     * @param message Message received from the server
     */
    private void handleServerMessage(Message message) {
        switch (message.type) {
            case MessageTypes.ERROR:
                Client.terminate(message.message);
                break;
            case MessageTypes.ACK:
                pid = message.pid;
                break;
        }
    }

    /**
     * Outputs all messages that can be displayed from the priority queue.
     */
    private void outputPendingMessages() {
        Message topMessage = messagePriorityQueue.peek();
        while (topMessage != null) {
            if (messageCanBeDisplayed(topMessage)) {
                displayMessage();
                topMessage = messagePriorityQueue.peek();
            } else {
                topMessage = null;
            }
        }
    }

    /**
     * Displays the first message in the priority queue
     */
    private void displayMessage() {
        Message message = messagePriorityQueue.poll();
        clock.update(message.ts);
        Client.receiveMessage(message.sender + ": " + message.message);
    }

    /**
     * Checks wether or not a message can be displayed.
     * <p>
     * For a message to be displayed it has to meet two conditions:
     * It must be the next message we expect from the sender and we must have seen al the messages the sender has seen
     * before the current message.
     *
     * @param message Message that is being checked
     * @return
     */
    private boolean messageCanBeDisplayed(Message message) {
        VectorClock messageClock = message.ts;
        int messagePid = message.pid;

        boolean receiverHasSeenAllMessagesSeenBySender = true;
        for (String pid : messageClock.getKnownPids()) {
            int integerPid = Integer.parseInt(pid);
            if (integerPid == messagePid) {
                continue;
            }

            if (messageClock.getTime(integerPid) > clock.getTime(integerPid)) {
                receiverHasSeenAllMessagesSeenBySender = false;
                break;
            }
        }

        boolean messageIsNextExpectedFromSender = clock.getTime(messagePid) + 1 == messageClock.getTime(messagePid);

        return messageIsNextExpectedFromSender && receiverHasSeenAllMessagesSeenBySender;
    }
}
