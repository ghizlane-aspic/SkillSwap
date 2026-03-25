package ma.skillswap.model.entity.enums;

public enum SwapStatus {
    PENDING("En attente"),
    ACCEPTED("Acceptée"),
    REJECTED("Refusée"),
    COMPLETED("Terminée");

    private final String label;

    SwapStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}