package com.outdoor.demo.service;

import com.outdoor.demo.entity.Activity;

import java.util.List;
import java.util.Map;

/**
 * 【Service层 - 业务逻辑接口】
 * 这个文件定义了活动模块的“业务规则”。
 * 就像餐厅的菜单一样，这里列出了我们可以对活动做什么操作，但不关心具体怎么做（怎么做在 Impl 实现类里写）。
 * 主要功能：
 * 1. 创建新活动
 * 2. 查看活动列表（带人数统计）
 * 3. 报名参加活动
 */
public interface ActivityService {
    /**
     * 创建活动
     * 处理创建活动的业务逻辑。
     *
     * @param activity 包含活动信息的对象
     * @return 新创建活动的唯一ID
     */
    Long create(Activity activity);

    /**
     * 获取活动列表
     * 这里的逻辑比较特殊，不仅要查出活动，还要算出每个活动有多少人报名了。
     *
     * @return 一个列表，每项包含了活动信息和报名人数
     */
    List<Map<String, Object>> listWithCurrentPeople();

    /**
     * 报名活动
     * 处理用户报名的逻辑。这里面会检查：
     * 1. 活动存不存在
     * 2. 人数满了没有
     * 3. 用户是不是已经报过名了
     *
     * @param activityId 活动的ID
     * @param userId 用户的ID
     * @return 如果报名成功返回 true，失败返回 false
     */
    boolean join(Long activityId, Long userId);
}

