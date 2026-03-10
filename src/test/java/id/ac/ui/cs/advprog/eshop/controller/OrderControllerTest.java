package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private PaymentService paymentService;

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
                .andExpect(view().name("historyForm"));
    }

    @Test
    void testShowOrderHistoryStatus() throws Exception {
        mockMvc.perform(post("/order/history")
                        .param("author", "Jovian"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderHistory"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void testPaymentOrderPageStatus() throws Exception {
        Order dummyOrder = new Order("123", new ArrayList<>(List.of(new Product())), 123L, "Jovian");
        when(orderService.findById("123")).thenReturn(dummyOrder);

        mockMvc.perform(get("/order/pay/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentOrder"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    void testPayOrderPostStatusSuccess() throws Exception {
        Order dummyOrder = new Order("123", new ArrayList<>(List.of(new Product())), 123L, "Jovian");
        Payment dummyPayment = new Payment("pay-1", dummyOrder, "VOUCHER", Map.of("voucherCode", "ESHOP1234ABC5678"));
        dummyPayment.setStatus("SUCCESS");

        when(orderService.findById("123")).thenReturn(dummyOrder);
        when(paymentService.addPayment(any(Order.class), anyString(), any())).thenReturn(dummyPayment);

        mockMvc.perform(post("/order/pay/123")
                        .param("method", "VOUCHER")
                        .param("voucherCode", "ESHOP1234ABC5678"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentSuccess"));
    }

    @Test
    void testPayOrderPostStatusRejected() throws Exception {
        List<Product> products = new ArrayList<>();
        Product p = new Product();
        p.setProductId("p-1");
        products.add(p);

        Order dummyOrder = new Order("123", products, 123L, "Jovian");
        Payment dummyPayment = new Payment("pay-1", dummyOrder, "VOUCHER", Map.of("voucherCode", "SALAH"));
        dummyPayment.setStatus("REJECTED");


        when(orderService.findById("123")).thenReturn(dummyOrder);
        when(paymentService.addPayment(any(), any(), any())).thenReturn(dummyPayment);
        when(orderService.findAllByAuthor("Jovian")).thenReturn(List.of(dummyOrder));

        mockMvc.perform(post("/order/pay/123")
                        .param("method", "VOUCHER")
                        .param("voucherCode", "SALAH"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderHistory")) // Sekarang ekspektasinya ke histori
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("authorName", "Jovian"));
    }

    @Test
    void testCreateOrderPost() throws Exception {
        mockMvc.perform(post("/order/create")
                        .param("author", "Jovian Felix"))
                .andExpect(status().is3xxRedirection()) // Mengetes redirect (302)
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/order/pay/")));
    }
}