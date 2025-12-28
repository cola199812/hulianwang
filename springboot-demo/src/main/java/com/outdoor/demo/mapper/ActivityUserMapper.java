package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.ActivityUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
/**
 * 活动报名数据访问接口
 * 负责ActivityUser表的数据库操作，管理用户与活动的关联。
 */
public interface ActivityUserMapper {
    /**
     * 插入报名记录
     * @param au 报名信息
     * @return 影响行数
     */
    int insert(ActivityUser au);

    /**
     * 统计活动报名人数
     * @param activityId 活动ID
     * @return 报名人数
     */
    int countByActivityId(@Param("activityId") Long activityId);

    /**
     * 查询用户是否已报名某活动
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 报名记录
     */
    ActivityUser findByActivityIdAndUserId(@Param("activityId") Long activityId, @Param("userId") Long userId);
}

