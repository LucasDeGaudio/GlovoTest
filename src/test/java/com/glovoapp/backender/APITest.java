package com.glovoapp.backender;

import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
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

    when(courierRepositoryMock.findById("courier-d190ca23f070")).thenThrow(new RuntimeException());

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
      new API("welcome message",orderRepositoryMock,courierRepositoryMock,ordersFilterMock))
      .build();

    try{
      mockMvc.perform(get("/orders/{courierId}","courier-d190ca23f070"))
        .andExpect(status().is5xxServerError());
    }catch (Exception e) {
      //nothing to do
    }

    verify(courierRepositoryMock, times(1)).findById("courier-d190ca23f070");
  }



}
