package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    // ========== SPECIFICATION-BASED TESTS ==========

    // --- Amazon class tests (mock ShoppingCart) ---

    @Test
    @DisplayName("specification-based: calculate returns zero when cart is empty")
    void calculateReturnsZeroForEmptyCart() {
        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(mockCart.getItems()).thenReturn(Collections.emptyList());

        Amazon amazon = new Amazon(mockCart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        assertThat(amazon.calculate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based: calculate applies all price rules to cart items")
    void calculateAppliesAllRules() {
        ShoppingCart mockCart = mock(ShoppingCart.class);
        List<Item> items = Arrays.asList(
                new Item(ItemType.ELECTRONIC, "Phone", 1, 800.00));
        when(mockCart.getItems()).thenReturn(items);

        Amazon amazon = new Amazon(mockCart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        // regular = 800, delivery = 5, electronics = 7.50
        assertThat(amazon.calculate()).isCloseTo(812.50, within(0.01));
    }

    @Test
    @DisplayName("specification-based: addToCart delegates to ShoppingCart.add")
    void addToCartDelegatesToCart() {
        ShoppingCart mockCart = mock(ShoppingCart.class);
        Amazon amazon = new Amazon(mockCart, Collections.emptyList());

        Item item = new Item(ItemType.OTHER, "Pen", 1, 2.00);
        amazon.addToCart(item);

        verify(mockCart, times(1)).add(item);
    }

    @Test
    @DisplayName("specification-based: calculate with only non-electronic items has no electronics surcharge")
    void noElectronicsSurchargeForNonElectronicItems() {
        ShoppingCart mockCart = mock(ShoppingCart.class);
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "Book", 1, 20.00),
                new Item(ItemType.OTHER, "Pen", 2, 3.00));
        when(mockCart.getItems()).thenReturn(items);

        Amazon amazon = new Amazon(mockCart, Arrays.asList(
                new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

        // regular = 20 + 6 = 26, delivery = 5 (2 items), electronics = 0
        assertThat(amazon.calculate()).isCloseTo(31.00, within(0.01));
    }

    // --- RegularCost tests ---

    @Test
    @DisplayName("specification-based: RegularCost returns zero for empty list")
    void regularCostEmptyList() {
        RegularCost rule = new RegularCost();
        assertThat(rule.priceToAggregate(Collections.emptyList())).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based: RegularCost sums quantity times price for each item")
    void regularCostSumsCorrectly() {
        RegularCost rule = new RegularCost();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "A", 2, 10.00),
                new Item(ItemType.ELECTRONIC, "B", 3, 5.00));

        assertThat(rule.priceToAggregate(items)).isCloseTo(35.00, within(0.01));
    }

    // --- DeliveryPrice tests ---

    @Test
    @DisplayName("specification-based: DeliveryPrice returns 0 for empty cart")
    void deliveryPriceEmptyCart() {
        DeliveryPrice rule = new DeliveryPrice();
        assertThat(rule.priceToAggregate(Collections.emptyList())).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based: DeliveryPrice returns 5 for 1 to 3 items")
    void deliveryPriceSmallCart() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "A", 1, 1.00),
                new Item(ItemType.OTHER, "B", 1, 1.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(5.0);
    }

    @Test
    @DisplayName("specification-based: DeliveryPrice returns 12.50 for 4 to 10 items")
    void deliveryPriceMediumCart() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "A", 1, 1.00),
                new Item(ItemType.OTHER, "B", 1, 1.00),
                new Item(ItemType.OTHER, "C", 1, 1.00),
                new Item(ItemType.OTHER, "D", 1, 1.00),
                new Item(ItemType.OTHER, "E", 1, 1.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(12.5);
    }

    @Test
    @DisplayName("specification-based: DeliveryPrice returns 20 for 11+ items")
    void deliveryPriceLargeCart() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "A", 1, 1.00),
                new Item(ItemType.OTHER, "B", 1, 1.00),
                new Item(ItemType.OTHER, "C", 1, 1.00),
                new Item(ItemType.OTHER, "D", 1, 1.00),
                new Item(ItemType.OTHER, "E", 1, 1.00),
                new Item(ItemType.OTHER, "F", 1, 1.00),
                new Item(ItemType.OTHER, "G", 1, 1.00),
                new Item(ItemType.OTHER, "H", 1, 1.00),
                new Item(ItemType.OTHER, "I", 1, 1.00),
                new Item(ItemType.OTHER, "J", 1, 1.00),
                new Item(ItemType.OTHER, "K", 1, 1.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(20.0);
    }

    // --- ExtraCostForElectronics tests ---

    @Test
    @DisplayName("specification-based: ExtraCostForElectronics returns 7.50 when electronic item present")
    void electronicsSurchargeApplied() {
        ExtraCostForElectronics rule = new ExtraCostForElectronics();
        List<Item> items = Arrays.asList(
                new Item(ItemType.ELECTRONIC, "Phone", 1, 500.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(7.50);
    }

    @Test
    @DisplayName("specification-based: ExtraCostForElectronics returns 0 when no electronic item")
    void noElectronicsSurcharge() {
        ExtraCostForElectronics rule = new ExtraCostForElectronics();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "Book", 1, 20.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based: ExtraCostForElectronics returns 0 for empty cart")
    void electronicsSurchargeEmptyCart() {
        ExtraCostForElectronics rule = new ExtraCostForElectronics();
        assertThat(rule.priceToAggregate(Collections.emptyList())).isEqualTo(0.0);
    }

    // --- Item tests ---

    @Test
    @DisplayName("specification-based: Item getters return correct values")
    void itemGettersReturnCorrectValues() {
        Item item = new Item(ItemType.ELECTRONIC, "Laptop", 2, 999.99);

        assertThat(item.getType()).isEqualTo(ItemType.ELECTRONIC);
        assertThat(item.getName()).isEqualTo("Laptop");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getPricePerUnit()).isEqualTo(999.99);
    }

    // ========== STRUCTURAL-BASED TESTS ==========

    @Test
    @DisplayName("structural-based: Amazon.calculate calls getItems on cart for each rule")
    void calculateCallsGetItemsForEachRule() {
        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(mockCart.getItems()).thenReturn(Collections.emptyList());

        PriceRule rule1 = mock(PriceRule.class);
        PriceRule rule2 = mock(PriceRule.class);
        when(rule1.priceToAggregate(anyList())).thenReturn(10.0);
        when(rule2.priceToAggregate(anyList())).thenReturn(5.0);

        Amazon amazon = new Amazon(mockCart, Arrays.asList(rule1, rule2));
        double total = amazon.calculate();

        assertThat(total).isCloseTo(15.0, within(0.01));
        verify(rule1, times(1)).priceToAggregate(anyList());
        verify(rule2, times(1)).priceToAggregate(anyList());
    }

    @Test
    @DisplayName("structural-based: Amazon.calculate with single mocked rule returns that rule's value")
    void calculateWithSingleMockedRule() {
        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(mockCart.getItems()).thenReturn(Collections.emptyList());

        PriceRule mockRule = mock(PriceRule.class);
        when(mockRule.priceToAggregate(anyList())).thenReturn(42.0);

        Amazon amazon = new Amazon(mockCart, Arrays.asList(mockRule));

        assertThat(amazon.calculate()).isCloseTo(42.0, within(0.01));
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice boundary - exactly 1 item returns $5")
    void deliveryPriceBoundaryOneItem() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "A", 1, 1.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(5.0);
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice boundary - exactly 3 items returns $5")
    void deliveryPriceBoundaryThreeItems() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "A", 1, 1.00),
                new Item(ItemType.OTHER, "B", 1, 1.00),
                new Item(ItemType.OTHER, "C", 1, 1.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(5.0);
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice boundary - exactly 4 items returns $12.50")
    void deliveryPriceBoundaryFourItems() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "A", 1, 1.00),
                new Item(ItemType.OTHER, "B", 1, 1.00),
                new Item(ItemType.OTHER, "C", 1, 1.00),
                new Item(ItemType.OTHER, "D", 1, 1.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(12.5);
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice boundary - exactly 10 items returns $12.50")
    void deliveryPriceBoundaryTenItems() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "A", 1, 1.00),
                new Item(ItemType.OTHER, "B", 1, 1.00),
                new Item(ItemType.OTHER, "C", 1, 1.00),
                new Item(ItemType.OTHER, "D", 1, 1.00),
                new Item(ItemType.OTHER, "E", 1, 1.00),
                new Item(ItemType.OTHER, "F", 1, 1.00),
                new Item(ItemType.OTHER, "G", 1, 1.00),
                new Item(ItemType.OTHER, "H", 1, 1.00),
                new Item(ItemType.OTHER, "I", 1, 1.00),
                new Item(ItemType.OTHER, "J", 1, 1.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(12.5);
    }

    @Test
    @DisplayName("structural-based: ExtraCostForElectronics with mixed types still returns 7.50")
    void electronicsSurchargeWithMixedTypes() {
        ExtraCostForElectronics rule = new ExtraCostForElectronics();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "Book", 1, 10.00),
                new Item(ItemType.ELECTRONIC, "Cable", 1, 5.00),
                new Item(ItemType.OTHER, "Pen", 1, 2.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(7.50);
    }

    @Test
    @DisplayName("structural-based: RegularCost handles item with zero quantity")
    void regularCostZeroQuantity() {
        RegularCost rule = new RegularCost();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "Ghost", 0, 100.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("structural-based: RegularCost handles item with zero price")
    void regularCostZeroPrice() {
        RegularCost rule = new RegularCost();
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "FreeItem", 5, 0.00));

        assertThat(rule.priceToAggregate(items)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("structural-based: multiple addToCart calls invoke cart.add each time")
    void multipleAddToCartCalls() {
        ShoppingCart mockCart = mock(ShoppingCart.class);
        Amazon amazon = new Amazon(mockCart, Collections.emptyList());

        Item item1 = new Item(ItemType.OTHER, "A", 1, 1.00);
        Item item2 = new Item(ItemType.ELECTRONIC, "B", 1, 2.00);
        Item item3 = new Item(ItemType.OTHER, "C", 1, 3.00);

        amazon.addToCart(item1);
        amazon.addToCart(item2);
        amazon.addToCart(item3);

        verify(mockCart, times(3)).add(any(Item.class));
        verify(mockCart).add(item1);
        verify(mockCart).add(item2);
        verify(mockCart).add(item3);
    }
}