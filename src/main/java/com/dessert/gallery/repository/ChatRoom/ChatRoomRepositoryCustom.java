package com.dessert.gallery.repository.ChatRoom;

import com.dessert.gallery.entity.ChatRoom;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.UserRole;

import java.util.List;

public interface ChatRoomRepositoryCustom {

    List<ChatRoom> getChatRoomsList(int page, User user);

    List<ChatRoom> searchChatRoomsList(int page, UserRole userRole, User user, String name);
}
