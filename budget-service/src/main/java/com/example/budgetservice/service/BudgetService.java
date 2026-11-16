package com.example.budgetservice.service;

import com.example.budgetservice.exception.BadRequestException;
import com.example.budgetservice.exception.ConflictException;
import com.example.budgetservice.exception.NotFoundException;
import com.example.budgetservice.model.Budget;
import com.example.budgetservice.model.Expense;
import com.example.budgetservice.repository.BudgetRepository;
import com.example.budgetservice.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(BudgetRepository budgetRepository, ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
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
        Budget budget = new Budget(projectId, totalAmount);
        return budgetRepository.save(budget);
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
        budget.addExpense(expense);
        return expenseRepository.save(expense);
    }

    public List<Expense> getExpenses(Long budgetId) {
        getBudget(budgetId);
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
}