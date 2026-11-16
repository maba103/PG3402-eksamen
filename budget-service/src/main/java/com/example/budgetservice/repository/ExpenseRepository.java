package com.example.budgetservice.repository;

import com.example.budgetservice.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByBudgetIdOrderBySpentDateDesc(Long budgetId);

    Optional<Expense> findByIdAndBudgetId(Long id, Long budgetId);
}