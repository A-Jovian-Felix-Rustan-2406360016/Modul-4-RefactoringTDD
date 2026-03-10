package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/create")
    public String createOrderPage(Model model) {
        return "createOrder";
    }

    @GetMapping("/history")
    public String orderHistoryPage() {
        return "orderHistory";
    }

    @PostMapping("/history")
    public String showOrderHistory(@RequestParam String author, Model model) {
        List<Order> orders = orderService.findAllByAuthor(author);
        model.addAttribute("orders", orders);
        model.addAttribute("author", author);
        return "orderList";
    }

    @GetMapping("/pay/{orderId}")
    public String paymentOrderPage(@PathVariable String orderId, Model model) {
        Order order = orderService.findById(orderId);
        model.addAttribute("order", order);
        return "paymentOrder";
    }
}
