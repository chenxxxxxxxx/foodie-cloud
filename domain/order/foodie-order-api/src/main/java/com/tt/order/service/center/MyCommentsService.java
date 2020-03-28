package com.tt.order.service.center;

import com.tt.order.pojo.OrderItems;
import com.tt.order.pojo.bo.center.OrderItemsCommentBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "foodie-order-service")
@RequestMapping("order-comments-api")
public interface MyCommentsService {

    /**
     * 根据订单id查询关联的商品
     *
     * @param orderId
     * @return
     */
    @GetMapping("orderItems")
    List<OrderItems> queryPendingComment(@RequestParam("orderId") String orderId);

    /**
     * 保存用户的评论
     *
     * @param orderId
     * @param userId
     * @param commentList
     */
    @PostMapping("saveOrderComments")
    void saveComments(@RequestParam("orderId") String orderId,
                      @RequestParam("userId") String userId,
                      @RequestBody List<OrderItemsCommentBO> commentList);

}
