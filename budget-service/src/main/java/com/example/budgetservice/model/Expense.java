package com.example.budgetservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExpenseCategory category = ExpenseCategory.OTHER;

    private Long taskId;

    @Column(nullable = false)
    private LocalDate spentDate;

    public Expense() {
    }

    @PrePersist
    void onCreate() {
        if (spentDate == null) spentDate = LocalDate.now();
        if (category == null) category = ExpenseCategory.OTHER;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }

    public Long getBudgetId() { return budget != null ? budget.getId() : null; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public ExpenseCategory getCategory() { return category; }
    public void setCategory(ExpenseCategory category) { this.category = category; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public LocalDate getSpentDate() { return spentDate; }
    public void setSpentDate(LocalDate spentDate) { this.spentDate = spentDate; }
}