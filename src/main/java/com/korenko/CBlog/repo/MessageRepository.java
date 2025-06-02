package com.korenko.CBlog.repo;

import com.korenko.CBlog.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("UPDATE MessageEntity m SET m.content = :content WHERE m.id = :id")
    int updateMessageContentById(@Param("id") int id, @Param("content") String content);

    @Query("SELECT DISTINCT CASE WHEN m.sender = :username THEN m.recipient ELSE m.sender END " +
            "FROM MessageEntity m WHERE m.sender = :username OR m.recipient = :username")
    List<String> findChatParticipants(@Param("username") String username);

    @Modifying
    @Query("UPDATE MessageEntity m SET " +
            "m.sender = CASE WHEN m.sender = :username THEN 'DELETED' ELSE m.sender END, " +
            "m.recipient = CASE WHEN m.recipient = :username THEN 'DELETED' ELSE m.recipient END " +
            "WHERE m.sender = :username OR m.recipient = :username")
    void markDeletedUser(@Param("username") String username);

    @Modifying
    @Query("DELETE FROM MessageEntity m WHERE m.sender = :username OR m.recipient = :username")
    void deleteAllMessagesByUser(@Param("username") String username);

    @Query("SELECT DISTINCT CASE " +
            "WHEN m.sender = :currentUser THEN m.recipient " +
            "ELSE m.sender END " +
            "FROM MessageEntity m " +
            "WHERE m.sender = :currentUser OR m.recipient = :currentUser")
    List<String> findOpponents(@Param("currentUser") String currentUser);

    @Query("SELECT m FROM MessageEntity m " +
            "WHERE (m.sender = :user1 AND m.recipient = :user2) " +
            "OR (m.sender = :user2 AND m.recipient = :user1) " +
            "ORDER BY m.timestamp DESC LIMIT 1")
    MessageEntity findLastMessageBetweenUsers(
            @Param("user1") String user1,
            @Param("user2") String user2);

    @Modifying
    @Query("UPDATE MessageEntity m SET m.isRead = :isRead WHERE m.id = :id")
    int updateMessageStatusById(@Param("id") int id, @Param("isRead") Boolean isRead);

    @Query("SELECT m.id FROM MessageEntity m WHERE " +
            "(m.sender = :user2 AND m.recipient = :user1) " +
            "AND m.isRead = false")
    List<Integer> findUnreadMessagesIdsBetweenUsers(@Param("user1") String user1,
                                                    @Param("user2") String user2);

    @Modifying
    @Query("UPDATE MessageEntity m SET m.isRead = true WHERE m.id IN :ids")
    void markMessagesAsRead(@Param("ids") List<Integer> ids);
}
//"((m.sender = :user1 AND m.recipient = :user2) OR " +
