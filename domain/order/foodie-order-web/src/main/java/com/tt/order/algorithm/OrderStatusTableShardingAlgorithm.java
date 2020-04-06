package com.tt.order.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;
import java.util.Iterator;

/**
 * Create By Lv.QingYu in 2020/4/2
 */
@Slf4j
public class OrderStatusTableShardingAlgorithm implements PreciseShardingAlgorithm {
    @Override
    public String doSharding(Collection collection, PreciseShardingValue preciseShardingValue) {
        Iterator iterator = collection.iterator();
        String columnName = preciseShardingValue.getColumnName();
        String logicTableName = preciseShardingValue.getLogicTableName();
        Comparable value = preciseShardingValue.getValue();
        int hashCode = value.hashCode();
        log.info("columnName :{}, logicTableName:{}, value:{}", columnName, logicTableName, value);
        String tableNameEnd = String.valueOf(hashCode % collection.size() + 1);
        while (iterator.hasNext()){
            String tableName = (String) iterator.next();
            if (tableName.endsWith(tableNameEnd)) {
                    return tableName;
            }
        }
        return null;
    }
}
