package com.example.budgetservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long projectId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    private String currency = "NOK";

    @Column(nullable = false)
    private Integer warningThresholdPercent = 80;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Expense> expenses = new ArrayList<>();

    public Budget() {
    }

    public Budget(Long projectId, BigDecimal totalAmount) {
        this.projectId = projectId;
        this.totalAmount = totalAmount;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }


    public BigDecimal getSpentAmount() {
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getRemainingAmount() {
        return totalAmount.subtract(getSpentAmount());
    }

    public BigDecimal getPercentUsed() {
        if (totalAmount.signum() == 0) return BigDecimal.ZERO;
        return getSpentAmount().multiply(BigDecimal.valueOf(100))
                .divide(totalAmount, 2, RoundingMode.HALF_UP);
    }

    public String getStatus() {
        if (getSpentAmount().compareTo(totalAmount) > 0) return "EXCEEDED";
        if (getPercentUsed().compareTo(BigDecimal.valueOf(warningThresholdPercent)) >= 0) return "WARNING";
        return "OK";
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        expense.setBudget(this);
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
        expense.setBudget(null);
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getWarningThresholdPercent() { return warningThresholdPercent; }
    public void setWarningThresholdPercent(Integer warningThresholdPercent) { this.warningThresholdPercent = warningThresholdPercent; }

    public Instant getCreatedAt() { return createdAt; }

    public List<Expense> getExpenses() { return expenses; }
}