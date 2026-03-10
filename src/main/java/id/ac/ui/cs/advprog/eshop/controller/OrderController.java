package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
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
    @Autowired private OrderService orderService;
    @Autowired private PaymentService paymentService;

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
        List<Order> orders = orderService.findAllByAuthor(author);
        model.addAttribute("orders", orders);
        model.addAttribute("authorName", author);

        return "orderHistory";
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

        if (payment.getStatus().equals("REJECTED")) {
            List<Order> orders = orderService.findAllByAuthor(order.getAuthor());
            model.addAttribute("orders", orders);
            model.addAttribute("authorName", order.getAuthor());

            return "orderHistory";
        }

        model.addAttribute("payment", payment);
        return "paymentSuccess";
    }
}