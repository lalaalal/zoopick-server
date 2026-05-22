package com.zoopick.server.service;

import com.zoopick.server.entity.*;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record CreateItemCommand(
        User reporter,
        ItemType type,
        ItemCategory category,
        ItemColor color,
        String imageUrl,
        Building building,
        String detailAddress,
        OffsetDateTime reportedAd
) {

}
