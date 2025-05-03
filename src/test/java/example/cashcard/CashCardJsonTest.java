package example.cashcard;

//Importieren der nötigen Packages
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

        @BeforeEach
        void setUp() {
                cashCards = Arrays.array(
                                new CashCard(99L, 123.45),
                                new CashCard(100L, 1.00),
                                new CashCard(101L, 150.00));
        }

        @Test
        void cashCardListSerializationTest() throws IOException {
                // JSON-String ausgeben, bevor die Assertion ausgeführt wird
                String jsonOutput = jsonList.write(cashCards).getJson();
                System.out.println("Serialisierte CashCards als JSON:");
                System.out.println(jsonOutput);

                assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
        }

        @Test
        void cashCardListDeserializationTest() throws IOException {
                String expected = """
                                [
                                        { "id": 99, "amount": 123.45 },
                                        { "id": 100, "amount": 1.00 },
                                        { "id": 101, "amount": 150.00 }
                                ]
                                """;

                // Ergebnis der Deserialisierung speichern und ausgeben
                CashCard[] deserializedCards = jsonList.parse(expected).getObject();
                System.out.println("Deserialisierte CashCards:");
                for (int i = 0; i < deserializedCards.length; i++) {
                        System.out.println("CashCard " + i + ": id=" + deserializedCards[i].id() +
                                        ", amount=" + deserializedCards[i].amount());
                }

                // Original cashCards zum Vergleich ausgeben
                System.out.println("\nOriginale CashCards:");
                for (int i = 0; i < cashCards.length; i++) {
                        System.out.println("CashCard " + i + ": id=" + cashCards[i].id() +
                                        ", amount=" + cashCards[i].amount());
                }

                // Die ursprüngliche Assertion beibehalten
                assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
        }

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