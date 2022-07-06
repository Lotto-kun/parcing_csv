public enum OperationType {
    income("приход"),
    expense("расход");

    private final String type;

    OperationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
