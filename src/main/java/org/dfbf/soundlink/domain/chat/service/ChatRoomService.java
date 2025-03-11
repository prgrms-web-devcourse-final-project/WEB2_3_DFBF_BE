package org.dfbf.soundlink.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.alert.dto.AlertChatRequest;
import org.dfbf.soundlink.domain.alert.entity.Alert;
import org.dfbf.soundlink.domain.alert.service.AlertService;
import org.dfbf.soundlink.domain.blocklist.repository.BlockListRepository;
import org.dfbf.soundlink.domain.chat.dto.ChatRejectDto;
import org.dfbf.soundlink.domain.chat.dto.ChatReqDto;
import org.dfbf.soundlink.domain.chat.dto.ChatRoomInfoDto;
import org.dfbf.soundlink.domain.chat.dto.ChatRoomListDto;
import org.dfbf.soundlink.domain.chat.entity.ChatRoom;
import org.dfbf.soundlink.domain.chat.entity.redis.ChatRequest;
import org.dfbf.soundlink.domain.chat.exception.ChatRoomNotFoundException;
import org.dfbf.soundlink.domain.chat.exception.UnauthorizedAccessException;
import org.dfbf.soundlink.domain.chat.repository.ChatRoomRepository;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.exception.EmotionRecordNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.exception.UserNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.exception.NoUserDataException;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.domain.user.service.UserStatusService;
import org.dfbf.soundlink.global.comm.enums.RoomStatus;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.dfbf.soundlink.global.feign.chat.DevChatClient;
import org.dfbf.soundlink.global.kafka.KafkaProducer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EmotionRecordRepository emotionRecordRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final BlockListRepository blockListRepository;
    private final AlertService alertService;
    private final DevChatClient devChatClient;
    private final KafkaProducer kafkaProducer;
    private final UserStatusService userStatusService;

    private static final String CHAT_REQUEST_KEY = "chatRequest";
    private static final String TOPIC = "alert-topic";

    // 요청을 Redis에 저장 (TTL: 60초)
    public ResponseResult saveRequestToRedis(Long requestUserId, Long emotionRecordId) {
        try {
            // 응답자의 ID를 EmotionRecord에서 가져옴
            Long responseUserId = emotionRecordRepository.findById(emotionRecordId)
                    .orElseThrow(EmotionRecordNotFoundException::new)
                    .getUser()
                    .getUserId();

            // 요청자와 응답자가 같은 경우
            if (requestUserId.equals(responseUserId)) {
                return new ResponseResult(400, "You can't chat with yourself.");
            }

            // Redis에 이미 requestUserId가 포함되어 있는 경우
            if (!redisTemplate.keys(CHAT_REQUEST_KEY + requestUserId + "to*").isEmpty()) {
                String firstKey = redisTemplate.keys(CHAT_REQUEST_KEY + requestUserId + "to*").iterator().next(); // 첫 번째 키 가져오기
                Long ttl = redisTemplate.getExpire(firstKey);
                return new ResponseResult(400, ttl + "초 후에 다시 시도해주세요.");
            }

            // 이미 요청이 있는지 확인(Redis에 emotionRecordId에 대한 요청이 있는지 확인)
            Set<String> existIngKeys = redisTemplate.keys(CHAT_REQUEST_KEY + "*to" + emotionRecordId + "*");
            if(!existIngKeys.isEmpty()){
                //이미 요청이 있을 경우, 예외처리
                log.info(existIngKeys.toString());
                return new ResponseResult(400, "Another user has already sent a request for this record.");
            }

            // 응답자가 요청자를 차단한 경우
            if (blockListRepository.existsByUser_UserIdAndBlockedUser_UserId(responseUserId, requestUserId)) {
                return new ResponseResult(400, "Blocked user.");
            }

            // Key & Request 객체 생성
            String key = CHAT_REQUEST_KEY + requestUserId + "to" + emotionRecordId;
            ChatRequest chatRequest = new ChatRequest(requestUserId, responseUserId, emotionRecordId);

            // Redis 저장
            redisTemplate.opsForValue().set(key, chatRequest, Duration.ofSeconds(60));

            // 알림 전송
            User requestUser = userRepository.findByUserIdWithCache(requestUserId)
                    .orElseThrow(UserNotFoundException::new);
            AlertChatRequest alertChatRequest = new AlertChatRequest(emotionRecordId, requestUser.getNickname());
            Alert alert = alertService.createAlert(responseUserId, "alarm", alertChatRequest);
            kafkaProducer.send(TOPIC, alert);

            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (EmotionRecordNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD);
        } catch (UserNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseResult(400, "Chat request failed.");
        }
    }

    // 요청을 삭제
    public ResponseResult deleteRequestFromRedis(Long userId, Long emotionRecordId) {
        try {
            Long recordIdInUserId = emotionRecordRepository.findUserIdByRecordId(emotionRecordId)
                    .orElseThrow(EmotionRecordNotFoundException::new);

            // Key 생성
            String key = CHAT_REQUEST_KEY + userId + "to" + emotionRecordId;

            // Redis에 Key가 존재하는 경우 삭제 (KEY가 없는 경우 400)
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.delete(key);
                Alert alert = alertService.createAlert(recordIdInUserId, "cancel", "Chat request has been canceled.");
                kafkaProducer.send(TOPIC, alert);
                log.info("tset");
                return new ResponseResult(ErrorCode.SUCCESS);
            } else {
                return new ResponseResult(400, "ChatRequest not found or expired.");
            }

        } catch (EmotionRecordNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD);
        } catch (UserNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseResult(400, "Chat request failed.");
        }
    }

    // 요청 거절
    public ResponseResult requestRejected(Long responseUserId, ChatRejectDto chatRejectDto) {
        try {
            Long recordIdInUserId = emotionRecordRepository.findUserIdByRecordId(chatRejectDto.emotionRecordId())
                    .orElseThrow(EmotionRecordNotFoundException::new);

            if (!responseUserId.equals(recordIdInUserId)) {
                return new ResponseResult(400, "응답자만 요청을 거절할 수 있습니다.");
            }

            // Key 생성
            Long requestUserId = userRepository.findUserIdByNickname(chatRejectDto.requestNickname())
                    .orElseThrow(UserNotFoundException::new);
            String key = CHAT_REQUEST_KEY + requestUserId + "to" + chatRejectDto.emotionRecordId();

            // Redis에 Key가 존재하는 경우 삭제 (KEY가 없는 경우 400)
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.delete(key);
                Alert alert = alertService.createAlert(requestUserId, "fail", "채팅 요청을 거부했습니다");
                kafkaProducer.send(TOPIC, alert);
                return new ResponseResult(ErrorCode.SUCCESS);
            } else {
                return new ResponseResult(400, "ChatRequest not found or expired.");
            }

        } catch (EmotionRecordNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD);
        } catch (UserNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseResult(400, "Chat request failed.");
        }
    }

    // 채팅방 생성 (요청 수락)
    @Transactional
    public ResponseResult createChatRoom(Long userId, Long recordId, String requestNickname) {
        try {
            Long recordIdInUserId = emotionRecordRepository.findUserIdByRecordId(recordId)
                    .orElseThrow(EmotionRecordNotFoundException::new);

            if (!userId.equals(recordIdInUserId)) {
                return new ResponseResult(400, "응답자만 요청을 수락할 수 있습니다.");
            }

            // 요청 보낸사람
            Long requestUserId = userRepository.findUserIdByNickname(requestNickname)
                    .orElseThrow(UserNotFoundException::new);
            User user = userRepository.findByUserIdWithCache(requestUserId)
                    .orElseThrow(UserNotFoundException::new);

            // Key 생성
            String key = CHAT_REQUEST_KEY + requestUserId + "to" + recordId;

            // Redis에 Key가 존재하는 경우 삭제 & 방생성 (KEY가 없는 경우 400)
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.delete(key);

                // 감정기록 조회
                EmotionRecord emotionRecord = emotionRecordRepository.findById(recordId)
                        .orElseThrow(EmotionRecordNotFoundException::new);

                // 이미 존재하는 채팅방인지 확인
                Optional<Long> chatRoomId = chatRoomRepository.findChatRoomIdByRequestUserIdAndRecordId(requestUserId, recordId);
                if (chatRoomId.isPresent()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("chatRoomId", chatRoomId.get());
                  
                    Alert alert = alertService.createAlert(requestUserId, "accept", map);
                    kafkaProducer.send(TOPIC, alert);
                  
                    return new ResponseResult(map);
                }

                Long responseUserId = emotionRecord.getUser().getUserId();

                ChatRoom chatRoom = ChatRoom.builder()
                        .requestUserId(user)
                        .recordId(emotionRecord)
                        .status(RoomStatus.CONNECTED) //상태 : 대기
                        .startTime(new Timestamp(System.currentTimeMillis()))
                        .endTime(null)
                        .build();

                // DB에 저장
                chatRoomRepository.save(chatRoom);

                ChatReqDto chatReqDto = new ChatReqDto(userId, responseUserId);

                // 레디스에 저장
                redisTemplate.opsForValue().set("Room::" + chatRoom.getChatRoomId(), String.valueOf(chatReqDto));

                // ChatRoomId Map에 저장
                Map<String, Object> map = new HashMap<>();
                map.put("chatRoomId", chatRoom.getChatRoomId());

                // 요청자에게 방번호를 보냄
                Alert alert = alertService.createAlert(requestUserId, "accept", map);
                kafkaProducer.send(TOPIC, alert);

                userStatusService.setChatting(userId, true);

                return new ResponseResult(ErrorCode.SUCCESS, map);

            } else {
                return new ResponseResult(400, "ChatRequest not found or expired.");
            }
        } catch (EmotionRecordNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD, e.getMessage());
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER, e.getMessage());
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // 채팅방 닫기
    @Transactional
    public ResponseResult closeChatRoom(@AuthenticationPrincipal Long userId, Long chatRoomId) {
        try {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(ChatRoomNotFoundException::new);

            // 요청자 또는 응답자가 아니면 예외 처리
            if(!chatRoom.getRequestUserId().getUserId().equals(userId) &&
                    !chatRoom.getRecordId().getUser().getUserId().equals(userId)) {
                throw new UnauthorizedAccessException(); // 권한이 없을 경우 예외 발생
            }

            chatRoom.updateChatRoomStatus(RoomStatus.CLOSED); // 삳태 '닫기'로 변경
            chatRoomRepository.save(chatRoom); // DB에 저장

            redisTemplate.delete("Room::"+chatRoomId); // 레디스에서 삭제

            userStatusService.setChatting(userId, false);

            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    //채팅방 목록 불러오기
    public ResponseResult getChatRoomList(@AuthenticationPrincipal Long userId) {
        try {
            List<ChatRoom> chatRooms = chatRoomRepository.findByRequestUserIdOrderByCreatedAtDesc(userId);

            List<ChatRoomListDto> chatRoomList = chatRooms.stream()
                    .map(chatRoom -> new ChatRoomListDto(
                            chatRoom.getChatRoomId(),
                            chatRoom.getRecordId().getRecordId().toString(),
                            chatRoom.getRecordId().getUser().getNickname(),
                            chatRoom.getRecordId().getEmotion().name(),
                            chatRoom.getRecordId().getSpotifyMusic().getSpotifyId(),
                            chatRoom.getRecordId().getSpotifyMusic().getTitle(),
                            chatRoom.getRecordId().getSpotifyMusic().getArtist(),
                            chatRoom.getRecordId().getSpotifyMusic().getAlbumImage(),
                            chatRoom.getRecordId().getSpotifyMusic().getVideoId(),
                            chatRoom.getRecordId().getComment(),
                            chatRoom.getCreatedAt()
                    ))
                    .toList();
            return new ResponseResult(ErrorCode.SUCCESS, chatRoomList);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    //채팅방 상세 정보 조회
    public ResponseResult getChatRoomInfo(Long chatRoomId, Long userId) {
        try {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(ChatRoomNotFoundException::new);

            // 채팅방 요청자 또는 응답자가 아닌 경우 예외 처리
            if (!chatRoom.getRequestUserId().getUserId().equals(userId) &&
                    !chatRoom.getRecordId().getUser().getUserId().equals(userId)) {
                throw new UnauthorizedAccessException(); // 권한이 없으면 예외 발생
            }

            ChatRoomInfoDto infoDto = new ChatRoomInfoDto(
                    chatRoom.getRecordId().getSpotifyMusic().getSpotifyId(),
                    chatRoom.getRecordId().getSpotifyMusic().getTitle(),
                    chatRoom.getRecordId().getSpotifyMusic().getArtist(),
                    chatRoom.getRecordId().getSpotifyMusic().getAlbumImage(),
                    chatRoom.getRecordId().getSpotifyMusic().getVideoId(),
                    chatRoom.getStatus().name(),
                    chatRoom.getCreatedAt()
            );
            return new ResponseResult(ErrorCode.SUCCESS, infoDto);
        } catch (ChatRoomNotFoundException e) {
            return new ResponseResult(ErrorCode.CHATROOM_NOT_FOUND, "채팅방을 찾을 수 없습니다.");
        }catch (UnauthorizedAccessException e) {
            return new ResponseResult(ErrorCode.CHAT_UNAUTHORIZED,"권한이 없습니다.");
        }catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, "채팅방 세부 정보를 가져오는 데 실패했습니다.");
        }
    }

    // (개발서버 전용) 채팅방 내 채팅 내역 가져오기
    public ResponseResult getChatHistoryResponse(Long userId, String chatRoomId){
        return devChatClient.getChatHistoryDev(chatRoomId, userId);
    }
}
