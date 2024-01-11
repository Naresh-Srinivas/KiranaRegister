// ReportService.java
package example.kirana.services;

import example.kirana.models.Report;
import example.kirana.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);
    private final TransactionService transactionService;

    // Constructor injection for TransactionService dependency
    @Autowired
    public ReportService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Generate weekly reports based on transactions.
     *
     * @return List of weekly reports.
     */
    public List<Report> generateWeeklyReports() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        log.info("Fetched {} transactions for weekly report.", transactions.size());
        return generateReports("weekly");
    }

    /**
     * Generate monthly reports based on transactions.
     *
     * @return List of monthly reports.
     */
    public List<Report> generateMonthlyReports() {
        return generateReports("monthly");
    }

    /**
     * Generate yearly reports based on transactions.
     *
     * @return List of yearly reports.
     */
    public List<Report> generateYearlyReports() {
        return generateReports("yearly");
    }

    /**
     * Generate reports for the specified period.
     *
     * @param period The period for which reports should be generated ("weekly", "monthly", "yearly").
     * @return List of reports for the specified period.
     * @throws IllegalArgumentException if an invalid period is specified.
     */
    private List<Report> generateReports(String period) {
        List<Transaction> transactions = transactionService.getAllTransactions();
        LocalDate currentDate = LocalDate.now();

        switch (period.toLowerCase()) {
            case "weekly":
                return generateReport(transactions, currentDate.minusDays(7), currentDate, "Weekly Report");
            case "monthly":
                return generateReport(transactions, currentDate.minusMonths(1), currentDate, "Monthly Report");
            case "yearly":
                return generateReport(transactions, currentDate.minusYears(1), currentDate, "Yearly Report");
            default:
                throw new IllegalArgumentException("Invalid period specified: " + period);
        }
    }

    /**
     * Generate a report based on filtered transactions within a specified date range.
     *
     * @param transactions List of all transactions.
     * @param startDate    Start date of the report period.
     * @param endDate      End date of the report period.
     * @param periodName   Name of the report period.
     * @return List containing the generated report.
     */
    private List<Report> generateReport(List<Transaction> transactions, LocalDate startDate, LocalDate endDate, String periodName) {

        // Log the input parameters
        log.info("Generating report for period: {}, startDate: {}, endDate: {}", periodName, startDate, endDate);

        // Filter transactions based on date range
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(transaction -> !transaction.getTransactionDate().isBefore(startDate) && !transaction.getTransactionDate().isAfter(endDate))
                .collect(Collectors.toList());

        // Log the filtered transactions
        log.info("Filtered transactions for {}: {}", periodName, filteredTransactions);

        // Calculate total debit, total credit, and net flow
        BigDecimal totalDebit = filteredTransactions.stream()
                .filter(transaction -> "debit".equalsIgnoreCase(transaction.getType()))
                .map(Transaction::getConvertedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = filteredTransactions.stream()
                .filter(transaction -> "credit".equalsIgnoreCase(transaction.getType()))
                .map(Transaction::getConvertedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netFlow = totalCredit.subtract(totalDebit);

        // Create a report object
        Report report = new Report();
        report.setPeriod(periodName);
        report.setTotalDebit(totalDebit);
        report.setTotalCredit(totalCredit);
        report.setNetFlow(netFlow);

        return List.of(report);
    }
}
