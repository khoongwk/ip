package duke;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import task.Deadline;
import task.Event;
import task.Task;
import task.Todo;

/**
 * Handles the opening and storing of tasks from text files.
 */
public class Storage {

    private String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Parses text file from file path specified to get
     * the Task objects stored, and returns them all
     * packaged in an ArrayList.
     *
     * @return ArrayList containing Task objects.
     * @throws DukeException Exception while loading from text file.
     */
    ArrayList<Task> load() throws DukeException {
        File taskListFile = new File(filePath);
        try {
            if (!taskListFile.exists() && !taskListFile.createNewFile()) {
                throw new DukeException("Failed to create new text file.");
            }
        } catch (Exception e) {
            throw new DukeException("Exception while opening task list file: " + e);
        }

        ArrayList<Task> outputTaskList = new ArrayList<>();
        Scanner taskReader;
        try {
            taskReader = new Scanner(taskListFile);
            while (taskReader.hasNextLine()) {
                String taskFromFile = taskReader.nextLine();
                // Note, this is assuming that format of
                // task.Task.getDescriptionForDatabase() remains the same.
                String[] formattedTaskString = taskFromFile.split(" - ");
                String taskType = formattedTaskString[0];
                boolean isTaskDone = formattedTaskString[1].equals("1");
                switch (taskType) {
                case "todo":
                    outputTaskList.add(new Todo(formattedTaskString[2], isTaskDone));
                    break;
                case "event":
                    outputTaskList.add(
                            new Event(
                                    formattedTaskString[2],
                                    formattedTaskString[3],
                                    isTaskDone
                            )
                    );
                    break;
                case "deadline":
                    outputTaskList.add(
                            new Deadline(
                                    formattedTaskString[2],
                                    formattedTaskString[3],
                                    isTaskDone
                            )
                    );
                    break;
                default:
                    break;
                }
            }
            taskReader.close();
        } catch (FileNotFoundException e) {
            throw new DukeException("Exception while scanning task list file: " + e);
        }
        return outputTaskList;
    }

    /**
     * Saves the Tasks stored in a TaskList into the file path of the Storage.
     *
     * @param taskList TaskList object containing Task(s) to store.
     * @return true if store was successful.
     * @throws DukeException Exception while storing into file.
     */
    public boolean store(TaskList taskList) throws DukeException {
        try {
            File taskListFile = new File(filePath);
            // Clear the pre-existing file if there is one.
            if (taskListFile.exists()) {
                taskListFile.delete();
                taskListFile.createNewFile();
            } else {
                throw new IOException("No file found at the specified path.");
            }
            if (taskList.getSize() > 0) {
                FileWriter taskWriter = new FileWriter(taskListFile);
                for (Task t : taskList.getTaskList()) {
                    taskWriter.write(t.getDescriptionForDatabase());
                    taskWriter.write("\n");
                }
                taskWriter.close();
            }
            return true;
        } catch (IOException e) {
            throw new DukeException("Exception occurred while storing into file: " + e);
        }
    }
}
