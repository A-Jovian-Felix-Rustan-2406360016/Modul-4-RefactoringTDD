package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void testCreateOrderPageStatus() throws Exception {
        mockMvc.perform(get("/order/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("createOrder"));
    }

    @Test
    void testOrderHistoryPageStatus() throws Exception {
        mockMvc.perform(get("/order/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderHistory"));
    }

    @Test
    void testShowOrderHistoryStatus() throws Exception {
        mockMvc.perform(post("/order/history")
                        .param("author", "Jovian"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderList"))
                .andExpect(model().attributeExists("orders"));
    }
}