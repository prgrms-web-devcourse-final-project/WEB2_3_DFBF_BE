package org.dfbf.soundlink.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.chat.dto.ChatReqDto;
import org.dfbf.soundlink.domain.chat.entity.ChatRoom;
import org.dfbf.soundlink.domain.chat.exception.ChatRoomNotFoundException;
import org.dfbf.soundlink.domain.chat.exception.UnauthorizedAccessException;
import org.dfbf.soundlink.domain.chat.repository.ChatRoomRepository;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.exception.EmotionRecordNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.exception.UserNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.comm.enums.RoomStatus;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;


@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final EmotionRecordRepository emotionRecordRepository;
    private final RedisTemplate<String, String> redisTemplate;


    @Transactional
    public ResponseResult createChatRoom(@AuthenticationPrincipal Long userId, Long recordId){
        try {
            //요청 보내는사람
            User requestUserId = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);

            //감정기록 조회
            EmotionRecord emotionRecord = emotionRecordRepository.findById(recordId)
                    .orElseThrow(EmotionRecordNotFoundException::new);
            //이미 존재하는 채팅방인지 확인
            if(chatRoomRepository.existsByRequestUserIdAndRecordId(requestUserId,emotionRecord)){
                return new ResponseResult(ErrorCode.CHAT_FAILED, "이미 존재하는 채팅방입니다.");
            }

            Long responseUserId = emotionRecord.getUser().getUserId();

            ChatRoom chatRoom = ChatRoom.builder()
                    .requestUserId(requestUserId)
                    .recordId(emotionRecord)
                    .status(RoomStatus.WAITING) //상태 : 대기
                    .startTime(new Timestamp(System.currentTimeMillis()))
                    .endTime(null)
                    .build();

            //DB에 저장
            chatRoomRepository.save(chatRoom);

            ChatReqDto chatReqDto = new ChatReqDto(userId, responseUserId);
            //레디스에 저장
            redisTemplate.opsForValue().set("Room::"+chatRoom.getChatRoomId(), String.valueOf(chatReqDto));

            return new ResponseResult(ErrorCode.SUCCESS, chatRoom);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    //채팅방 닫기
    @Transactional
    public ResponseResult closeChatRoom(@AuthenticationPrincipal Long userId, Long chatRoomId) {
        try {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(ChatRoomNotFoundException::new);

            //요청자 또는 응답자가 아니면 예외 처리
            if(!chatRoom.getRequestUserId().getUserId().equals(userId) &&
                    !chatRoom.getRecordId().getUser().getUserId().equals(userId)) {
                throw new UnauthorizedAccessException();//권한이 없을 경우 예외 발생
            }

            chatRoom.updateChatRoomStatus(RoomStatus.CLOSED); //삳태 '닫기'로 변경
            chatRoomRepository.save(chatRoom);//DB에 저장

            redisTemplate.delete("Room::"+chatRoomId);//레디스에서 삭제
            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
