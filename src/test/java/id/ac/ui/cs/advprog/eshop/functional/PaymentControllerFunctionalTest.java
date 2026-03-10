package id.ac.ui.cs.advprog.eshop.functional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

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
