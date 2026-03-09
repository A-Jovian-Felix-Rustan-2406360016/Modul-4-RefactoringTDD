package id.ac.ui.cs.advprog.eshop.model;

import lombok.Getter;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

@Getter
public class Payment {
    private String id;
    private String method;
    private String status;
    private Map<String, String> paymentData;

    public Payment(String id, String method, String status, Map<String, String> paymentData) {
        this.id = id;

        if (method == null || method.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.method = method;
        this.paymentData = paymentData;
        this.setStatus(status);
    }

    public void setStatus(String status) {
        List<String> validStatus = Arrays.asList("WAITING_PAYMENT", "SUCCESS", "REJECTED");
        if (validStatus.contains(status)) {
            this.status = status;
        } else {
            throw new IllegalArgumentException();
        }
    }
}