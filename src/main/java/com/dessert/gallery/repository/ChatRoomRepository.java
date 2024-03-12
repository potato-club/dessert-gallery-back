package com.dessert.gallery.repository;

import com.dessert.gallery.dto.chat.list.ChatRoomDto;
import com.dessert.gallery.entity.ChatRoom;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByCustomer(User user);
    List<ChatRoom> findByStore(Store store);
    boolean existsByCustomerAndStore(User customer, Store store);
}
