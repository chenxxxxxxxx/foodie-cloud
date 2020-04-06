package com.tt.order.service;

import com.tt.order.pojo.OrderStatus;

import java.util.List;

/**
 * Create By Lv.QingYu in 2020/4/2
 */
public interface OrderStatusService {

    List<OrderStatus> getOrderStatus();

    void saveOrderStatus(OrderStatus orderStatus);
}
