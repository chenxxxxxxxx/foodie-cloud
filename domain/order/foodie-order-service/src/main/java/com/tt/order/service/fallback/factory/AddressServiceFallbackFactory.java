package com.tt.order.service.fallback.factory;

import com.tt.user.pojo.UserAddress;
import com.tt.user.pojo.bo.AddressBO;
import com.tt.user.service.AddressService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Create By Lv.QingYu in 2020/3/28
 */
@Component
public class AddressServiceFallbackFactory implements FallbackFactory<AddressService> {
    @Override
    public AddressService create(Throwable throwable) {
        return new AddressService() {
            @Override
            public List<UserAddress> queryAll(String userId) {
                return null;
            }

            @Override
            public void addNewUserAddress(AddressBO addressBO) {

            }

            @Override
            public void updateUserAddress(AddressBO addressBO) {

            }

            @Override
            public void deleteUserAddress(String userId, String addressId) {

            }

            @Override
            public void updateUserAddressToBeDefault(String userId, String addressId) {

            }

            @Override
            public UserAddress queryUserAddres(String userId, String addressId) {
                return null;
            }
        };
    }
}
