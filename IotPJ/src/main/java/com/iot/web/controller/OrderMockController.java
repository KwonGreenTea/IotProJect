package com.iot.web.controller;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/mock/orders")
public class OrderMockController {

    private static class Order {
        Long id;
        String orderNo;
        String buyerName;
        String buyerEmail;
        String productName;
        int totalPrice;
        String status; // PENDING, SHIPPING, DONE
        String createdAt;
        Order(Long id, String orderNo, String buyerName, String buyerEmail, String productName, int totalPrice, String status, String createdAt){
            this.id=id; this.orderNo=orderNo; this.buyerName=buyerName; this.buyerEmail=buyerEmail;
            this.productName=productName; this.totalPrice=totalPrice; this.status=status; this.createdAt=createdAt;
        }
    }

    private final Map<Long, Order> store = new ConcurrentHashMap<>();

    public OrderMockController() {
        // 데모용 초기 데이터 20건
        var fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (long i=1;i<=20;i++){
            String st = i%3==1? "PENDING" : (i%3==2? "SHIPPING":"DONE");
            store.put(i, new Order(
                    i,
                    "ORD-"+String.format("%05d", i),
                    "구매자"+i,
                    "buyer"+i+"@demo.com",
                    "테스트 상품 "+((i%5)+1),
                    (int)(10000+(i*137)),
                    st,
                    LocalDateTime.now().minusDays(i).format(fmt)
            ));
        }
    }

    @GetMapping
    public Map<String,Object> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,DESC") String sort,
            @RequestParam(required = false) String buyer,
            @RequestParam(required = false) String product,
            @RequestParam(required = false) String status
    ){
        // 필터링
        List<Order> all = new ArrayList<>(store.values());
        all.sort(Comparator.comparing(o -> o.createdAt));
        if (sort.toUpperCase().contains("DESC")) Collections.reverse(all);

        if (buyer != null && !buyer.isBlank()) {
            all.removeIf(o -> !((o.buyerName!=null && o.buyerName.contains(buyer)) ||
                                (o.buyerEmail!=null && o.buyerEmail.contains(buyer))));
        }
        if (product != null && !product.isBlank()) {
            all.removeIf(o -> !(o.productName!=null && o.productName.contains(product)));
        }
        if (status != null && !status.isBlank()) {
            all.removeIf(o -> !status.equalsIgnoreCase(o.status));
        }

        int total = all.size();
        int from = Math.min(page*size, total);
        int to   = Math.min(from + size, total);
        List<Order> pageList = all.subList(from, to);

        List<Map<String,Object>> content = new ArrayList<>();
        for (Order o : pageList){
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", o.id);
            m.put("orderNo", o.orderNo);
            m.put("buyerName", o.buyerName);
            m.put("buyerEmail", o.buyerEmail);
            m.put("productName", o.productName);
            m.put("totalPrice", o.totalPrice);
            m.put("status", o.status);
            m.put("createdAt", o.createdAt);
            content.add(m);
        }
        Map<String,Object> res = new LinkedHashMap<>();
        res.put("content", content);
        res.put("total", total);
        res.put("page", page);
        res.put("size", size);
        return res;
    }

    @PostMapping("/{id}/start")
    public Map<String,Object> start(@PathVariable Long id){
        Order o = store.get(id);
        if (o!=null) o.status = "SHIPPING";
        return Map.of("ok", true);
    }

    @PostMapping("/{id}/complete")
    public Map<String,Object> complete(@PathVariable Long id){
        Order o = store.get(id);
        if (o!=null) o.status = "DONE";
        return Map.of("ok", true);
    }

    @GetMapping("/{id}")
    public Map<String,Object> detail(@PathVariable Long id){
        Order o = store.get(id);
        if (o==null) return Map.of("error","not found");
        return Map.of(
                "id", o.id,
                "orderNo", o.orderNo,
                "buyerName", o.buyerName,
                "buyerEmail", o.buyerEmail,
                "productName", o.productName,
                "totalPrice", o.totalPrice,
                "status", o.status,
                "createdAt", o.createdAt
        );
    }
}
