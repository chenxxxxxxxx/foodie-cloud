package com.tt.auth.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Create By Lv.QingYu in 2020/3/30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccount implements Serializable {

    private String username;

    private String token;

    private String refreshToken;

}
