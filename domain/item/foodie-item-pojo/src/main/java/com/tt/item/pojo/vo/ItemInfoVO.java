package com.tt.item.pojo.vo;

import com.tt.item.pojo.Items;
import com.tt.item.pojo.ItemsImg;
import com.tt.item.pojo.ItemsParam;
import com.tt.item.pojo.ItemsSpec;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品详情VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoVO {

    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;

}
