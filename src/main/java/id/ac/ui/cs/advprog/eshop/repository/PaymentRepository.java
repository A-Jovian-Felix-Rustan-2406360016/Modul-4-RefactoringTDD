package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentRepository {
    private final List<Payment> paymentData = new ArrayList<>();

    public Payment save(Payment payment) {
        Payment existingPayment = findById(payment.getId());

        if (existingPayment != null) {
            int index = paymentData.indexOf(existingPayment);
            paymentData.set(index, payment);
        } else {
            paymentData.add(payment);
        }
        return payment;
    }

    public Payment findById(String id) {
        return paymentData.stream()
                .filter(payment -> payment.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Payment> findAll() {
        return new ArrayList<>(paymentData);
    }
}