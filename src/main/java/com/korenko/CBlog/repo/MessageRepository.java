package com.korenko.CBlog.repo;

import com.korenko.CBlog.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
    List<MessageEntity> findBySenderOrderByTimestampAsc(String sender);

    @Query("SELECT m FROM MessageEntity m WHERE " +
            "(m.sender = :user1 AND m.recipient = :user2) OR " +
            "(m.sender = :user2 AND m.recipient = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<MessageEntity> findMessagesBetweenUsers(@Param("user1") String user1,
                                                 @Param("user2") String user2);

    void deleteById(Integer id);
    MessageEntity getMessageEntityById(Integer id);
//    void updateById(Integer id, String text);
}
