package com.tt.order.service.impl;

import com.tt.order.mapper.OrderStatusMapper;
import com.tt.order.pojo.OrderStatus;
import com.tt.order.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create By Lv.QingYu in 2020/4/2
 */
@Service
public class OrderStatusServiceImpl implements OrderStatusService {

    private final OrderStatusMapper orderStatusMapper;

    @Autowired
    public OrderStatusServiceImpl(OrderStatusMapper orderStatusMapper){
        this.orderStatusMapper = orderStatusMapper;
    }

    @Override
    public List<OrderStatus> getOrderStatus() {
        List<OrderStatus> orderStatusList = orderStatusMapper.selectAll();
        return orderStatusList;
    }

    @Override
    public void saveOrderStatus(OrderStatus orderStatus) {
        orderStatusMapper.insert(orderStatus);
    }
}
