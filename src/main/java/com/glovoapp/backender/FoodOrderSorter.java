package com.glovoapp.backender;

public class FoodOrderSorter implements OrderSorter {

  private OrderSorter next;

  @Override
  public void setNext(OrderSorter sorter) {
    this.next = sorter;
  }

  @Override
  public int compare(Order o1, Order o2) {
    boolean o1Food = o1.getFood();
    boolean o2Food = o2.getFood();

    if(o1Food != o2Food) {

      if (o1Food) {
        return -1;
      } else {
        return 1;
      }
    }

    if (this.next != null) {
      return this.next.compare(o1, o2);
    }

    return 0;

    //true first
    //return (o1Food != o2Food) ? (o1Food) ? -1 : 1 : 0;
  }

}
