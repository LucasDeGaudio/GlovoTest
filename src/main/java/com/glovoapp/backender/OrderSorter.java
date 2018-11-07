package com.glovoapp.backender;

public interface OrderSorter {

  void setNext(OrderSorter sorter);
  int compare(Order o1, Order o2);

}
