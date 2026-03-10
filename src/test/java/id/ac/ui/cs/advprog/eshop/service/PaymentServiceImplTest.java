package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    @InjectMocks
    PaymentServiceImpl paymentService;

    @Mock
    PaymentRepository paymentRepository;

    Order order;

    @BeforeEach
    void setUp() {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        products.add(product);

        this.order = new Order("13652556-012a-4c07-b546-54eb1396d79b",
                products, 1708560000L, "Safira Sudrajat");
    }

    @Test
    void testAddPaymentVoucherSuccess() {
        Map<String, String> paymentData = Map.of("voucherCode", "ESHOP1234ABC5678");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.addPayment(this.order, "VOUCHER", paymentData);

        assertEquals(PaymentStatus.SUCCESS.getValue(), result.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), this.order.getStatus());
    }

    @Test
    void testAddPaymentBankTransferSuccess() {
        Map<String, String> paymentData = Map.of("bankName", "BCA", "referenceCode", "REF123");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.addPayment(this.order, "BANK_TRANSFER", paymentData);

        assertEquals(PaymentStatus.SUCCESS.getValue(), result.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), this.order.getStatus());
    }

    @Test
    void testAddPaymentUnknownMethodShouldReturnRejected() {
        Map<String, String> paymentData = Map.of("someKey", "someValue");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.addPayment(this.order, "GOPAY", paymentData);

        assertEquals(PaymentStatus.REJECTED.getValue(), result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), this.order.getStatus());
    }

    @Test
    void testIsValidVoucherFailScenarios() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, String> dataNull = new HashMap<>();
        dataNull.put("voucherCode", null);
        assertEquals(PaymentStatus.REJECTED.getValue(), paymentService.addPayment(this.order, "VOUCHER", dataNull).getStatus());

        assertEquals(PaymentStatus.REJECTED.getValue(), paymentService.addPayment(this.order, "VOUCHER", Map.of("voucherCode", "ESHOP123")).getStatus());

        assertEquals(PaymentStatus.REJECTED.getValue(), paymentService.addPayment(this.order, "VOUCHER", Map.of("voucherCode", "ABCDE12345678910")).getStatus());

        assertEquals(PaymentStatus.REJECTED.getValue(), paymentService.addPayment(this.order, "VOUCHER", Map.of("voucherCode", "ESHOP1234567AAAA")).getStatus());
    }

    @Test
    void testIsValidBankTransferFailScenarios() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, String> dataBankNull = new HashMap<>();
        dataBankNull.put("bankName", null);
        dataBankNull.put("referenceCode", "REF");
        assertEquals(PaymentStatus.REJECTED.getValue(), paymentService.addPayment(this.order, "BANK_TRANSFER", dataBankNull).getStatus());

        assertEquals(PaymentStatus.REJECTED.getValue(), paymentService.addPayment(this.order, "BANK_TRANSFER", Map.of("bankName", "  ", "referenceCode", "REF")).getStatus());

        Map<String, String> dataRefNull = new HashMap<>();
        dataRefNull.put("bankName", "BCA");
        dataRefNull.put("referenceCode", null);
        assertEquals(PaymentStatus.REJECTED.getValue(), paymentService.addPayment(this.order, "BANK_TRANSFER", dataRefNull).getStatus());

        assertEquals(PaymentStatus.REJECTED.getValue(), paymentService.addPayment(this.order, "BANK_TRANSFER", Map.of("bankName", "BCA", "referenceCode", "")).getStatus());
    }

    @Test
    void testSetStatusSuccessUpdatesOrder() {
        Payment payment = new Payment("pay-1", this.order, "VOUCHER", Map.of("voucherCode", "ESHOP1234ABC5678"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.setStatus(payment, PaymentStatus.SUCCESS.getValue());

        assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), this.order.getStatus());
    }

    @Test
    void testSetStatusRejectedUpdatesOrder() {
        Payment payment = new Payment("pay-1", this.order, "VOUCHER", Map.of("voucherCode", "ESHOP1234ABC5678"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.setStatus(payment, PaymentStatus.REJECTED.getValue());

        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), this.order.getStatus());
    }

    @Test
    void testSetStatusWaitingDoesNotChangeOrder() {
        Payment payment = new Payment("pay-1", this.order, "VOUCHER", Map.of("voucherCode", "ESHOP1234ABC5678"));
        this.order.setStatus(OrderStatus.WAITING_PAYMENT.getValue());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.setStatus(payment, PaymentStatus.WAITING_PAYMENT.getValue());

        assertEquals(PaymentStatus.WAITING_PAYMENT.getValue(), payment.getStatus());
        assertEquals(OrderStatus.WAITING_PAYMENT.getValue(), this.order.getStatus());
    }

    @Test
    void testGetPayment() {
        Payment payment = new Payment("pay-1", this.order, "VOUCHER", Map.of("voucherCode", "ESHOP1234ABC5678"));
        when(paymentRepository.findById("pay-1")).thenReturn(payment);

        Payment result = paymentService.getPayment("pay-1");
        assertNotNull(result);
        assertEquals("pay-1", result.getId());
    }

    @Test
    void testGetAllPayments() {
        List<Payment> payments = List.of(new Payment("pay-1", this.order, "VOUCHER", Map.of("voucherCode", "ESHOP1234ABC5678")));
        when(paymentRepository.findAll()).thenReturn(payments);

        List<Payment> result = paymentService.getAllPayments();
        assertEquals(1, result.size());
    }
}