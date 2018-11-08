package com.glovoapp.backender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("OrdersFilter")
public class OrdersFilter {

    /**
     * The logger.
     */
    private final Logger log = Logger.getLogger("OrdersFilter.class");

    /**
     * The order repository.
     */
    @Autowired
    private OrderRepository orderRepository;

    @Value("${backender.specific_order}")
    String specificOrder;

    @Value("${backender.required_box}")
    String requiredBox;

    @Value("${backender.electric_vehicle}")
    String electricVehicle;

    @Value("${backender.distance_limit}")
    int distanceLimit;

    @Value("${backender.slot_delta}")
    int slotDelta;

    private OrderSorter sorter;


    /**
     * Method that filters all orders based on established specifications for a courier.
     *
     * @return filtered orders
     */

    public List<OrderVM> filter(Courier courier) {

        log.info("Entering filter");

        Boolean withBox = courier.getBox();
        Boolean withElectricVehicle = analyzeVehicle(courier.getVehicle());

        //log.info("courier: " + courier.getId());
        //log.info("withBox: " + withBox);
        //log.info("withElectricVehicle: " + withElectricVehicle);

        List<Order> orders =  orderRepository.findAll();
        Map<Order, Double> unsortedMap = new HashMap<>();

        for (Order order : orders) {

            Boolean hasSpecialWord = analyzeDescription(order.getDescription());

            if (hasSpecialWord) {
                if (!withBox) {
                    continue;
                }
            }

            Double distance =  DistanceCalculator.calculateDistance(courier.getLocation(), order.getPickup());

            if (distance <= distanceLimit) {
                unsortedMap.put(order, distance);
            } else if (withElectricVehicle) {
                unsortedMap.put(order, distance);
            }
        }

        Map<Order, Double> sortedMap = sortOrdersByDistance(unsortedMap);

        List<List<Order>> ordersGrouped = groupOrdersInSlots(sortedMap);

        List<OrderVM> finalList = new ArrayList<>();

        for (List<Order> list : ordersGrouped) {
            List<OrderVM> res = specificSort(list);
            finalList.addAll(res);
        }

        log.info("Leaving filter");

        return finalList;
    }


    /**
     * Method to analyze the vehicle of the courier.
     *
     * @return true if it is MOTORCYCLE or ELECTRIC_SCOOTER, false otherwise
     */
    private Boolean analyzeVehicle (Vehicle vehicle) {

        log.info("Entering analyzeVehicle");

        List<String> listWords = Arrays.asList(electricVehicle.split(","));
        String vehicleType = vehicle.toString().toLowerCase();

        log.info("Leaving analyzeVehicle");

        return listWords.stream().map(String::toLowerCase).anyMatch(s -> vehicleType.contains(s));

    }


    /**
     * Method to analyze the description of the order.
     *
     * @return true if the description contains Pizza, Cake or Flamingo, false otherwise
     */
    private Boolean analyzeDescription (String description) {

        log.info("Entering analyzeDescription");

        description = description.toLowerCase();
        List<String> strings = Arrays.asList(description.split(" "));
        List<String> listWords = Arrays.asList(requiredBox.split(","));

        log.info("Leaving analyzeDescription");

        return listWords.stream().map(String::toLowerCase).anyMatch(s -> strings.contains(s));
    }

    /**
     * Method that sorts the orders based on the distance to the Courier.
     *
     * @return the orders sorted
     */
    private Map<Order, Double> sortOrdersByDistance (Map<Order, Double> unsortedMap) {

        log.info("Entering sortOrdersByDistance");

        Map<Order, Double> sortedMap = new LinkedHashMap<>();
        unsortedMap.entrySet().stream()
          .sorted(Map.Entry.comparingByValue())
          .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        log.info("Leaving sortOrdersByDistance");

        return sortedMap;
    }

    /**
     * Method that groups the orders in slots
     *
     * @return the orders grouped.
     */
    private List<List<Order>> groupOrdersInSlots (Map<Order, Double> sortedMap) {

        log.info("Entering groupOrdersInSlots");

        List<List<Order>> entireLists = new ArrayList<List<Order>>();
        List<Order> temporalList = new ArrayList<Order>();
        int slotLimit = slotDelta;

        for (Map.Entry<Order,Double> o : sortedMap.entrySet()) {

            if (o.getValue()*1000 < slotLimit) {
                temporalList.add(o.getKey());
            } else {
                List<Order> copyList = new ArrayList<>(temporalList);
                entireLists.add(copyList);
                slotLimit += slotDelta;
                temporalList.clear();
                while (o.getValue()*1000 >= slotLimit){
                    slotLimit += slotDelta;
                }
                temporalList.add(o.getKey());
            }
        }

        entireLists.add(temporalList);

        log.info("Leaving groupOrdersInSlots");

        return  entireLists;
    }


    /**
     * Method that sorts the orders based on specific parameter.
     *
     * @return the orders sorted by specific parameter
     */
    private List<OrderVM> specificSort (List<Order> sortedList) {

        log.info("Entering specificSort");

        List<String> orderList = Arrays.asList(specificOrder.split(","));

        Map<String, Class<? extends OrderSorter>> sorterMap = new HashMap<>();
        sorterMap.put("vip" , VipOrderSorter.class);
        sorterMap.put("food" , FoodOrderSorter.class);

        //Here define the Chain Responsibility to sort orders
        OrderSorter newSorter = null;
        OrderSorter prevSorter = null;

        for (String code : orderList) {
            Class<? extends OrderSorter> clazz = sorterMap.get(code);
            try {
                newSorter = clazz.newInstance();
                if (prevSorter != null) {
                    prevSorter.setNext(newSorter);
                } else {
                    this.sorter = newSorter;
                }
                prevSorter = newSorter;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        Collections.sort(sortedList, this.sorter::compare);

        log.info("Leaving specificSort");

        return sortedList
          .stream()
          .map(order -> new OrderVM(order.getId(), order.getDescription()))
          .collect(Collectors.toList());

    }

}
