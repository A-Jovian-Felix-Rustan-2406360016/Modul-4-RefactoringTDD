package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
    private Map<String, String> paymentData;

    @BeforeEach
    void setUp() {
        this.paymentData = new HashMap<>();
        this.paymentData.put("voucherCode", "ESHOP1234ABC5678");
    }

    @Test
    void testCreatePaymentWithValidData() {
        Payment payment = new Payment("pay-1", "VOUCHER", "SUCCESS", paymentData);
        assertEquals("pay-1", payment.getId());
        assertEquals("VOUCHER", payment.getMethod());
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals(paymentData, payment.getPaymentData());
    }

    @Test
    void testCreatePaymentInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Payment("pay-1", "VOUCHER", "INVALID_STATUS", paymentData);
        });
    }

    @Test
    void testCreatePaymentEmptyMethod() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Payment("pay-1", "", "SUCCESS", paymentData);
        });
    }

    @Test
    void testSetStatusToRejected() {
        Payment payment = new Payment("pay-1", "VOUCHER", "WAITING_PAYMENT", paymentData);
        payment.setStatus("REJECTED");
        assertEquals("REJECTED", payment.getStatus());
    }

    @Test
    void testSetStatusToInvalidStatus() {
        Payment payment = new Payment("pay-1", "VOUCHER", "SUCCESS", paymentData);
        assertThrows(IllegalArgumentException.class, () -> payment.setStatus("MEOW"));
    }
}