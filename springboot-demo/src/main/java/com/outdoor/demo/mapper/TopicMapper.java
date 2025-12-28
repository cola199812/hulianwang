package com.outdoor.demo.mapper;

import com.outdoor.demo.entity.Topic;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TopicMapper {
    @Insert("INSERT IGNORE INTO topic(name) VALUES(#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Topic topic);

    @Select("SELECT * FROM topic WHERE name = #{name}")
    Topic findByName(String name);

    @Insert("INSERT IGNORE INTO post_topic(post_id, topic_id) VALUES(#{postId}, #{topicId})")
    void insertPostTopic(@Param("postId") Long postId, @Param("topicId") Long topicId);
    
    @Select("SELECT * FROM topic ORDER BY id DESC LIMIT 20")
    List<Topic> findHotTopics();

    @Select("SELECT t.* FROM topic t INNER JOIN post_topic pt ON t.id = pt.topic_id WHERE pt.post_id = #{postId}")
    List<Topic> findByPostId(Long postId);
}
