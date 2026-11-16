package com.example.budgetservice.controller;

import com.example.budgetservice.model.Budget;
import com.example.budgetservice.model.Expense;
import com.example.budgetservice.service.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Budget service is running!";
    }


    // POST /api/budgets   body: { "projectId": 1, "totalAmount": 50000 }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Budget createBudget(@RequestBody Budget budget) {
        return budgetService.createBudget(budget.getProjectId(), budget.getTotalAmount());
    }

    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetService.getAllBudgets();
    }

    @GetMapping("/{id}")
    public Budget getBudget(@PathVariable Long id) {
        return budgetService.getBudget(id);
    }

    // GET /api/budgets/project/1
    @GetMapping("/project/{projectId}")
    public Budget getBudgetForProject(@PathVariable Long projectId) {
        return budgetService.getBudgetForProject(projectId);
    }

    // DELETE /api/budgets/4
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
    }


    @PostMapping("/{budgetId}/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public Expense addExpense(@PathVariable Long budgetId, @RequestBody Expense expense) {
        return budgetService.addExpense(budgetId, expense);
    }

    @GetMapping("/{budgetId}/expenses")
    public List<Expense> getExpenses(@PathVariable Long budgetId) {
        return budgetService.getExpenses(budgetId);
    }

    @DeleteMapping("/{budgetId}/expenses/{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(@PathVariable Long budgetId, @PathVariable Long expenseId) {
        budgetService.deleteExpense(budgetId, expenseId);
    }
}