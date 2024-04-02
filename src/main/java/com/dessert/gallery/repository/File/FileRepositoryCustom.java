package com.dessert.gallery.repository.File;

import com.dessert.gallery.dto.user.response.UserProfileResponseDto;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;

public interface FileRepositoryCustom {

    UserProfileResponseDto getUserProfileAsUser(User user);

    UserProfileResponseDto getUserProfileAsManager(User user, Store store);
}
