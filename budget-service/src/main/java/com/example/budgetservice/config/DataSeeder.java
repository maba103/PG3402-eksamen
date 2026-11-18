package com.example.budgetservice.config;

import com.example.budgetservice.model.Budget;
import com.example.budgetservice.model.Expense;
import com.example.budgetservice.model.ExpenseCategory;
import com.example.budgetservice.repository.BudgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@ConditionalOnProperty(name = "budget.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final BudgetRepository budgetRepository;

    public DataSeeder(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (budgetRepository.count() > 0) {
            log.info("Database already has budgets, skipping test data");
            return;
        }
        LocalDate today = LocalDate.now();

        Budget project1 = new Budget(1L, new BigDecimal("150000.00"));
        project1.addExpense(expense("Tiles for bathroom", "25000.00", ExpenseCategory.MATERIALS, 1L, today.minusDays(20)));
        project1.addExpense(expense("Plumber, 10 hours", "15000.00", ExpenseCategory.LABOR, 2L, today.minusDays(10)));
        project1.addExpense(expense("Building permit", "12500.00", ExpenseCategory.PERMITS, null, today.minusDays(5)));
        budgetRepository.save(project1);

        Budget project2 = new Budget(2L, new BigDecimal("50000.00"));
        project2.addExpense(expense("Kitchen cabinets", "30000.00", ExpenseCategory.EQUIPMENT, 3L, today.minusDays(14)));
        project2.addExpense(expense("Electrician, 10 hours", "8000.00", ExpenseCategory.LABOR, null, today.minusDays(3)));
        budgetRepository.save(project2);

        log.info("Inserted test data: budgets for project 1 and project 2");
    }

    private Expense expense(String description, String amount, ExpenseCategory category,
                            Long taskId, LocalDate date) {
        Expense e = new Expense();
        e.setDescription(description);
        e.setAmount(new BigDecimal(amount));
        e.setCategory(category);
        e.setTaskId(taskId);
        e.setSpentDate(date);
        return e;
    }
}