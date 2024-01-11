package example.kirana.services;

import example.kirana.models.Transaction;
import example.kirana.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionsRepository transactionRepository;

    @Autowired
    private CurrencyConversionService currencyConversionService;

    /**
     * Get all transactions.
     *
     * @return List of all transactions.
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Create a transaction.
     *
     * @param transaction The transaction to be created.
     * @return The created transaction.
     */
    public Transaction createTransaction(Transaction transaction) {
        // Implement validation or business logic if needed

        // Convert amount to common currency before saving
        BigDecimal commonCurrencyAmount = convertToCommonCurrency(transaction.getCurrency(), transaction.getOriginalAmount());
        transaction.setConvertedAmount(commonCurrencyAmount);

        return transactionRepository.save(transaction);
    }

    /**
     * Create a transaction by an employee (restricted to credit transactions).
     *
     * @param transaction The transaction to be created.
     * @return The created transaction.
     */
    public Transaction createTransactionByEmployee(Transaction transaction) {
        // Employees can only add credit transactions
        if ("credit".equalsIgnoreCase(transaction.getType())) {
            // Implement validation or business logic if needed for employee-specific transactions

            // Convert amount to common currency before saving
            BigDecimal commonCurrencyAmount = convertToCommonCurrency(transaction.getCurrency(), transaction.getOriginalAmount());
            transaction.setConvertedAmount(commonCurrencyAmount);

            return transactionRepository.save(transaction);
        }
        // Handle the case where an employee tries to add a debit transaction
        return null;
    }

    /**
     * Update a transaction by its ID.
     *
     * @param transactionId       The ID of the transaction to be updated.
     * @param updatedTransaction  The updated transaction data.
     * @return The updated transaction.
     */
    public Transaction updateTransaction(String transactionId, Transaction updatedTransaction) {
        // Implement validation or business logic if needed
        Transaction existingTransaction = transactionRepository.findById(transactionId).orElse(null);
        if (existingTransaction != null) {
            // Update transaction properties
            existingTransaction.setTransactionDate(updatedTransaction.getTransactionDate());
            existingTransaction.setCurrency(updatedTransaction.getCurrency());
            existingTransaction.setOriginalAmount(updatedTransaction.getOriginalAmount());
            existingTransaction.setConvertedAmount(updatedTransaction.getConvertedAmount());
            existingTransaction.setType(updatedTransaction.getType());

            return transactionRepository.save(existingTransaction);
        }
        return null; // Or throw an exception indicating transaction not found
    }

    /**
     * Delete a transaction by its ID.
     *
     * @param transactionId The ID of the transaction to be deleted.
     */
    public void deleteTransaction(String transactionId) {
        // Implement validation or business logic if needed
        transactionRepository.deleteById(transactionId);
    }

    /**
     * Convert an amount from the source currency to a common currency.
     *
     * @param sourceCurrency The source currency code.
     * @param amount         The amount to be converted.
     * @return The converted amount.
     */
    private BigDecimal convertToCommonCurrency(String sourceCurrency, BigDecimal amount) {
        String commonCurrency = "INR"; // Choose your common currency
        BigDecimal conversionRate = currencyConversionService.getConversionRate(sourceCurrency, commonCurrency);
        return amount.multiply(conversionRate);
    }

}
