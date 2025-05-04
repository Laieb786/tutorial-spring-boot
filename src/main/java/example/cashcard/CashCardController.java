package example.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
import java.net.URI;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

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

    // Die @PostMapping-Annotation definiert einen Endpunkt für POST-Anfragen.
    // Diese Methode erstellt eine neue CashCard im Repository und gibt eine Antwort
    // mit dem HTTP-Status 201-CREATED und einem Location-Header zurück, der auf die
    // neu erstellte Ressource verweist.
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb) {
        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
        URI locationOfNewCashCard = ucb
                .path("/cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping()
    private ResponseEntity<Iterable<CashCard>> findAll() {
        return ResponseEntity.ok(cashCardRepository.findAll());
    }
}
