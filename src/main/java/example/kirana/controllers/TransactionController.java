package example.kirana.controllers;

import example.kirana.models.Transaction;
import example.kirana.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling transaction-related endpoints in the Kirana application.
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Constructs a new TransactionController with the specified TransactionService.
     *
     * @param transactionService The service responsible for managing transactions.
     */
    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Retrieves all transactions.
     *
     * @return List of all transactions.
     */
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    /**
     * Creates a new transaction by an admin user.
     *
     * @param transaction The transaction details to create.
     * @return The created transaction.
     */
    @PostMapping("/admin")
    public Transaction createTransactionByAdmin(@RequestBody Transaction transaction) {
        return transactionService.createTransaction(transaction);
    }

    /**
     * Creates a new transaction by an employee user.
     *
     * @param transaction The transaction details to create.
     * @return The created transaction.
     */
    @PostMapping("/employee")
    public Transaction createTransactionByEmployee(@RequestBody Transaction transaction) {
        return transactionService.createTransactionByEmployee(transaction);
    }

    /**
     * Updates an existing transaction.
     *
     * @param transactionId The ID of the transaction to update.
     * @param transaction   The updated transaction details.
     * @return The updated transaction.
     */
    @PutMapping("/{transactionId}")
    public Transaction updateTransaction(@PathVariable String transactionId, @RequestBody Transaction transaction) {
        return transactionService.updateTransaction(transactionId, transaction);
    }

    /**
     * Deletes a transaction by its ID.
     *
     * @param transactionId The ID of the transaction to delete.
     */
    @DeleteMapping("/{transactionId}")
    public void deleteTransaction(@PathVariable String transactionId) {
        transactionService.deleteTransaction(transactionId);
    }
}
