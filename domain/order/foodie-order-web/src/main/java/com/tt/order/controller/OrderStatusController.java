package com.tt.order.controller;

import com.tt.order.pojo.OrderStatus;
import com.tt.order.service.OrderStatusService;
import com.tt.pojo.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Create By Lv.QingYu in 2020/4/2
 */
@RestController
@RequestMapping("order/status")
public class OrderStatusController {

    private final OrderStatusService orderStatusService;

    @Autowired
    public OrderStatusController(OrderStatusService orderStatusService){
        this.orderStatusService = orderStatusService;
    }

    @GetMapping("getOrderStatus")
    public JSONResult getOrderStatus(){
        List<OrderStatus> orderStatus = orderStatusService.getOrderStatus();
        return JSONResult.ok(orderStatus);
    }

    @PostMapping("saveOrderStatus")
    public JSONResult saveOrderStatus(@RequestBody OrderStatus orderStatus){
        orderStatusService.saveOrderStatus(orderStatus);
        return JSONResult.ok();
    }

}
