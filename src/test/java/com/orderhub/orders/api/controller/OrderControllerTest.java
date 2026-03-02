package com.orderhub.orders.api.controller;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.orderhub.orders.appication.service.OrderService;
        import com.orderhub.orders.domain.exception.BusinessRuleException;
        import com.orderhub.orders.domain.exception.ResourceNotFoundException;
        import com.orderhub.orders.domain.model.Order;
        import com.orderhub.orders.domain.model.OrderStatus;
        import org.junit.jupiter.api.Test;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
        import org.springframework.boot.test.context.SpringBootTest;
        import org.springframework.test.context.bean.override.mockito.MockitoBean;
        import org.springframework.http.MediaType;
        import org.springframework.test.web.servlet.MockMvc;

        import java.math.BigDecimal;
        import java.time.Instant;
        import java.util.UUID;

        import static org.mockito.ArgumentMatchers.any;
        import static org.mockito.Mockito.when;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    OrderService service;

    @Test
    void create_shouldReturn201() throws Exception {
        UUID id = UUID.randomUUID();

        Order created = new Order(
                id,
                "c-123",
                new BigDecimal("99.90"),
                OrderStatus.CREATED,
                Instant.parse("2026-03-01T10:00:00Z")
        );

        when(service.create(any())).thenReturn(created);

        String body = """
                {
                  "customerId": "c-123",
                  "totalAmount": 99.90
                }
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orders/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.customerId").value("c-123"))
                .andExpect(jsonPath("$.totalAmount").value(99.90));
    }

    @Test
    void create_shouldReturn400_whenValidationFails() throws Exception {
        String body = """
                {
                  "customerId": "",
                  "totalAmount": -10
                }
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(2));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getById(id)).thenThrow(new ResourceNotFoundException("Order not found: " + id));

        mockMvc.perform(get("/orders/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Order not found: " + id))
                .andExpect(jsonPath("$.path").value("/orders/" + id));
    }

    @Test
    void cancel_shouldReturn409_whenAlreadyCancelled() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.cancel(id)).thenThrow(new BusinessRuleException("Order already cancelled: " + id));

        mockMvc.perform(post("/orders/" + id + "/cancel"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"))
                .andExpect(jsonPath("$.message").value("Order already cancelled: " + id))
                .andExpect(jsonPath("$.path").value("/orders/" + id + "/cancel"));
    }
}