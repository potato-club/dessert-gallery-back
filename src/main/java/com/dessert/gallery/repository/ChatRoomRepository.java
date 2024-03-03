package com.dessert.gallery.repository;

import com.dessert.gallery.entity.ChatRoom;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    boolean existsByCustomerAndStore(User customer, Store store);
}
