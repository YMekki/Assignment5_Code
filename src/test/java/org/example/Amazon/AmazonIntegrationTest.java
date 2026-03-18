package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class AmazonIntegrationTest {

    private static Database db;
    private ShoppingCartAdaptor cart;

    @BeforeAll
    static void setupDatabase() {
        db = new Database();
    }

    @BeforeEach
    void resetAndPrepare() {
        db.resetDatabase();
        cart = new ShoppingCartAdaptor(db);
    }

    @AfterAll
    static void tearDown() {
        db.close();
    }

    // ========== SPECIFICATION-BASED TESTS ==========

    @Test
    @DisplayName("specification-based: empty cart returns zero total")
    void emptyCartReturnsZero() {
        Amazon amazon = new Amazon(cart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        assertThat(amazon.calculate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based: single non-electronic item calculates regular cost plus delivery")
    void singleNonElectronicItem() {
        Amazon amazon = new Amazon(cart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        amazon.addToCart(new Item(ItemType.OTHER, "Book", 1, 10.00));

        // regular cost = 10.00, delivery = 5 (1 item), electronics surcharge = 0
        assertThat(amazon.calculate()).isCloseTo(15.00, within(0.01));
    }

    @Test
    @DisplayName("specification-based: single electronic item includes electronics surcharge")
    void singleElectronicItem() {
        Amazon amazon = new Amazon(cart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Laptop", 1, 999.99));

        // regular cost = 999.99, delivery = 5 (1 item), electronics = 7.50
        assertThat(amazon.calculate()).isCloseTo(1012.49, within(0.01));
    }

    @Test
    @DisplayName("specification-based: multiple items in 1-3 range use $5 delivery")
    void multipleItemsSmallDelivery() {
        Amazon amazon = new Amazon(cart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        amazon.addToCart(new Item(ItemType.OTHER, "Pen", 2, 1.50));
        amazon.addToCart(new Item(ItemType.OTHER, "Notebook", 3, 5.00));
        amazon.addToCart(new Item(ItemType.OTHER, "Eraser", 1, 0.75));

        // regular = (2*1.50) + (3*5.00) + (1*0.75) = 3 + 15 + 0.75 = 18.75
        // delivery = 5 (3 items), electronics = 0
        assertThat(amazon.calculate()).isCloseTo(23.75, within(0.01));
    }

    @Test
    @DisplayName("specification-based: 4-10 items use $12.50 delivery")
    void mediumDeliveryTier() {
        Amazon amazon = new Amazon(cart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        for (int i = 0; i < 5; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 2.00));
        }

        // regular = 5 * 2.00 = 10.00, delivery = 12.50 (5 items), electronics = 0
        assertThat(amazon.calculate()).isCloseTo(22.50, within(0.01));
    }

    @Test
    @DisplayName("specification-based: 11+ items use $20 delivery")
    void largeDeliveryTier() {
        Amazon amazon = new Amazon(cart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        for (int i = 0; i < 11; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 1.00));
        }

        // regular = 11 * 1.00 = 11.00, delivery = 20.00 (11 items), electronics = 0
        assertThat(amazon.calculate()).isCloseTo(31.00, within(0.01));
    }

    @Test
    @DisplayName("specification-based: mixed electronic and non-electronic items apply electronics surcharge once")
    void mixedItemTypesElectronicsSurchargeAppliedOnce() {
        Amazon amazon = new Amazon(cart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Phone", 1, 500.00));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Charger", 1, 25.00));
        amazon.addToCart(new Item(ItemType.OTHER, "Case", 1, 15.00));

        // regular = 500 + 25 + 15 = 540, delivery = 5 (3 items), electronics = 7.50
        assertThat(amazon.calculate()).isCloseTo(552.50, within(0.01));
    }

    @Test
    @DisplayName("specification-based: item with quantity > 1 multiplies price correctly")
    void quantityMultipliesPrice() {
        Amazon amazon = new Amazon(cart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        amazon.addToCart(new Item(ItemType.OTHER, "T-Shirt", 5, 12.00));

        // regular = 5 * 12.00 = 60.00, delivery = 5 (1 item in cart), electronics = 0
        assertThat(amazon.calculate()).isCloseTo(65.00, within(0.01));
    }

    @Test
    @DisplayName("specification-based: addToCart persists item to database and is retrievable")
    void addToCartPersistsToDatabase() {
        Amazon amazon = new Amazon(cart, Arrays.asList(new RegularCost()));

        amazon.addToCart(new Item(ItemType.OTHER, "Widget", 3, 7.00));

        List<Item> items = cart.getItems();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Widget");
        assertThat(items.get(0).getQuantity()).isEqualTo(3);
        assertThat(items.get(0).getPricePerUnit()).isEqualTo(7.00);
        assertThat(items.get(0).getType()).isEqualTo(ItemType.OTHER);
    }

    // ========== STRUCTURAL-BASED TESTS ==========

    @Test
    @DisplayName("structural-based: calculate iterates all rules and sums results")
    void calculateSumsAllRules() {
        Amazon amazon = new Amazon(cart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Tablet", 2, 200.00));

        // regular = 2 * 200 = 400, delivery = 5 (1 item), electronics = 7.50
        double total = amazon.calculate();
        assertThat(total).isCloseTo(412.50, within(0.01));
    }

    @Test
    @DisplayName("structural-based: calculate with no rules returns zero")
    void calculateWithNoRules() {
        Amazon amazon = new Amazon(cart, Collections.emptyList());

        amazon.addToCart(new Item(ItemType.OTHER, "Book", 1, 20.00));

        assertThat(amazon.calculate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("structural-based: calculate with only RegularCost rule")
    void calculateWithOnlyRegularCost() {
        Amazon amazon = new Amazon(cart, Arrays.asList(new RegularCost()));

        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Mouse", 1, 30.00));

        assertThat(amazon.calculate()).isCloseTo(30.00, within(0.01));
    }

    @Test
    @DisplayName("structural-based: database reset clears all cart items")
    void databaseResetClearsCart() {
        cart.add(new Item(ItemType.OTHER, "OldItem", 1, 5.00));
        assertThat(cart.getItems()).hasSize(1);

        db.resetDatabase();
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("structural-based: ShoppingCartAdaptor stores and retrieves multiple items in order")
    void adapterStoresMultipleItems() {
        cart.add(new Item(ItemType.ELECTRONIC, "Keyboard", 1, 50.00));
        cart.add(new Item(ItemType.OTHER, "Mousepad", 2, 10.00));
        cart.add(new Item(ItemType.ELECTRONIC, "Monitor", 1, 300.00));

        List<Item> items = cart.getItems();
        assertThat(items).hasSize(3);
        assertThat(items).extracting(Item::getName)
                .containsExactly("Keyboard", "Mousepad", "Monitor");
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice boundary at exactly 4 items returns $12.50")
    void deliveryPriceBoundaryAtFourItems() {
        Amazon amazon = new Amazon(cart, Arrays.asList(new DeliveryPrice()));

        for (int i = 0; i < 4; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 1.00));
        }

        assertThat(amazon.calculate()).isCloseTo(12.50, within(0.01));
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice boundary at exactly 10 items returns $12.50")
    void deliveryPriceBoundaryAtTenItems() {
        Amazon amazon = new Amazon(cart, Arrays.asList(new DeliveryPrice()));

        for (int i = 0; i < 10; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 1.00));
        }

        assertThat(amazon.calculate()).isCloseTo(12.50, within(0.01));
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice boundary at exactly 11 items returns $20")
    void deliveryPriceBoundaryAtElevenItems() {
        Amazon amazon = new Amazon(cart, Arrays.asList(new DeliveryPrice()));

        for (int i = 0; i < 11; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 1.00));
        }

        assertThat(amazon.calculate()).isCloseTo(20.00, within(0.01));
    }

    @Test
    @DisplayName("structural-based: full end-to-end flow with add, calculate, reset, recalculate")
    void fullEndToEndFlow() {
        Amazon amazon = new Amazon(cart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Speaker", 1, 80.00));
        double firstTotal = amazon.calculate();
        // regular = 80, delivery = 5, electronics = 7.50
        assertThat(firstTotal).isCloseTo(92.50, within(0.01));

        db.resetDatabase();
        double afterReset = amazon.calculate();
        assertThat(afterReset).isEqualTo(0.0);

        amazon.addToCart(new Item(ItemType.OTHER, "Book", 2, 15.00));
        double secondTotal = amazon.calculate();
        // regular = 30, delivery = 5, electronics = 0
        assertThat(secondTotal).isCloseTo(35.00, within(0.01));
    }
}