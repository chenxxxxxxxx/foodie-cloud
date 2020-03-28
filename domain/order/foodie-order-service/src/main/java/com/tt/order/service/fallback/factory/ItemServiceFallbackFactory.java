package com.tt.order.service.fallback.factory;

import com.tt.item.pojo.Items;
import com.tt.item.pojo.ItemsImg;
import com.tt.item.pojo.ItemsParam;
import com.tt.item.pojo.ItemsSpec;
import com.tt.item.pojo.vo.CommentLevelCountsVO;
import com.tt.item.pojo.vo.ShopCartVO;
import com.tt.item.service.ItemService;
import com.tt.pojo.PagedGridResult;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Create By Lv.QingYu in 2020/3/28
 */
@Component
public class ItemServiceFallbackFactory implements FallbackFactory<ItemService> {
    @Override
    public ItemService create(Throwable throwable) {
        return new ItemService() {
            @Override
            public Items queryItemById(String itemId) {
                return null;
            }

            @Override
            public List<ItemsImg> queryItemImgList(String itemId) {
                return null;
            }

            @Override
            public List<ItemsSpec> queryItemSpecList(String itemId) {
                return null;
            }

            @Override
            public ItemsParam queryItemParam(String itemId) {
                return null;
            }

            @Override
            public CommentLevelCountsVO queryCommentCounts(String itemId) {
                return null;
            }

            @Override
            public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize) {
                return null;
            }

            @Override
            public List<ShopCartVO> queryItemsBySpecIds(String specIds) {
                return null;
            }

            @Override
            public ItemsSpec queryItemSpecById(String specId) {
                return null;
            }

            @Override
            public String queryItemMainImgById(String itemId) {
                return null;
            }

            @Override
            public void decreaseItemSpecStock(String specId, int buyCounts) {

            }
        };
    }
}
