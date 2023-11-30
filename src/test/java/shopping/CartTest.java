package shopping;

import customer.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import product.Product;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    private final Customer customer = new Customer(1, "123");
    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart(customer);
    }

    /**
     * Проверяет добавление товара в корзину.<br>
     * Проверки:
     * <ul>
     *     <li>если единиц товара больше, чем добавляется в корзину, то товар должен добавиться в корзину</li>
     *     <li>если единиц товара меньше, чем добавляется в корзину, то должно быть выброшено исключение</li>
     *     <li>если единиц товара столько же, сколько добавляется в корзину (последний товар), то товар должен
     *     добавиться в корзину</li>
     *     <li>если в корзину добавляется ноль или меньше единиц товаров, то должно быть выброшено исключение</li>
     * </ul>
     */
    @Test
    void testAdd() {
        Product bread = new Product();
        bread.setName("Bread");
        bread.addCount(10);
        cart.add(bread, 5);
        assertEquals(1, cart.getProducts().size());
        assertTrue(cart.getProducts().containsKey(bread));
        assertEquals(5, cart.getProducts().get(bread));

        Product milk = new Product();
        milk.setName("Milk");
        milk.addCount(1);
        Exception notEnoughException = assertThrows(IllegalArgumentException.class, () -> cart.add(milk, 2));
        assertEquals("Невозможно добавить товар 'Milk' в корзину, т.к. нет необходимого количества товаров",
                notEnoughException.getMessage());

        Product cookie = new Product();
        cookie.setName("Cookie");
        cookie.addCount(1);
        cart.add(cookie, 1);
        assertEquals(2, cart.getProducts().size());
        assertTrue(cart.getProducts().containsKey(cookie));
        assertEquals(1, cart.getProducts().get(cookie));

        assertThrows(IllegalArgumentException.class, () -> cart.add(cookie, 0));
        // тут надо было бы проверить сообщение исключения, например, что нельзя добавить ноль товаров в корзину
        assertThrows(IllegalArgumentException.class, () -> cart.add(cookie, -10));
        // тут надо было бы проверить сообщение исключения, например, что нельзя добавить отрицательное кол-во товаров
        // в корзину
    }

}