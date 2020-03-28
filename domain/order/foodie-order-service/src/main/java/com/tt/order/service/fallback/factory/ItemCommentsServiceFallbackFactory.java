package com.tt.order.service.fallback.factory;

import com.google.common.collect.Lists;
import com.tt.item.pojo.vo.MyCommentVO;
import com.tt.item.service.ItemCommentsService;
import com.tt.pojo.PagedGridResult;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Create By Lv.QingYu in 2020/3/28
 */
@Component
@Slf4j
public class ItemCommentsServiceFallbackFactory implements FallbackFactory<ItemCommentsService> {
    @Override
    public ItemCommentsService create(Throwable throwable) {
        return new ItemCommentsService() {
            @Override
            public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {
                MyCommentVO commentVO = new MyCommentVO();
                commentVO.setContent("正在加载中");

                PagedGridResult result = new PagedGridResult();
                result.setRows(Lists.newArrayList(commentVO));
                result.setTotal(1);
                result.setRecords(1);
                return result;
            }

            @Override
            public void saveComments(Map<String, Object> map) {
                log.warn("服务器异常，无法提供服务~~~~~~~");
            }
        };
    }
}
