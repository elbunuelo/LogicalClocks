package edu.gvsu.cis.cis656.client;

import java.util.ArrayList;

public class OutputWriter {
    ArrayList<String> events;
    String username;

    public OutputWriter(String username) {
        this.events = new ArrayList<>();
        this.username = username;
        render();
    }

    /**
     * Constructs the prompt to be shown to the user.
     * <p>
     * The prompt displays the username and a busy indicator in case the user is busy
     *
     * @return
     */
    private String buildPrompt() {
        String prompt = username;
        prompt += "> ";

        return prompt;
    }


    /**
     * Adds an event to the list and rerenders the terminal.
     *
     * @param event Text that will be shown in a line of the terminal.
     */
    public void addEvent(String event) {
        events.add(event);
        render();
    }

    /**
     * Renders the console, writing all of the previous events and the prompt at the bottom.
     * <p>
     * This method is not very portable and relies on the LINES and COLUMNS environment variables that unix systems
     * have for rendering the prompt at the bottom and the separator on the second to last line of the terminal.
     */
    private void render() {
        System.out.print("\033[H\033[2J");
        int terminalLines = Integer.parseInt(System.getenv("LINES"));
        int terminalCols = Integer.parseInt(System.getenv("COLUMNS"));

        if (events.size() < terminalLines) {
            for (int i = 0; i < terminalLines - events.size() - 2; i++) {
                System.out.println("");
            }
        }

        for (int i = 0; i < events.size(); i++) {
            System.out.println(events.get(i));
        }

        String separator = "";
        for (int i = 0; i < terminalCols; i++) {
            separator += "-";
        }
        System.out.println(separator);
        System.out.print(buildPrompt());
    }
}
