package example.kirana.repositories;

import example.kirana.models.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionsRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
}
