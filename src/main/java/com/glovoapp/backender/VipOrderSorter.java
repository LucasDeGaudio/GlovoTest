package com.glovoapp.backender;

public class VipOrderSorter implements OrderSorter {

  private OrderSorter next;

  @Override
  public void setNext(OrderSorter sorter) {
    this.next = sorter;
  }

  @Override
  public int compare(Order o1, Order o2) {
    boolean o1Vip = o1.getVip();
    boolean o2Vip = o2.getVip();

    if(o1Vip != o2Vip) {

      if (o1Vip) {
        return -1;
      } else {
        return 1;
      }
    }

    if (this.next != null) {
      return this.next.compare(o1, o2);
    }

    return 0;

    // return true first
    //return (o1Vip != o2Vip) ? (o1Vip) ? -1 : 1 : 0;

  }
}
