package br.com.checklistweb.category;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChecklistPerson person = ChecklistPerson.AMBOS;

    public Category() {
    }

    public Category(String name, Integer displayOrder, ChecklistPerson person) {
        this.name = name;
        this.displayOrder = displayOrder;
        this.person = person;
        this.active = true;
    }

    @PrePersist
    public void prePersist() {
        if (this.displayOrder == null) {
            this.displayOrder = 0;
        }

        if (this.active == null) {
            this.active = true;
        }

        if (this.person == null) {
            this.person = ChecklistPerson.AMBOS;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.displayOrder == null) {
            this.displayOrder = 0;
        }

        if (this.active == null) {
            this.active = true;
        }

        if (this.person == null) {
            this.person = ChecklistPerson.AMBOS;
        }
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setPerson(ChecklistPerson person) {
        this.person = person;
    }
}