package ma.skillswap.model.entity.enums;

public enum Level {
    DEBUTANT("Débutant"),
    INTERMEDIAIRE("Intermédiaire"),
    EXPERT("Expert");

    private final String label;

    Level(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}