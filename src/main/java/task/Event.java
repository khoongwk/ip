package task;

/**
 * An event is a task which occurs at a particular date and time.
 */
public class Event extends Task {

    private String eventDate;

    public Event(String description, String date) {
        super(description);
        this.eventDate = date;
    }

    public Event(String description, String date, boolean isDone) {
        super(description, isDone);
        this.eventDate = date;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (at: "
                + eventDate + ")";
    }

    @Override
    public String getDescriptionForDatabase() {
        return "event - " + (this.isTaskDone() ? "1" : "0") + " - " +
                this.getDescription() + " - " + this.eventDate;
    }
}
