package com.dessert.gallery.repository.ChatRoom;

import com.dessert.gallery.entity.ChatRoom;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryCustom {

    boolean existsByCustomerAndStore(User customer, Store store);

    List<ChatRoom> getChatRoomsList(int page, User user);
}
