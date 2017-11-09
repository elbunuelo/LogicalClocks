package edu.gvsu.cis.cis656.client;

import java.util.Scanner;

/**
 * Handles the input of commands and relays them to the Client for execution.
 */
public class InputReader implements Runnable {

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();

            if (message.trim().equals("")) {
                Client.output("");
            } else {
                Client.sendMessage(message);
            }
        }
    }
}
