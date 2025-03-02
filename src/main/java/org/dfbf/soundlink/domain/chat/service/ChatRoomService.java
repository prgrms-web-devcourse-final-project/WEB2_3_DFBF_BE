package org.dfbf.soundlink.domain.chat.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.chat.dto.ChatReqDto;
import org.dfbf.soundlink.domain.chat.entity.ChatRoom;
import org.dfbf.soundlink.domain.chat.exception.ChatRoomNotFoundException;
import org.dfbf.soundlink.domain.chat.repository.ChatRoomRepository;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.exception.EmotionRecordNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.exception.UserNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.auth.JwtProvider;
import org.dfbf.soundlink.global.comm.enums.RoomStatus;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final JwtProvider jwtProvider;

    @Transactional
    public ResponseResult createChatRoom(HttpServletRequest request, Long recordId){
        try {
            String accessToken = jwtProvider.resolveAccessToken(request); //AT 추출
            Long userId = jwtProvider.getUserId(accessToken);

            //요청 보내는사람
            User requestUserId = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);

            //감정기록 조회
            EmotionRecord emotionRecord = emotionRecordRepository.findById(recordId)
                    .orElseThrow(EmotionRecordNotFoundException::new);

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
        }catch (DataIntegrityViolationException e) {
            return new ResponseResult(ErrorCode.CHAT_FAILED, "채팅방 생성 실패: 이미 존재하는 데이터입니다."); // recordId 값 중복 시
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    //채팅방 닫기
    @Transactional
    public ResponseResult closeChatRoom(Long chatRoomId) {
        try {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(ChatRoomNotFoundException::new);
            chatRoom.updateChatRoomStatus(RoomStatus.CLOSED); //삳태 '닫기'로 변경
            chatRoomRepository.save(chatRoom);//DB에 저장

            redisTemplate.delete("Room::"+chatRoomId);//레디스에서 삭제
            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
