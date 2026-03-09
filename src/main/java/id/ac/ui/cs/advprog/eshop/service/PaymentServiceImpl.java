package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        Payment payment = new Payment(UUID.randomUUID().toString(), order, method, paymentData);

        if (method.equals("VOUCHER")) {
            String voucherCode = paymentData.get("voucherCode");
            if (isValidVoucher(voucherCode)) {
                payment.setStatus(PaymentStatus.SUCCESS.getValue());
            } else {
                payment.setStatus(PaymentStatus.REJECTED.getValue());
            }
        }
        else if (method.equals("BANK_TRANSFER")) {
            String bankName = paymentData.get("bankName");
            String referenceCode = paymentData.get("referenceCode");
            if (bankName != null && !bankName.isBlank() &&
                    referenceCode != null && !referenceCode.isBlank()) {
                payment.setStatus(PaymentStatus.SUCCESS.getValue());
            } else {
                payment.setStatus(PaymentStatus.REJECTED.getValue());
            }
        }

        updateOrderStatus(payment);
        return paymentRepository.save(payment);
    }

    private boolean isValidVoucher(String code) {
        if (code == null || code.length() != 16 || !code.startsWith("ESHOP")) {
            return false;
        }
        long digitCount = code.chars().filter(Character::isDigit).count();
        return digitCount == 8;
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        updateOrderStatus(payment);
        return paymentRepository.save(payment);
    }


    private void updateOrderStatus(Payment payment) {
        String status = payment.getStatus();
        if (PaymentStatus.SUCCESS.getValue().equals(status)) {
            payment.getOrder().setStatus(OrderStatus.SUCCESS.getValue());
        } else if (PaymentStatus.REJECTED.getValue().equals(status)) {
            payment.getOrder().setStatus(OrderStatus.FAILED.getValue());
        }
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
