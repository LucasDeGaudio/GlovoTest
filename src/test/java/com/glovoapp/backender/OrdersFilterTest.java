package com.glovoapp.backender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OrdersFilterTest {

  @Autowired
  private OrdersFilter ordersFilter;

  @Autowired
  private CourierRepository courierRepository;

  /**
   * Test that the description order contains special word, and the courier
   * does not have box. So, no order is return.
   */
  @Test
  void analyzeDescriptionAndDiscard() {

    Courier courier = courierRepository.findById("courier-2");

    OrderRepository orderRepositoryMock = mock(OrderRepository.class);

    ReflectionTestUtils.setField(ordersFilter, "orderRepository", orderRepositoryMock);

    Order expected = new Order().withId("order-2")
        .withDescription("I want a pizza cut into very small slices")
        .withFood(true)
        .withVip(false)
        .withPickup(new Location(41.3965463, 2.1963997))
        .withDelivery(new Location(41.407834, 2.1675979));

    List<Order> orders = new ArrayList<>();

    orders.add(expected);

    when(orderRepositoryMock.findAll()).thenReturn(orders);

    List<OrderVM> res = ordersFilter.filter(courier);

    assertTrue(res.isEmpty());

  }

  /**
   * Test that the description order contains special word, and the courier
   * has box. So, the order is return.
   */
  @Test
  void analyzeDescriptionAndContinue() {

    Courier courier = courierRepository.findById("courier-1");

    OrderRepository orderRepositoryMock = mock(OrderRepository.class);
    ReflectionTestUtils.setField(ordersFilter, "orderRepository", orderRepositoryMock);

    Order expected = new Order().withId("order-2")
      .withDescription("I want a pizza cut into very small slices")
      .withFood(true)
      .withVip(false)
      .withPickup(new Location(41.3965463, 2.1963997))
      .withDelivery(new Location(41.407834, 2.1675979));

    List<Order> orders = new ArrayList<>();
    orders.add(expected);

    when(orderRepositoryMock.findAll()).thenReturn(orders);

    List<OrderVM> res = ordersFilter.filter(courier);

    assertTrue(res.size() == 1);

    assertEquals(expected.getId(), res.get(0).getId());

  }


  /**
   * Test that the order pickup is longer than 5 kilometres, and the courier
   * does not have electric vehicle. So, no order is return.
   */
  @Test
  void analyzeVehicleAndDiscard() {

    Courier courier = courierRepository.findById("courier-3");

    OrderRepository orderRepositoryMock = mock(OrderRepository.class);
    ReflectionTestUtils.setField(ordersFilter, "orderRepository", orderRepositoryMock);

    Order expected = new Order().withId("order-3")
      .withDescription("I want a big pizza")
      .withFood(true)
      .withVip(false)
      .withPickup(new Location(41.6965463, 2.1963997))
      .withDelivery(new Location(41.407834, 2.1675979));

    List<Order> orders = new ArrayList<>();
    orders.add(expected);

    when(orderRepositoryMock.findAll()).thenReturn(orders);

    List<OrderVM> res = ordersFilter.filter(courier);

    assertTrue(res.isEmpty());

  }

  /**
   * Test that the order pickup is longer than 5 kilometres, and the courier
   * has electric vehicle. So the order is return.
   */
  @Test
  void analyzeVehicleAndContinue() {

    Courier courier = courierRepository.findById("courier-1");

    OrderRepository orderRepositoryMock = mock(OrderRepository.class);
    ReflectionTestUtils.setField(ordersFilter, "orderRepository", orderRepositoryMock);

    Order expected = new Order().withId("order-3")
      .withDescription("I want a big pizza")
      .withFood(true)
      .withVip(false)
      .withPickup(new Location(41.6965463, 2.1963997))
      .withDelivery(new Location(41.407834, 2.1675979));

    List<Order> orders = new ArrayList<>();
    orders.add(expected);

    when(orderRepositoryMock.findAll()).thenReturn(orders);

    List<OrderVM> res = ordersFilter.filter(courier);

    assertTrue(res.size() == 1);

    assertEquals(expected.getId(), res.get(0).getId());

  }



  /**
   * Test specif group of orders (orders.json) and expected correct out.
   */
  @Test
  void evaluateOrder() {

    Courier courier = courierRepository.findById("courier-4");

    OrderRepository orderRepositoryMock = mock(OrderRepository.class);
    ReflectionTestUtils.setField(ordersFilter, "orderRepository", orderRepositoryMock);

    Order order1 = new Order().withId("order-322919652412")
      .withDescription("2x Tuna poke with Salad\n1x Hot dog with Fries\n2x Hot dog with Salad")
      .withFood(true)
      .withVip(false)
      .withDelivery(new Location(41.40643053036008, 2.1686480252396))
      .withPickup(new Location(41.40732297416662, 2.1764821793089455));

    Order order2 = new Order().withId("order-ad6408f60eb9")
      .withDescription("1x Hot dog with Fries")
      .withFood(true)
      .withVip(true)
      .withDelivery(new Location(41.38674400469712, 2.1703181250736066))
      .withPickup(new Location(41.38167379906084, 2.168236173254168));

    Order order3 = new Order().withId("order-2caca9769122")
      .withDescription("2x Hot dog with Salad\n1x Burger with Fries\n1x Tuna poke with Fries\n2x Pork bao with Fries")
      .withFood(false)
      .withVip(false)
      .withDelivery(new Location(41.40391924122571, 2.1772300459332614))
      .withPickup(new Location(41.38708026547135, 2.1674480678929138));

    Order order4 = new Order().withId("order-00871048b0b4")
      .withDescription("Envelope")
      .withFood(true)
      .withVip(true)
      .withDelivery(new Location(41.38368639845396, 2.167711445725376))
      .withPickup(new Location(41.39771344020253, 2.1730913522124435));

    Order order5 = new Order().withId("order-577dfb162503")
      .withDescription("Keys")
      .withFood(true)
      .withVip(false)
      .withDelivery(new Location(41.397470952265415, 2.1753207964537853))
      .withPickup(new Location(41.37597521847954, 2.17748168103529));

    Order order6 = new Order().withId("order-50800b932298")
      .withDescription("1x Tuna poke with Fries\n2x Burger with Salad")
      .withFood(false)
      .withVip(false)
      .withDelivery(new Location(41.38814508730626, 2.1830450531519694))
      .withPickup(new Location(41.3893491544701, 2.17839618477298));

    Order order7 = new Order().withId("order-a53231753ad7")
      .withDescription("2x Kebab with Salad\n1x Burger with Fries\n2x Hot dog with Salad")
      .withFood(false)
      .withVip(false)
      .withDelivery(new Location(41.39067390349294, 2.1661826731817735))
      .withPickup(new Location(41.37879454643381, 2.164509829158904));

    Order order8 = new Order().withId("order-c6b6359a87eb")
      .withDescription("1x Burger with Fries\n2x Kebab with Salad\n2x Pork bao with Salad\n2x Kebab with Salad")
      .withFood(true)
      .withVip(false)
      .withDelivery(new Location(41.38913471917432, 2.1646793772006285))
      .withPickup(new Location(41.40380842560184, 2.1838796274539503));

    Order order9 = new Order().withId("order-dc84fe631325")
      .withDescription("2x Hot dog with Salad\n2x Hot dog with Fries\n1x Pizza with Salad")
      .withFood(false)
      .withVip(false)
      .withDelivery(new Location(41.40303754022592, 2.18151334178865))
      .withPickup(new Location(41.3774273428396, 2.174549859306898));

    Order order10 = new Order().withId("order-19f862e14ae0")
      .withDescription("1x Burger with Salad")
      .withFood(false)
      .withVip(true)
      .withDelivery(new Location(41.39876886201351, 2.1639504864212147))
      .withPickup(new Location(41.387314074904765, 2.170604438066421));

    List<Order> orders = new ArrayList<>();
    orders.add(order1);
    orders.add(order2);
    orders.add(order3);
    orders.add(order4);
    orders.add(order5);
    orders.add(order6);
    orders.add(order7);
    orders.add(order8);
    orders.add(order9);
    orders.add(order10);

    when(orderRepositoryMock.findAll()).thenReturn(orders);

    List<OrderVM> res = ordersFilter.filter(courier);

    assertTrue(res.size() == 10);

    assertEquals("order-00871048b0b4", res.get(0).getId());
    assertEquals("order-322919652412", res.get(1).getId());
    assertEquals("order-c6b6359a87eb", res.get(2).getId());
    assertEquals("order-19f862e14ae0", res.get(3).getId());
    assertEquals("order-2caca9769122", res.get(4).getId());
    assertEquals("order-50800b932298", res.get(5).getId());
    assertEquals("order-ad6408f60eb9", res.get(6).getId());
    assertEquals("order-a53231753ad7", res.get(7).getId());
    assertEquals("order-577dfb162503", res.get(8).getId());
    assertEquals("order-dc84fe631325", res.get(9).getId());

  }

}
