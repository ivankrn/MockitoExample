package shopping;

import customer.Customer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingServiceImplTest {

    private final ProductDao productDao = Mockito.mock(ProductDao.class);

    private final ShoppingService shoppingService = new ShoppingServiceImpl(productDao);

    /**
     * Проверяет, что при получении корзины покупателя возвращается актуальная корзина покупателя.<br>
     * <b>Данный тест не пройдет, т.к. каждый раз возвращается новая корзина покупателя, а не уже собранная.</b><br>
     * Наверно данный тест плохой, так как неизвестно, какое поведение у системы должно быть. Если абстрагироваться и
     * представить, что пользователь на ПК добавил себе в корзину товар, то если данный пользователь зайдет с мобильного
     * устройства, то его корзина должна быть сохранена. В данном же случае, каждое получение корзины покупателя
     * возвращает новую корзину, поэтому данные не согласованы.<br>
     * Возможно данный тест не нужен, так как {@link ShoppingServiceImpl#getCart(Customer)} фактически переносит
     * ответственность на класс {@link Cart}
     */
    @Test
    void testGetCart() {
        Customer customer = new Customer(1L, "123");
        Cart firstGetCart = shoppingService.getCart(customer);
        Cart secondGetCard = shoppingService.getCart(customer);
        // проверяем, что если ничего не изменили, то должна вернуться та же корзина
        assertEquals(firstGetCart, secondGetCard);
        // возможно лучше проверить содержимое (вдруг разные лишь ссылки на объекты, а не их содержимое?)
        assertEquals(firstGetCart.getProducts().size(), secondGetCard.getProducts().size());

        Product bread = new Product();
        bread.setName("Bread");
        bread.addCount(10);
        firstGetCart.add(bread, 1);
        Cart thirdGetCard = shoppingService.getCart(customer);
        // проверяем, что если добавили в корзину товар, то при получении корзины должна вернуться та же корзина
        assertEquals(firstGetCart, thirdGetCard);
        // возможно лучше проверить содержимое (вдруг разные лишь ссылки на объекты, а не их содержимое?)
        assertEquals(firstGetCart.getProducts().get(bread), secondGetCard.getProducts().get(bread));
    }

    /**
     * Не нужен, так как {@link ShoppingServiceImpl#getAllProducts()} переносит ответственность на {@link ProductDao}
     */
    @Test
    void testGetAllProducts() {
    }

    /**
     * Не нужен, так как {@link ShoppingServiceImpl#getProductByName(String)} переносит ответственность на
     * {@link ProductDao}
     */
    @Test
    void testGetProductByName() {
    }

    /**
     * <p>Проверяет совершение покупки.</p>
     * Проверки:
     * <ul>
     *     <li>при пустой корзине покупка не должна произойти</li>
     *     <li>при непустой корзине покупка должна произойти, и при этом количество продукта должно уменьшиться</li>
     *     <li>если товар закончился, то должно быть выброшено исключение {@link BuyException}</li>
     * </ul>
     *
     * @throws BuyException при ошибке покупки
     */
    @Test
    void testBuy() throws BuyException {
        Customer customer = new Customer(1L, "123");
        Cart cart = shoppingService.getCart(customer);
        // проверка при пустой корзине
        assertFalse(shoppingService.buy(cart));

        Product cookie = new Product();
        cookie.setName("Cookie");
        cookie.addCount(10);
        cart.add(cookie, 1);
        // проверка при непустой корзине
        assertTrue(shoppingService.buy(cart));
        Mockito.verify(productDao).save(Mockito.argThat((Product product) ->
                product.getName().equals("Cookie") && product.getCount() == 9));
        /* Вопрос: возможно проще проверить на сохранение самого объекта, но вдруг будет сохранен тот же объект, но по
        другой ссылке, или вдруг количество продукта даже не уменьшится? */
        Mockito.verify(productDao).save(cookie);

        Customer fastestHandsInTheWildWest = new Customer(2L, "124");
        Cart fastestHandsCart = shoppingService.getCart(fastestHandsInTheWildWest);
        fastestHandsCart.add(cookie, 5);
        cart.edit(cookie, 5);
        shoppingService.buy(fastestHandsCart);
        // проверка в случае если товар закончился
        Exception exception = assertThrows(BuyException.class, () -> shoppingService.buy(cart));
        assertEquals("В наличии нет необходимого количества товара Cookie", exception.getMessage());
    }

    /**
     * Проверяет возможность покупки последнего товара.<br>
     * <b>Данный тест не пройдет.</b> Выделил в отдельный из-за того, что обнаружил что реализация метода validateCount()
     * в Cart не позволяет купить последний товар (т.к. в Cart метод validateCount() сравнивает количество по <= 0).
     *
     * @throws BuyException при ошибке покупки
     */
    @Test
    void testBuyLastProduct() throws BuyException {
        Customer customer = new Customer(1L, "123");
        Product cookie = new Product();
        cookie.setName("Cookie");
        cookie.addCount(1);
        Cart cart = shoppingService.getCart(customer);
        cart.add(cookie, 1);

        assertTrue(shoppingService.buy(cart));
        Mockito.verify(productDao).save(Mockito.argThat((Product product) ->
                product.getName().equals("Cookie") && product.getCount() == 0));
    }

}