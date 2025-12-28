package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 【Mapper层 - 数据访问接口】
 * 这个文件是专门用来跟数据库打交道的。
 * 它的主要作用是定义了对 Activity（活动）表进行的各种操作，比如：
 * 1. 把新的活动保存到数据库
 * 2. 根据ID查找特定的活动
 * 3. 查出所有的活动列表
 *
 * 你不需要自己写实现类，MyBatis 框架会自动帮你把这些方法和 SQL 语句对应起来。
 */
public interface ActivityMapper {
    /**
     * 插入新活动
     * 这个方法用来把一个新的 Activity 对象保存到数据库里。
     *
     * @param activity 要保存的活动信息（比如活动名、时间、地点等）
     * @return 数据库受影响的行数（成功插入一条数据通常返回1）
     */
    int insert(Activity activity);

    /**
     * 根据ID查询活动
     * 这个方法用来根据活动的唯一ID，从数据库里把活动的所有信息取出来。
     *
     * @param id 活动的唯一标识ID
     * @return 查找到的活动对象（如果没找到会返回 null）
     */
    Activity findById(@Param("id") Long id);

    /**
     * 查询所有活动
     * 这个方法用来把数据库里所有的活动记录都查出来。
     *
     * @return 一个包含所有活动的列表
     */
    List<Activity> findAll();
}

