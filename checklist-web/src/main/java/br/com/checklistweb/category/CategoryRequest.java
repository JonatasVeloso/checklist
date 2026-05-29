package br.com.checklistweb.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoryRequest {

    @NotBlank
    private String name;

    @NotNull
    private Integer displayOrder;

    @NotNull
    private ChecklistPerson person;

    public CategoryRequest() {
    }

    public String getName() {
        return name;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public ChecklistPerson getPerson() {
        return person;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void setPerson(ChecklistPerson person) {
        this.person = person;
    }
}