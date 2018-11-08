package com.glovoapp.backender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class APITest {

  @Autowired
  private API api;

  @Rule
  public ExpectedException thrown = ExpectedException.none();


  /**
   * Test that the controller return INTERNAL_SERVER_ERROR when the reposity fail.
   */
  @Test
  void InternalServerError() {

    CourierRepository courierRepositoryMock = mock(CourierRepository.class);
    OrderRepository orderRepositoryMock = mock(OrderRepository.class);
    OrdersFilter ordersFilterMock = mock(OrdersFilter.class);

    ReflectionTestUtils.setField(api, "courierRepository", courierRepositoryMock);
    ReflectionTestUtils.setField(api, "orderRepository", orderRepositoryMock);
    ReflectionTestUtils.setField(api, "ordersFilter", ordersFilterMock);

    when(courierRepositoryMock.findById("courier-d190ca23f070")).thenThrow(new RuntimeException("Mock Exception"));

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
      new API("welcome message",orderRepositoryMock,courierRepositoryMock,ordersFilterMock))
      .build();

    MvcResult result;

    try{
      result = mockMvc.perform(get("/orders/{courierId}","courier-d190ca23f070"))
        .andExpect(status().is5xxServerError())
        .andReturn();
      String content = result.getResponse().getContentAsString();
      assertEquals("Mock Exception", content);
    }catch (Exception e) {
      //nothing to do
    }

    verify(courierRepositoryMock, times(1)).findById("courier-d190ca23f070");
  }


  /**
   * Test that the controller return BAD_REQUEST when the reposity does not find the courier.
   */
  @Test
  void BadRequestError() {

    CourierRepository courierRepositoryMock = mock(CourierRepository.class);
    OrderRepository orderRepositoryMock = mock(OrderRepository.class);
    OrdersFilter ordersFilterMock = mock(OrdersFilter.class);

    ReflectionTestUtils.setField(api, "courierRepository", courierRepositoryMock);
    ReflectionTestUtils.setField(api, "orderRepository", orderRepositoryMock);
    ReflectionTestUtils.setField(api, "ordersFilter", ordersFilterMock);

    when(courierRepositoryMock.findById("courier-d190ca23f070")).thenReturn(null);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
      new API("welcome message",orderRepositoryMock,courierRepositoryMock,ordersFilterMock))
      .build();

    MvcResult result;

    try{
      result = mockMvc.perform(get("/orders/{courierId}","courier-d190ca23f070"))
        .andExpect(status().is4xxClientError())
        .andReturn();
      String content = result.getResponse().getContentAsString();
      assertEquals("The Courier does not exists", content);
    }catch (Exception e) {
      //nothing to do
    }

    verify(courierRepositoryMock, times(1)).findById("courier-d190ca23f070");
  }


  /**
   * Test the happy path request.
   */
  @Test
  void OkRequest() {

    CourierRepository courierRepositoryMock = mock(CourierRepository.class);
    OrderRepository orderRepositoryMock = mock(OrderRepository.class);
    OrdersFilter ordersFilterMock = mock(OrdersFilter.class);

    ReflectionTestUtils.setField(api, "courierRepository", courierRepositoryMock);
    ReflectionTestUtils.setField(api, "orderRepository", orderRepositoryMock);
    ReflectionTestUtils.setField(api, "ordersFilter", ordersFilterMock);

    OrderVM order = new OrderVM("order-2", "I want a pizza cut into very small slices");

    Courier courier = new Courier().withId("courier-d190ca23f070")
      .withBox(true)
      .withName("Pablo Carrasco")
      .withVehicle(Vehicle.MOTORCYCLE)
      .withLocation(new Location(41.3965463,2.1963997));

    List<OrderVM> list = new ArrayList<>();
    list.add(order);

    when(courierRepositoryMock.findById("courier-d190ca23f070")).thenReturn(courier);
    when(ordersFilterMock.filter(courier)).thenReturn(list);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
      new API("welcome message",orderRepositoryMock,courierRepositoryMock,ordersFilterMock))
      .build();

    MvcResult result;

    try{
       result = mockMvc.perform(get("/orders/{courierId}","courier-d190ca23f070"))
        .andExpect(status().isOk())
        .andReturn();
      String content = result.getResponse().getContentAsString();
      assertTrue(content.contains("I want a pizza cut into very small slices"));
    }catch (Exception e) {
      //nothing to do
    }

    verify(courierRepositoryMock, times(1)).findById("courier-d190ca23f070");
    verify(ordersFilterMock, times(1)).filter(courier);

  }

}
