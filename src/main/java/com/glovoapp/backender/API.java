package com.glovoapp.backender;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
@ComponentScan("com.glovoapp.backender")
@EnableAutoConfiguration
class API {
    private final String welcomeMessage;
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final OrdersFilter ordersFilter;

    /**
     * The logger.
     */
    private final Logger log = Logger.getLogger("API.class");

    @Autowired
    API(@Value("${backender.welcome_message}") String welcomeMessage,
      OrderRepository orderRepository, CourierRepository courierRepository, OrdersFilter ordersFilter) {
        this.welcomeMessage = welcomeMessage;
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.ordersFilter = ordersFilter;
    }

    @RequestMapping("/")
    @ResponseBody
    String root() {
        return welcomeMessage;
    }

    @RequestMapping("/orders")
    @ResponseBody
    List<OrderVM> orders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderVM(order.getId(), order.getDescription()))
                .collect(Collectors.toList());
    }


    @RequestMapping(value = "/orders/{courierId}")
    @ResponseBody
    ResponseEntity<?> ordersByCourier(
      @PathVariable("courierId") String courierId) {

        log.info("Entering new Endpoint /orders/{courierId}");
        Courier courier = null;

        try {
            courier = courierRepository.findById(courierId);
        } catch (Exception exception) {
            log.info("there was an error obtaining the courier: " + courierId);
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (courier == null) {
            return new ResponseEntity<>("The Courier does not exists", HttpStatus.BAD_REQUEST);
        }

        try {
            List<OrderVM> orders = ordersFilter.filter(courier);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(API.class);
    }
}
