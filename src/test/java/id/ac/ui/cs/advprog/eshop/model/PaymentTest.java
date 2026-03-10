package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
    private Map<String, String> paymentData;
    private Order order;

    @BeforeEach
    void setUp() {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);
        products.add(product);

        this.order = new Order("13652556-012a-4c07-b546-54eb1396d79b",
                products, 1708560000L, "Safira Sudrajat");

        this.paymentData = new HashMap<>();
        this.paymentData.put("voucherCode", "ESHOP1234ABC5678");
    }

    @Test
    void testCreatePaymentDefaultStatus() {
        Payment payment = new Payment("pay-1", this.order, "VOUCHER", this.paymentData);
        assertEquals("pay-1", payment.getId());
        assertSame(this.order, payment.getOrder());
        assertEquals("VOUCHER", payment.getMethod());
        assertEquals(this.paymentData, payment.getPaymentData());
        assertEquals(PaymentStatus.WAITING_PAYMENT.getValue(), payment.getStatus());
    }

    @Test
    void testCreatePaymentSuccessStatus() {
        Payment payment = new Payment("pay-1", this.order, "VOUCHER",
                PaymentStatus.SUCCESS.getValue(), this.paymentData);
        assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
    }

    @Test
    void testCreatePaymentInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Payment("pay-1", this.order, "VOUCHER", "MEOW", this.paymentData);
        });
    }

    @Test
    void testCreatePaymentNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Payment("pay-1", null, "VOUCHER", this.paymentData);
        });
    }

    @Test
    void testCreatePaymentEmptyMethod() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Payment("pay-1", this.order, "", this.paymentData);
        });
    }

    @Test
    void testCreatePaymentEmptyPaymentData() {
        this.paymentData.clear();
        assertThrows(IllegalArgumentException.class, () -> {
            new Payment("pay-1", this.order, "VOUCHER", this.paymentData);
        });
    }

    @Test
    void testSetStatusToRejected() {
        Payment payment = new Payment("pay-1", this.order, "VOUCHER", this.paymentData);
        payment.setStatus(PaymentStatus.REJECTED.getValue());
        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    @Test
    void testSetStatusToInvalidStatus() {
        Payment payment = new Payment("pay-1", this.order, "VOUCHER", this.paymentData);
        assertThrows(IllegalArgumentException.class, () -> payment.setStatus("BOOM"));
    }

    @Test
    void testCreatePaymentNullMethod() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Payment("pay-1", this.order, null, this.paymentData);
        });
    }

    @Test
    void testCreatePaymentNullPaymentData() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Payment("pay-1", this.order, "VOUCHER", null);
        });
    }
}