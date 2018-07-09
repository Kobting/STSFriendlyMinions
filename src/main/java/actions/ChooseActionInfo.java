package actions;

public class ChooseActionInfo {

    private String name;
    private String description;
    private Runnable action;

    public ChooseActionInfo(String name, String description, Runnable action) {
        this.name = name;
        this.description = description;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Runnable getAction() {
        return action;
    }
}
