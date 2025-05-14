package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
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

    // Diese Methode gibt eine Seite von CashCards zurück, basierend auf den
    // angegebenen Seitennummer und der Seitengröße.
    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
        Page<CashCard> page = cashCardRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))));

        return ResponseEntity.ok(page.getContent());
    }
}
