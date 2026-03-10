package id.ac.ui.cs.advprog.eshop.functional;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.findAll().clear();
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(10);
        products.add(product);
        Order dummyOrder = new Order(
                "order-1",
                products,
                System.currentTimeMillis(),
                "Jovian",
                OrderStatus.WAITING_PAYMENT.getValue()
        );

        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234567890");

        Payment dummyPayment = new Payment("pay-123", dummyOrder, "VOUCHER", paymentData);
        paymentRepository.save(dummyPayment);
    }

    @Test
    void testAdminPaymentListPageIsAccessible() throws Exception {
        mockMvc.perform(get("/payment/admin/list"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("All Payments")));
    }

    @Test
    void testPaymentDetailPageIsAccessible() throws Exception {
        mockMvc.perform(get("/payment/detail/pay-123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Payment Detail")));
    }

    @Test
    void testAdminUpdatePaymentStatus() throws Exception {
        mockMvc.perform(post("/payment/admin/set-status/pay-123")
                        .param("status", "SUCCESS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/admin/list"));
    }
}