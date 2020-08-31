package duke;

import task.Deadline;
import task.Event;
import task.Todo;

class Duke {

    private Storage storage;
    private TaskList taskList;
    private Ui ui;

    private boolean isRunning;

    Duke(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.isRunning = true;
        try {
            taskList = new TaskList(storage.load());
        } catch (DukeException e) {
            ui.showException(e);
            taskList = new TaskList();
        }
    }


    /**
     * Main control system which loops user input until user exits with the
     * command 'bye'.
     */
    private void run() {
        ui.showIntroduction();
        ui.showLine();
        // Loop program until command 'bye' is entered as input.
        while (isRunning) {
            try {
                String input = ui.readCommand();
                Parser.Command command = Parser.parseCommand(input);
                switch (command) {
                    case BYE:
                        this.isRunning = false;
                        if (storage.store(taskList)) {
                            ui.showMessage("Saved your list. Exiting...");
                        }
                        break;
                    case LIST:
                        ui.showMessage("Here are the tasks in your list:");
                        String tasksString = taskList.getAllTasks();
                        if (tasksString.length() == 0) {
                            ui.showMessage("No tasks found.");
                        } else {
                            ui.showMessage(tasksString);
                        }
                        break;
                    case DONE:
                        if (input.substring(4).length() > 1) {
                            // For processing "done" command with the corresponding integer value.
                            String numString = input.substring(5);
                            int entryNum = Integer.parseInt(numString);
                            if (taskList.markTaskDone(entryNum)) {
                                ui.showMessage("Nice! I've marked this task as done:");
                                ui.showMessage(taskList.getTask(entryNum - 1).toString());
                            }
                        } else {
                            throw new DukeException("Invalid number for done command.");
                        }
                        break;
                    case DELETE:
                        if (input.substring(6).length() > 1) {
                            String numString = input.substring(7);
                            int entryNum = Integer.parseInt(numString);
                            String taskToRemoveString = taskList.getTask(entryNum - 1).toString();
                            if (taskList.deleteTask(entryNum)) {
                                ui.showMessage("Noted. I have removed this task:");
                                ui.showMessage(taskToRemoveString);
                                ui.showMessage("Now you have " + taskList.getSize() + " tasks in the list.");
                            }
                        } else {
                            throw new DukeException("Invalid number for delete command.");
                        }
                        break;
                    case TODO:
                        if (input.substring(4).length() > 1) {
                            String description = input.substring(5);
                            Todo d = new Todo(description);
                            if (taskList.addTask(d)) {
                                ui.showMessage("Got it, I've added this task: " + d);
                                ui.showMessage("Now you have " + taskList.getSize() + " tasks in the list.");
                            }
                        } else {
                            throw new DukeException("The description of a todo cannot be empty.");
                        }
                        break;
                    case EVENT:
                        if (input.substring(5).length() > 1) {
                            String description = input.substring(6);
                            String[] processedDesc = Parser.parseTimedTask(description);
                            Event e = new Event(processedDesc[0], processedDesc[1]);
                            if (taskList.addTask(e)) {
                                ui.showMessage("Got it, I've added this task: " + e);
                                ui.showMessage("Now you have " + taskList.getSize() + " tasks in the list.");
                            }
                        } else {
                            throw new DukeException("The description of an event cannot be empty.");
                        }
                        break;
                    case DEADLINE:
                        if (input.substring(8).length() > 1) {
                            String description = input.substring(9);
                            String[] processedDesc = Parser.parseTimedTask(description);
                            Deadline d = new Deadline(processedDesc[0], processedDesc[1]);
                            if (taskList.addTask(d)) {
                                ui.showMessage("Got it, I've added this task: " + d);
                                ui.showMessage("Now you have " + taskList.getSize() + " tasks in the list.");
                            }
                        } else {
                            throw new DukeException("The description of a deadline cannot be empty.");
                        }
                        break;
                    case FIND:
                        if (input.substring(4).length() > 1) {
                            String description = input.substring(5);
                            String matchingTasks = taskList.getMatchingTasks(description);
                            if (matchingTasks.length() > 0) {
                                ui.showMessage("Here are the matching tasks in your list:");
                                ui.showMessage(matchingTasks);
                            } else {
                                ui.showMessage("No tasks matching your description was found.");
                            }
                        }
                        break;

                    // Default for INVALID command.
                    default:
                        throw new DukeException("I'm sorry, but I don't know what that means :-(");
                }
            } catch (DukeException e) {
                ui.showException(e);
            } finally {
                ui.showLine();
            }
        }
        ui.closeUi();
    }


    /**
     * Initialise program with a new instance of duke.Duke.
     *
     * @param args String array passed into main.
     */
    public static void main(String[] args) {
        String filePath = System.getProperty("user.dir")
                + (System.getProperty("user.dir").endsWith("text-ui-test")
                ? "\\..\\data\\taskList.txt"
                : "\\data\\taskList.txt");
        Duke dukeProgram = new Duke(filePath);
        dukeProgram.run();
    }
}
