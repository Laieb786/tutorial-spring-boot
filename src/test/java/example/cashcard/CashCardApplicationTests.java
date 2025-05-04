package example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.*;

//Die Klasse CashCardApplicationTests ist mit @SpringBootTest annotiert, um den gesamten Anwendungskontext zu laden und die Anwendung in einem zufälligen Port zu starten.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class CashCardApplicationTests {
    // Die Annotation @Autowired wird verwendet, um die TestRestTemplate-Instanz zu
    // injizieren, die für die Durchführung von HTTP-Anfragen an die Anwendung
    // verwendet wird.
    @Autowired
    TestRestTemplate restTemplate;

    // Dies ist ein Test für den GET-Endpunkt, der bei einer Abfrage einer bekannten
    // ID (99) für eine CashCard den HTTP-Status 200-OK zurückgeben soll, sowie
    // korrekte JSON-Daten mit id=99 und amount=123.45.
    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
    }

    // Dies ist ein Test für den GET-Endpunkt, der bei einer Abfrage einer
    // unbekannten ID (hier 1000) einen HTTP-Status 404-Fehler zurückgeben soll.
    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    // Dies ist ein Test für den POST-Endpunkt, der eine neue CashCard mit einem
    // Betrag von 250.0 erstellen soll. Der Test überprüft, ob der HTTP-Status
    // 201-CREATED zurückgegeben wird und ob die Location-Header-Informationen
    // korrekt sind. Anschließend wird die neu erstellte CashCard abgerufen und
    // überprüft, ob die ID nicht null ist und der Betrag korrekt ist.
    @Test
    // @DirtiesContext
    void shouldCreateANewCashCard() {
        CashCard newCashCard = new CashCard(null, 250.00);
        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", newCashCard, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCashCard, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");
        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);
    }

    // Dies ist ein Test für den GET-Endpunkt, der alle CashCards zurückgeben soll.
    // Der Test überprüft, ob der HTTP-Status 200-OK zurückgegeben wird und ob die
    // Anzahl der CashCards 3 beträgt. Anschließend werden die IDs und Beträge der
    // CashCards überprüft, um sicherzustellen, dass sie den erwarteten Werten
    // entsprechen.
    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int cashCardCount = documentContext.read("$.length()");
        assertThat(cashCardCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.00, 150.00);
    }
}
