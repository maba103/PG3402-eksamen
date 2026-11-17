package com.example.budgetservice.service;

import com.example.budgetservice.dto.UpdateBudgetRequest;
import com.example.budgetservice.event.BudgetEvent;
import com.example.budgetservice.exception.BadRequestException;
import com.example.budgetservice.exception.ConflictException;
import com.example.budgetservice.exception.NotFoundException;
import com.example.budgetservice.messaging.BudgetEventPublisher;
import com.example.budgetservice.model.Budget;
import com.example.budgetservice.model.Expense;
import com.example.budgetservice.repository.BudgetRepository;
import com.example.budgetservice.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetEventPublisher eventPublisher;

    public BudgetService(BudgetRepository budgetRepository,
                         ExpenseRepository expenseRepository,
                         BudgetEventPublisher eventPublisher) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.eventPublisher = eventPublisher;
    }

    public Budget createBudget(Long projectId, BigDecimal totalAmount) {
        if (projectId == null || projectId <= 0) {
            throw new BadRequestException("projectId is required and must be positive");
        }
        if (totalAmount == null || totalAmount.signum() < 0) {
            throw new BadRequestException("totalAmount is required and cannot be negative");
        }
        if (budgetRepository.existsByProjectId(projectId)) {
            throw new ConflictException("Project " + projectId + " already has a budget");
        }
        Budget budget = budgetRepository.save(new Budget(projectId, totalAmount));

        sendEvent("budget.created", budget,
                "Budget of " + totalAmount + " " + budget.getCurrency() + " created for project " + projectId);
        return budget;
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Budget getBudget(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Budget " + id + " not found"));
    }

    public Budget getBudgetForProject(Long projectId) {
        return budgetRepository.findByProjectId(projectId)
                .orElseThrow(() -> new NotFoundException("No budget found for project " + projectId));
    }

    @Transactional
    public Budget updateBudget(Long id, UpdateBudgetRequest request) {
        Budget budget = getBudget(id);
        String statusBefore = budget.getStatus();

        if (request.totalAmount() != null) {
            if (request.totalAmount().signum() < 0) {
                throw new BadRequestException("totalAmount cannot be negative");
            }
            budget.setTotalAmount(request.totalAmount());
        }
        if (request.warningThresholdPercent() != null) {
            int p = request.warningThresholdPercent();
            if (p < 1 || p > 100) {
                throw new BadRequestException("warningThresholdPercent must be between 1 and 100");
            }
            budget.setWarningThresholdPercent(p);
        }
        if (request.currency() != null) {
            if (!request.currency().matches("[A-Z]{3}")) {
                throw new BadRequestException("currency must be a 3-letter code, e.g. NOK");
            }
            budget.setCurrency(request.currency());
        }

        sendEventIfStatusGotWorse(budget, statusBefore);
        return budgetRepository.save(budget);
    }

    @Transactional
    public void deleteBudget(Long id) {
        budgetRepository.delete(getBudget(id));
    }

    @Transactional
    public Expense addExpense(Long budgetId, Expense expense) {
        if (expense.getDescription() == null || expense.getDescription().isBlank()) {
            throw new BadRequestException("description is required");
        }
        if (expense.getAmount() == null || expense.getAmount().signum() <= 0) {
            throw new BadRequestException("amount is required and must be greater than 0");
        }
        Budget budget = getBudget(budgetId);
        String statusBefore = budget.getStatus();

        budget.addExpense(expense);
        Expense saved = expenseRepository.save(expense);

        sendEventIfStatusGotWorse(budget, statusBefore);
        return saved;
    }

    public List<Expense> getExpenses(Long budgetId) {
        getBudget(budgetId); // 404 hvis budsjettet ikke finnes
        return expenseRepository.findByBudgetIdOrderBySpentDateDesc(budgetId);
    }

    @Transactional
    public void deleteExpense(Long budgetId, Long expenseId) {
        Budget budget = getBudget(budgetId);
        Expense expense = expenseRepository.findByIdAndBudgetId(expenseId, budgetId)
                .orElseThrow(() -> new NotFoundException(
                        "Expense " + expenseId + " not found in budget " + budgetId));
        budget.removeExpense(expense);
    }

    private void sendEventIfStatusGotWorse(Budget budget, String statusBefore) {
        String statusAfter = budget.getStatus();
        if (rank(statusAfter) <= rank(statusBefore)) {
            return;
        }
        if (statusAfter.equals("EXCEEDED")) {
            sendEvent("budget.exceeded", budget, "Project " + budget.getProjectId()
                    + " is over budget: " + budget.getPercentUsed() + "% used");
        } else {
            sendEvent("budget.warning", budget, "Project " + budget.getProjectId()
                    + " has used " + budget.getPercentUsed() + "% of its budget");
        }
    }

    private int rank(String status) {
        return switch (status) {
            case "EXCEEDED" -> 2;
            case "WARNING" -> 1;
            default -> 0;
        };
    }

    private void sendEvent(String type, Budget budget, String message) {
        eventPublisher.publish(new BudgetEvent(
                type,
                budget.getId(),
                budget.getProjectId(),
                budget.getTotalAmount(),
                budget.getSpentAmount(),
                budget.getPercentUsed(),
                budget.getCurrency(),
                budget.getStatus(),
                message,
                Instant.now()));
    }
}