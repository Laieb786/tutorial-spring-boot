package example.cashcard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
    // This interface will automatically provide CRUD operations for CashCard
    // entities
    // No need to implement any methods, Spring Data JPA will handle it for us

}
