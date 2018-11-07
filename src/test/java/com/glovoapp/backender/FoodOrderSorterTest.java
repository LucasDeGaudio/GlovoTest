package com.glovoapp.backender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class FoodOrderSorterTest {


  @Test
  void compareDifferentOrders() {

    Order o1 = new Order().withId("order-1")
      .withDescription("I want a pizza cut into very small slices")
      .withFood(true)
      .withVip(true)
      .withPickup(new Location(41.3965463, 2.1963997))
      .withDelivery(new Location(41.407834, 2.1675979));

    Order o2 = new Order().withId("order-2")
      .withDescription("fries")
      .withFood(false)
      .withVip(false)
      .withPickup(new Location(41.3965463, 2.1963997))
      .withDelivery(new Location(41.407834, 2.1675979));

    FoodOrderSorter foodOrderSorter = new FoodOrderSorter();
    int res = foodOrderSorter.compare(o1, o2);

    assertEquals(-1, res);
  }

  @Test
  void compareDifferentOrders2() {

    Order o1 = new Order().withId("order-3")
      .withDescription("I want a pizza cut into very small slices")
      .withFood(false)
      .withVip(true)
      .withPickup(new Location(41.3965463, 2.1963997))
      .withDelivery(new Location(41.407834, 2.1675979));

    Order o2 = new Order().withId("order-4")
      .withDescription("fries")
      .withFood(true)
      .withVip(false)
      .withPickup(new Location(41.3965463, 2.1963997))
      .withDelivery(new Location(41.407834, 2.1675979));

    FoodOrderSorter foodOrderSorter = new FoodOrderSorter();
    int res = foodOrderSorter.compare(o1, o2);

    assertEquals(1, res);
  }


  @Test
  void compareEqualsOrders() {

    Order o1 = new Order().withId("order-5")
      .withDescription("I want a pizza cut into very small slices")
      .withFood(true)
      .withVip(false)
      .withPickup(new Location(41.3965463, 2.1963997))
      .withDelivery(new Location(41.407834, 2.1675979));

    Order o2 = new Order().withId("order-6")
      .withDescription("fries")
      .withFood(true)
      .withVip(true)
      .withPickup(new Location(41.3965463, 2.1963997))
      .withDelivery(new Location(41.407834, 2.1675979));

    FoodOrderSorter foodOrderSorter = new FoodOrderSorter();
    int res = foodOrderSorter.compare(o1, o2);

    assertEquals(0, res);
  }
}
