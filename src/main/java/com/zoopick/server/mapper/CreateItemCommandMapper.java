package com.zoopick.server.mapper;

import com.zoopick.server.dto.item.CreateItemPostRequest;
import com.zoopick.server.entity.Building;
import com.zoopick.server.entity.User;
import com.zoopick.server.service.CreateItemCommand;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

@Component
@NullMarked
public class CreateItemCommandMapper {
    public CreateItemCommand toCreateItemCommand(User user, Building building, CreateItemPostRequest request) {
        return CreateItemCommand.builder()
                .reporter(user)
                .type(request.getType())
                .category(request.getCategory())
                .color(request.getColor())
                .imageUrl(request.getImageUrl())
                .building(building)
                .detailAddress(request.getDetailAddress())
                .reportedAd(request.getReportedAt())
                .build();
    }

}
