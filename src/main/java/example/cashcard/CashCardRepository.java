package example.cashcard;

import org.springframework.data.repository.CrudRepository;

public interface CashCardRepository extends CrudRepository<CashCard, Long> {
    // This interface will automatically provide CRUD operations for CashCard
    // entities
    // No need to implement any methods, Spring Data JPA will handle it for us

}