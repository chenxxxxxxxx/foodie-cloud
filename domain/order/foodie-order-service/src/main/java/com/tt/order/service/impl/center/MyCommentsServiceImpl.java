package com.tt.order.service.impl.center;

import com.tt.enums.YesOrNo;
import com.tt.order.mapper.OrderItemsMapper;
import com.tt.order.mapper.OrderStatusMapper;
import com.tt.order.mapper.OrdersMapper;
import com.tt.order.pojo.OrderItems;
import com.tt.order.pojo.OrderStatus;
import com.tt.order.pojo.Orders;
import com.tt.order.pojo.bo.center.OrderItemsCommentBO;
import com.tt.order.service.center.MyCommentsService;
import com.tt.order.service.fallback.ItemCommentsServiceFeignClient;
import com.tt.service.BaseService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {

    private final OrderItemsMapper orderItemsMapper;
    private final OrdersMapper ordersMapper;
    private final OrderStatusMapper orderStatusMapper;
    private final ItemCommentsServiceFeignClient itemCommentsService;
    private final Sid sid;

    @Autowired
    public MyCommentsServiceImpl(OrderItemsMapper orderItemsMapper,
                                 OrdersMapper ordersMapper,
                                 OrderStatusMapper orderStatusMapper,
                                 ItemCommentsServiceFeignClient itemCommentsService,
                                 Sid sid) {
        this.orderItemsMapper = orderItemsMapper;
        this.ordersMapper = ordersMapper;
        this.orderStatusMapper = orderStatusMapper;
        this.itemCommentsService = itemCommentsService;
        this.sid = sid;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems query = new OrderItems();
        query.setOrderId(orderId);
        return orderItemsMapper.select(query);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String orderId, String userId,
                             List<OrderItemsCommentBO> commentList) {

        // 1. 保存评价 items_comments
        for (OrderItemsCommentBO oic : commentList) {
            oic.setCommentId(sid.nextShort());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("commentList", commentList);
        itemCommentsService.saveComments(map);

        // 2. 修改订单表改已评价 orders
        Orders order = new Orders();
        order.setId(orderId);
        order.setIsComment(YesOrNo.YES.type);
        ordersMapper.updateByPrimaryKeySelective(order);

        // 3. 修改订单状态表的留言时间 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    // TODO 移到了itemCommentService
//    @Transactional(propagation = Propagation.SUPPORTS)
//    @Override
//    public PagedGridResult queryMyComments(String userId,
//                                           Integer page,
//                                           Integer pageSize) {
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("userId", userId);
//
//        PageHelper.startPage(page, pageSize);
//        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);
//
//        return setterPagedGrid(list, page);
//    }
}
