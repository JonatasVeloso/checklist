package br.com.checklistweb.category;

public class CategoryResponse {

    private Long id;
    private String name;
    private Integer displayOrder;
    private Boolean active;

    public CategoryResponse() {
    }

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.displayOrder = category.getDisplayOrder();
        this.active = category.getActive();
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
}