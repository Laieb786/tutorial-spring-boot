package example.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

//Die @RestController-Annotation kennzeichnet diese Klasse als Controller für REST-Anfragen
//Die @RequestMapping-Annotation legt fest, dass alle Anfragen an diesen Controller mit "/cashcards" beginnen
@RestController
@RequestMapping("/cashcards")

public class CashCardController {
    // Die CashCardRepository-Instanz wird hier als Abhängigkeit injiziert
    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    // Die @GetMapping-Annotation definiert einen Endpunkt für GET-Anfragen mit
    // einer Pfadvariable (requestedId).
    // Diese Methode sucht eine CashCard anhand ihrer ID im Repository
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
