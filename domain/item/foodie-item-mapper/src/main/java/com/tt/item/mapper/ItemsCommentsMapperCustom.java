package com.tt.item.mapper;

import com.tt.item.pojo.ItemsComments;
import com.tt.item.pojo.vo.MyCommentVO;
import com.tt.my.mapper.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom extends MyMapper<ItemsComments> {

    void saveComments(Map<String, Object> map);

    List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String, Object> map);

}