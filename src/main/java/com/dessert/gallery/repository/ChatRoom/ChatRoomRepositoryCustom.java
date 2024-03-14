package com.dessert.gallery.repository.ChatRoom;

import com.dessert.gallery.entity.ChatRoom;
import com.dessert.gallery.entity.User;

import java.util.List;

public interface ChatRoomRepositoryCustom {

    List<ChatRoom> getChatRoomsList(int page, User user);
}
