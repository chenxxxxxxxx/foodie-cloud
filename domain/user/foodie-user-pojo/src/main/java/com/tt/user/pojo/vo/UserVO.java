package com.tt.user.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Create By Lv.QingYu in 2020/4/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {

    private String token;

    private String userId;

    /**
     * 用户名 用户名
     */
    private String username;

    /**
     * 昵称 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 头像
     */
    private String face;

    /**
     * 手机号 手机号
     */
    private String mobile;

    /**
     * 邮箱地址 邮箱地址
     */
    private String email;

    /**
     * 性别 性别 1:男  0:女  2:保密
     */
    private Integer sex;

    /**
     * 生日 生日
     */
    private Date birthday;
}
