package com.zoopick.server.service;

import com.zoopick.server.entity.ChatRoom;
import com.zoopick.server.entity.ChatRoomStatus;
import com.zoopick.server.entity.Item;
import com.zoopick.server.entity.User;
import com.zoopick.server.exception.InternalServerException;
import com.zoopick.server.repository.ChatRoomRepository;
import com.zoopick.server.repository.ItemRepository;
import com.zoopick.server.service.notification.NotificationService;
import com.zoopick.server.service.notification.SendNotificationCommand;
import com.zoopick.server.service.notification.payload.ItemReturnedPayload;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
@NullMarked
public class ItemReturnedEventListener {
    private final ChatRoomRepository chatRoomRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleItemReturned(ItemReturnedEvent event) {
        Item item = itemRepository.findByIdOrThrow(event.itemId());

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByItem(item);
        User owner = chatRooms.stream()
                .findAny()
                .map(ChatRoom::getOwner)
                .orElseThrow(() -> new InternalServerException("chat_room.owner is null!"));
        List<User> finders = chatRooms.stream()
                .map(ChatRoom::getFinder)
                .toList();

        chatRooms.forEach(chatRoom -> chatRoom.setStatus(ChatRoomStatus.RESOLVED_RETURNED));

        notificationService.send(finders, new SendNotificationCommand(
                owner.getNickname(),
                "아이템을 되찾았어요! 도움을 주셔서 감사합니다",
                new ItemReturnedPayload(
                        event.itemId(), item.getDisplayName(), owner.getNickname()
                )
        ));
    }
}
