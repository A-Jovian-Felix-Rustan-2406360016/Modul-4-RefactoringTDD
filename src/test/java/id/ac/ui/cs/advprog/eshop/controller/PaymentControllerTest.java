package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    private Order dummyOrder;
    private Payment dummyPayment;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);

        List<Product> products = new ArrayList<>();
        products.add(product);

        this.dummyOrder = new Order(
                "13652556-012a-4c07-b546-54eb1396d79b",
                products,
                1708560000L,
                "Jovian Felix"
        );

        this.dummyPayment = new Payment(
                "pay-1",
                this.dummyOrder,
                "VOUCHER",
                Map.of("voucherCode", "ESHOP1234ABC5678")
        );
    }

    @Test
    void testPaymentDetail() throws Exception {
        when(paymentService.getPayment("pay-1")).thenReturn(dummyPayment);

        mockMvc.perform(get("/payment/detail/pay-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentDetail"))
                .andExpect(model().attributeExists("payment"));
    }

    @Test
    void testAdminPaymentList() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(List.of(dummyPayment));

        mockMvc.perform(get("/payment/admin/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminPaymentList"))
                .andExpect(model().attributeExists("payments"));
    }

    @Test
    void testAdminPaymentDetail() throws Exception {
        when(paymentService.getPayment("pay-1")).thenReturn(dummyPayment);

        mockMvc.perform(get("/payment/admin/detail/pay-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminPaymentDetail"))
                .andExpect(model().attributeExists("payment"));
    }

    @Test
    void testSetStatusSuccess() throws Exception {
        when(paymentService.getPayment("pay-1")).thenReturn(dummyPayment);

        mockMvc.perform(post("/payment/admin/set-status/pay-1")
                        .param("status", "SUCCESS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/admin/list"));

        verify(paymentService, times(1)).setStatus(any(Payment.class), eq("SUCCESS"));
    }

    @Test
    void testSetStatusPaymentNotFound() throws Exception {
        when(paymentService.getPayment("pay-1")).thenReturn(null);

        mockMvc.perform(post("/payment/admin/set-status/pay-1")
                        .param("status", "SUCCESS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/admin/list"));

        verify(paymentService, never()).setStatus(any(), anyString());
    }

    @Test
    void testPaymentDetailForm() throws Exception {
        mockMvc.perform(get("/payment/detail")
                        .flashAttr("order", dummyOrder))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentOrder"));
    }
}