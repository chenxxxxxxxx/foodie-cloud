package com.tt.order.algorithm;

import lombok.extern.slf4j.Slf4j;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * Create By Lv.QingYu in 2020/4/2
 */
@Slf4j
public class TableShardingAlgorithm implements PreciseShardingAlgorithm {

    @Override
    public String doSharding(Collection collection, PreciseShardingValue preciseShardingValue) {
        log.info("c :{}, c :{}", collection, preciseShardingValue);
        return null;
    }
}
