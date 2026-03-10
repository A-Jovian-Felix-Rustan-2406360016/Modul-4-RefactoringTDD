package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    private static final String ORDER_HISTORY_VIEW = "orderHistory";

    @Autowired
    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/create")
    public String createOrderPage() {
        return "createOrder";
    }

    @PostMapping("/create")
    public String createOrder(@RequestParam String author) {
        List<Product> products = new ArrayList<>();
        Product dummy = new Product();
        dummy.setProductId(UUID.randomUUID().toString());
        dummy.setProductName("Default Item");
        dummy.setProductQuantity(1);
        products.add(dummy);

        Order order = new Order(UUID.randomUUID().toString(), products, System.currentTimeMillis(), author);
        orderService.createOrder(order);
        return "redirect:/order/pay/" + order.getId();
    }

    @GetMapping("/history")
    public String historyPage() {
        return "historyForm";
    }

    @PostMapping("/history")
    public String showHistory(@RequestParam String author, Model model) {
        return populateHistoryModel(author, model);
    }

    @GetMapping("/pay/{orderId}")
    public String paymentOrderPage(@PathVariable String orderId, Model model) {
        model.addAttribute("order", orderService.findById(orderId));
        return "paymentOrder";
    }

    @PostMapping("/pay/{id}")
    public String payOrder(@PathVariable String id,
                           @RequestParam String method,
                           @RequestParam Map<String, String> paymentData,
                           Model model) {

        Order order = orderService.findById(id);
        Payment payment = paymentService.addPayment(order, method, paymentData);

        if ("REJECTED".equals(payment.getStatus())) {
            return populateHistoryModel(order.getAuthor(), model);
        }

        model.addAttribute("payment", payment);
        return "paymentSuccess";
    }

    private String populateHistoryModel(String author, Model model) {
        List<Order> orders = orderService.findAllByAuthor(author);
        model.addAttribute("orders", orders);
        model.addAttribute("authorName", author);
        return ORDER_HISTORY_VIEW;
    }
}