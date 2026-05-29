package br.com.checklistweb.category;

public enum ChecklistPerson {

    AMBOS("Ambos"),
    MORZIN("Morzin"),
    MORZINHA("Morzinha");

    private final String label;

    ChecklistPerson(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}