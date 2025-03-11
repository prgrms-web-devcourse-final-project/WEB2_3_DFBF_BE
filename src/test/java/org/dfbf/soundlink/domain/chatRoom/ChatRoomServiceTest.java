package org.dfbf.soundlink.domain.chatRoom;

import org.dfbf.soundlink.domain.alert.entity.Alert;
import org.dfbf.soundlink.domain.alert.service.AlertService;
import org.dfbf.soundlink.domain.blocklist.repository.BlockListRepository;
import org.dfbf.soundlink.domain.chat.dto.ChatRejectDto;
import org.dfbf.soundlink.domain.chat.dto.ChatRoomInfoDto;
import org.dfbf.soundlink.domain.chat.entity.ChatRoom;
import org.dfbf.soundlink.domain.chat.repository.ChatRoomRepository;
import org.dfbf.soundlink.domain.chat.service.ChatRoomService;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.domain.user.service.UserStatusService;
import org.dfbf.soundlink.global.comm.enums.SocialType;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.dfbf.soundlink.global.comm.enums.Emotions.HAPPY;
import static org.dfbf.soundlink.global.comm.enums.RoomStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {
    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private EmotionRecordRepository emotionRecordRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BlockListRepository blockListRepository;

    @Mock
    private AlertService alertService;

    @Mock
    private UserStatusService userStatusService;

    private User requestUser;
    private User responseUser;
    private EmotionRecord emotionRecord;
    private ChatRejectDto chatRejectDto;

    private static final String CHAT_REQUEST_KEY = "chatRequest";
    private Long responseUserId = 2L;
    private Long requestUserId = 1L;
    private Long emotionRecordId = 123L;
    private String requestNickname = "testNickName";

    @BeforeEach
    void setUp() {
        // User 객체 생성
        requestUser = User.builder()
                .nickName("testUser")
                .socialId(1L)
                .socialType(null)
                .loginId("testLogin")
                .password("testPassword")
                .email("test@test.com")
                .build();

        // userId 강제로 설정
        ReflectionTestUtils.setField(requestUser, "userId", 1L);

        // ResponseUser 객체 생성 (이 감정 기록의 주인공)
        responseUser = User.builder()
                .nickName("responseUser")
                .socialId(123L)
                .socialType(SocialType.NONE)
                .loginId("responseLogin")
                .password("password")
                .email("responseUser@example.com")
                .build();

        ReflectionTestUtils.setField(responseUser, "userId", 2L);

        // EmotionRecord 객체 생성 (responseUser가 감정을 기록한 것)
        emotionRecord = EmotionRecord.builder()
                .user(responseUser)  // responseUser가 감정 기록을 남긴 것
                .emotion(HAPPY)
                .comment("Great day!")
                .build();


        chatRejectDto = new ChatRejectDto(emotionRecordId, requestNickname);
    }
    

    @Test
    @DisplayName("채팅 요청: Redis에 저장 성공")
    void testSaveRequestToRedis_SUCCESS() {
        // given
        when(emotionRecordRepository.findById(emotionRecordId)).thenReturn(Optional.of(emotionRecord));  // 감정 기록 조회
        when(userRepository.findByUserIdWithCache(requestUserId)).thenReturn(Optional.of(requestUser));  // 요청자 정보 조회
        when(redisTemplate.keys(anyString())).thenReturn(Set.of());  // 이미 요청이 존재하지 않도록 mock
        when(blockListRepository.existsByUser_UserIdAndBlockedUser_UserId(responseUserId, requestUserId)).thenReturn(false); // 차단된 사용자 없음

        // 알림 서비스 mock
//        when(alertService.send(eq(responseUserId), eq("alarm"), any(Alert.class)))
//                .thenReturn(new ResponseResult(ErrorCode.SUCCESS));  // 알림 전송 mock

        // Redis 관련 mock 설정
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);  // ValueOperations 객체 생성
        when(redisTemplate.opsForValue()).thenReturn(valueOps);  // redisTemplate에서 valueOps 반환하도록 설정

        // Redis에 값이 설정되는 동작 모킹
        doNothing().when(valueOps).set(anyString(), any(), any());  // set() 메서드가 아무 동작도 하지 않도록 mock

        // when
        ResponseResult result = chatRoomService.saveRequestToRedis(requestUserId, emotionRecordId);

        // then
        assertEquals(200, result.getCode());  // 성공 코드 확인

        // Redis에 값이 저장되었는지 확인
        verify(redisTemplate).opsForValue();  // Redis에 값 저장 메서드 호출 확인
        verify(alertService).send(eq(responseUserId), eq("alarm"), any(Alert.class));  // 알림 전송 메서드 호출 확인
    }

    @Test
    @DisplayName("채팅 요청 삭제")
    void testDeleteRequestFromRedis_SUCCESS() {
        // given
        when(emotionRecordRepository.findUserIdByRecordId(emotionRecordId)).thenReturn(Optional.of(responseUserId)); // EmotionRecord 조회 mock

        // Key가 존재한다고 가정
        when(redisTemplate.hasKey(CHAT_REQUEST_KEY + requestUserId + "to" + emotionRecordId)).thenReturn(true);

        // when
        ResponseResult result = chatRoomService.deleteRequestFromRedis(requestUserId, emotionRecordId);

        // then
        assertEquals(200, result.getCode()); // 성공 응답 코드
        assertEquals("성공", result.getMessage()); // 메시지 확인

        // Redis에서 키를 삭제했는지 확인
        verify(redisTemplate).delete(CHAT_REQUEST_KEY + requestUserId + "to" + emotionRecordId);

        // 알림 서비스 호출 여부 확인
        verify(alertService).send(responseUserId, "cancel", "Chat request has been canceled.");
    }

    @Test
    @DisplayName("채팅 요청 거절")
    void testRequestRejected_SUCCESS() {
        // given
        when(emotionRecordRepository.findUserIdByRecordId(emotionRecordId)).thenReturn(Optional.of(responseUserId));  // 감정 기록 조회 mock
        when(userRepository.findUserIdByNickname(requestNickname)).thenReturn(Optional.of(requestUserId));  // 요청자 ID 조회 mock

        // Key가 존재한다고 가정
        when(redisTemplate.hasKey(CHAT_REQUEST_KEY + requestUserId + "to" + emotionRecordId)).thenReturn(true);

        // when
        ResponseResult result = chatRoomService.requestRejected(responseUserId, chatRejectDto);

        // then
        assertEquals(200, result.getCode());  // 성공 응답 코드
        assertEquals("성공", result.getMessage());  // 메시지 확인

        // Redis에서 키를 삭제했는지 확인
        verify(redisTemplate).delete(CHAT_REQUEST_KEY + requestUserId + "to" + emotionRecordId);

        // 알림 서비스 호출 여부 확인
        verify(alertService).send(requestUserId, "fail", "채팅 요청을 거부했습니다");
    }




    @Test
    @DisplayName("채팅방 생성: 채팅 요청 수락 시")
    void testCreateChatRoom_Success() {
        Long userId = 2L;  // 응답자
        Long recordId = 123L;  // 감정기록 ID

        // Mock 설정
        when(emotionRecordRepository.findUserIdByRecordId(recordId)).thenReturn(Optional.of(userId));
        when(userRepository.findUserIdByNickname(requestNickname)).thenReturn(Optional.of(1L));
        when(userRepository.findByUserIdWithCache(1L)).thenReturn(Optional.of(requestUser));
        when(redisTemplate.hasKey(anyString())).thenReturn(true);  // Redis 키가 존재한다고 가정
        when(redisTemplate.delete(anyString())).thenReturn(true);  // Redis 키 삭제 성공
        when(emotionRecordRepository.findById(recordId)).thenReturn(Optional.of(emotionRecord));
        when(chatRoomRepository.findChatRoomIdByRequestUserIdAndRecordId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // Redis 관련 메서드에 대한 모킹 추가
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);  // ValueOperations (키-값 데이터 저장)
        when(redisTemplate.opsForValue()).thenReturn(valueOps);  // 가짜 객체 반환

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .requestUserId(requestUser)
                .recordId(emotionRecord)
                .status(CONNECTED)
                .startTime(new Timestamp(System.currentTimeMillis()))
                .endTime(null)
                .build();

        when(chatRoomRepository.save(Mockito.any(ChatRoom.class))).thenReturn(chatRoom);
        // 타입을 명시적으로 지정

        // 서비스 호출
        ResponseResult result = chatRoomService.createChatRoom(userId, recordId, requestNickname);

        // 결과 검증
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData() instanceof Map);

        // 레디스에 값이 저장되는지 확인
        verify(redisTemplate).opsForValue();
    }

    @Test
    @DisplayName("채팅방 닫기: 레디스에서 삭제, 상태 변경")
    void testCloseChatRoom_Success() {
        Long userId = 1L; // 요청자 or 응답자 아이디
        Long chatRoomId = 2L; // 닫을 채팅방 id

        // 채팅방 객체 생성 (사용자가 닫을 수 있는 상태)
        ChatRoom chatRoom = ChatRoom.builder()
                .requestUserId(requestUser)
                .recordId(emotionRecord)
                .status(CONNECTED)  // 기존 상태 - CONNECTED
                .startTime(new Timestamp(System.currentTimeMillis()))
                .endTime(null)
                .build();

        // Mock 설정
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom)); // 채팅방 조회 성공
        when(chatRoomRepository.save(Mockito.any(ChatRoom.class))).thenReturn(chatRoom); // 저장 성공
        when(redisTemplate.delete("Room::" + chatRoomId)).thenReturn(true); // 레디스 삭제 성공

        // userStatusService 모킹
        doNothing().when(userStatusService).setChatting(anyLong(), anyBoolean()); // setChatting 메서드가 아무 동작도 하지 않도록 설정

        ResponseResult result = chatRoomService.closeChatRoom(userId, chatRoomId);

        assertEquals(200, result.getCode());
        assertEquals(CLOSED, chatRoom.getStatus());  // 상태가 'CLOSED'로 변경되었는지 확인
        // 레디스에서 채팅방 정보 삭제되었는지 확인
        verify(redisTemplate).delete("Room::" + chatRoomId);
        verify(chatRoomRepository).save(any(ChatRoom.class));  // 채팅방 상태 변경 확인
        verify(userStatusService).setChatting(userId, false);  // userStatusService의 setChatting 메서드가 호출되었는지 확인
    }


    @DisplayName("채팅방 목록 조회 테스트")
    @Test
    void testGetChatRoomList() {
        //given
        Long userId =1L;

        SpotifyMusic mockMusic = SpotifyMusic.builder()
                .spotifyId("12345")
                .title("Test Song")
                .artist("Test Artist")
                .albumImage("test_image_url")
                .videoId("test_video_id")
                .build();

        EmotionRecord mockRecord = EmotionRecord.builder()
                .user(requestUser)
                .emotion(HAPPY)
                .comment("Feeling great!")
                .spotifyMusic(mockMusic)
                .build();
        ReflectionTestUtils.setField(mockRecord, "recordId", 1L);

        ChatRoom mockChatRoom = ChatRoom.builder()
                .requestUserId(requestUser)
                .recordId(mockRecord)
                .status(CONNECTED)
                .startTime(new Timestamp(System.currentTimeMillis()))
                .endTime(null)
                .build();

        List<ChatRoom> mockChatRooms = List.of(mockChatRoom);

        when(chatRoomRepository.findByRequestUserIdOrderByCreatedAtDesc(userId)).thenReturn(mockChatRooms);

        //when
        ResponseResult result = chatRoomService.getChatRoomList(userId);

        //then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());   //null값 유무 확인
        assertTrue(result.getData() instanceof List<?>); //List 타입 확인

        List<?> chatRoomList = (List<?>) result.getData();
        assertEquals(1, chatRoomList.size());
    }

    @Test
    @DisplayName("채팅방 상세 정보 조회: 정상적으로 조회될 때")
    void testGetChatRoomInfo_Success() {
        // given
        Long chatRoomId = 1L;
        Long userId = 1L; // 채팅방 요청자 또는 응답자 ID


        SpotifyMusic spotifyMusic = SpotifyMusic.builder()
                .spotifyId("123")
                .title("Test Song")
                .artist("Test Artist")
                .albumImage("test_image.jpg")
                .videoId("test_video")
                .build();

        EmotionRecord testEmotionRecord = EmotionRecord.builder()
                .user(requestUser)
                .spotifyMusic(spotifyMusic)
                .build();

        ChatRoom chatRoom = ChatRoom.builder()
                .requestUserId(requestUser) // 요청자
                .recordId(testEmotionRecord)
                .status(CONNECTED)
                .startTime(new Timestamp(System.currentTimeMillis()))
                .build();

        // when
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

        // 실행
        ResponseResult result = chatRoomService.getChatRoomInfo(chatRoomId, userId);

        // then
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData() instanceof ChatRoomInfoDto);

        ChatRoomInfoDto infoDto = (ChatRoomInfoDto) result.getData();
        assertEquals("123", infoDto.spotifyId());
        assertEquals("Test Song", infoDto.title());
        assertEquals("Test Artist", infoDto.artist());
    }
}
