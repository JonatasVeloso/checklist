package br.com.checklistweb.category;

public class CategoryResponse {

    private Long id;
    private String name;
    private Integer displayOrder;
    private Boolean active;
    private ChecklistPerson person;
    private String personLabel;

    public CategoryResponse() {
    }

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.displayOrder = category.getDisplayOrder();
        this.active = category.getActive();
        this.person = category.getPerson();
        this.personLabel = category.getPerson().getLabel();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public Boolean getActive() {
        return active;
    }

    public ChecklistPerson getPerson() {
        return person;
    }

    public String getPersonLabel() {
        return personLabel;
    }
}