package example.cashcard;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

//Erstellen der Klasse CashCardJsonTest
//Die Klasse ist mit @JsonTest annotiert, um die JSON-Serialisierung und -Deserialisierung zu testen
@JsonTest
class CashCardJsonTest {

        @Autowired
        private JacksonTester<CashCard> json;

        @Autowired
        private JacksonTester<CashCard[]> jsonList;

        private CashCard[] cashCards;

        // Die Methode setUp() wird vor jedem Testfall ausgeführt und initialisiert die
        // cashCards-Variable mit einem Array von CashCard-Objekten
        // Diese Methode wird mit der @BeforeEach-Annotation versehen, um
        // sicherzustellen, dass sie vor jedem Testfall ausgeführt wird
        @BeforeEach
        void setUp() {
                cashCards = Arrays.array(
                                new CashCard(99L, 123.45),
                                new CashCard(100L, 1.00),
                                new CashCard(101L, 150.00));
        }

        // Die Methode cashCardListSerializationTest() testet die Serialisierung des
        // cashCards-Arrays in JSON-Format
        // Die Methode verwendet die JacksonTester-Instanz, um das cashCards-Array in
        // JSON zu serialisieren und vergleicht das Ergebnis mit einer erwarteten
        // JSON-Datei (list.json)
        @Test
        void cashCardListSerializationTest() throws IOException {
                String jsonOutput = jsonList.write(cashCards).getJson();
                System.out.println("Serialisierte CashCards als JSON:");
                System.out.println(jsonOutput);

                assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
        }

        // Die Methode cashCardListDeserializationTest() testet die Deserialisierung
        // eines JSON-Strings in ein CashCard-Array
        // Die Methode verwendet die JacksonTester-Instanz, um den JSON-String in ein
        // CashCard-Array zu deserialisieren und vergleicht das Ergebnis mit dem
        // ursprünglichen cashCards-Array
        @Test
        void cashCardListDeserializationTest() throws IOException {
                String expected = """
                                [
                                        { "id": 99, "amount": 123.45 },
                                        { "id": 100, "amount": 1.00 },
                                        { "id": 101, "amount": 150.00 }
                                ]
                                """;

                CashCard[] deserializedCards = jsonList.parse(expected).getObject();
                System.out.println("Deserialisierte CashCards:");
                for (int i = 0; i < deserializedCards.length; i++) {
                        System.out.println("CashCard " + i + ": id=" + deserializedCards[i].id() +
                                        ", amount=" + deserializedCards[i].amount());
                }

                System.out.println("\nOriginale CashCards:");
                for (int i = 0; i < cashCards.length; i++) {
                        System.out.println("CashCard " + i + ": id=" + cashCards[i].id() +
                                        ", amount=" + cashCards[i].amount());
                }

                assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
        }

        // Die Methode cashCardSerializationTest() testet die Serialisierung eines
        // einzelnen CashCard-Objekts in JSON-Format
        // Die Methode verwendet die JacksonTester-Instanz, um das CashCard-Objekt in
        // JSON zu serialisieren und vergleicht das Ergebnis mit einer erwarteten
        // JSON-Datei (single.json)
        @Test
        void cashCardSerializationTest() throws IOException {
                CashCard cashCard = cashCards[0];
                assertThat(json.write(cashCard)).isStrictlyEqualToJson("single.json");
                assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id");
                assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id")
                                .isEqualTo(99);
                assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");
                assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount")
                                .isEqualTo(123.45);
        }

        // Die Methode cashCardDeserializationTest() testet die Deserialisierung eines
        // JSON-Strings in ein CashCard-Objekt
        // Die Methode verwendet die JacksonTester-Instanz, um den JSON-String in ein
        // CashCard-Objekt zu deserialisieren und vergleicht das Ergebnis mit einem
        // erwarteten CashCard-Objekt
        // Die Methode gibt auch die ID und den Betrag des deserialisierten
        // CashCard-Objekts aus
        @Test
        void cashCardDeserializationTest() throws IOException {
                String expected = """
                                {
                                    "id": 99,
                                    "amount": 123.45
                                }
                                """;
                assertThat(json.parse(expected))
                                .isEqualTo(new CashCard(99L, 123.45));
                assertThat(json.parseObject(expected).id()).isEqualTo(99);
                assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
        }
}