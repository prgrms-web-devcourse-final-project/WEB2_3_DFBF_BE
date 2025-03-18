package org.example.soundlinkchat_java.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 에러 코드 예시
    TEST_STATUS(HttpStatus.OK, "health"),

    // 성공 (Success, 200)
    SUCCESS(HttpStatus.OK, "성공"),

    // DB
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB 연결 에러"),

    // 클라이언트 오류 (Client Error, 400)
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Validation Error"),

    // 서버 관련 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 에러"),

    // Feign 관련 오류
    KAKAO_API_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "카카오 API 인증 실패 (401)"),
    KAKAO_API_FORBIDDEN(HttpStatus.FORBIDDEN, "카카오 API 접근 권한 없음 (403)"),
    KAKAO_API_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청 (400)"),
    KAKAO_API_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 서버 오류 (500)"),

    // User
    FAIL_TO_FIND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다"),
    NOT_EQUALS_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용중인 이메일입니다"),
    NOT_DUPLICATE_EMAIL(HttpStatus.OK, "사용 가능한 이메일입니다"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT,"이미 사용중인 닉네임입니다."),
    NOT_DUPLICATE_NICKNAME(HttpStatus.OK,"사용 가능한 닉네임입니다."),
    DUPLICATE_LOGINID(HttpStatus.CONFLICT,"이미 사용중인 로그인ID입니다."),
    NOT_DUPLICATE_LOGINID(HttpStatus.OK,"사용 가능한 로그인ID입니다."),

    //Email
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"이메일 전송 중 오류 발생"),

    // Block 관련 오류 (Block Error)
    BLOCKED_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "차단된 유저의 아이디를 찾을 수 없습니다."),
    BLOCKING_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "차단을 시도한 유저의 아이디를 찾을 수 없습니다."),
    ALREADY_BLOCKED_USER(HttpStatus.BAD_REQUEST, "이미 차단한 유저입니다."),

    // EmotionRecord
    FAIL_TO_FIND_EMOTION_RECORD(HttpStatus.NOT_FOUND, "해당 감정 기록을 찾을 수 없습니다."),
    FAIL_TO_FIND_EMOTION(HttpStatus.NOT_FOUND, "해당 감정을 찾을 수 없습니다."),
    INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST, "페이지 요청 값이 잘못되었습니다."),

    // SpotifyMusic
    FAIL_TO_FIND_SPOTIFY_MUSIC(HttpStatus.NOT_FOUND, "해당 SpotifyId를 찾을 수 없습니다."),

    // Auth
    TOKEN_NOT_EXPIRED(HttpStatus.OK, "토큰 정상"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰 만료됨"),
    TOKEN_TAMPERED(HttpStatus.UNAUTHORIZED, "토큰 변조됨"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰"),

    // 카카오페이 결제 에러
    KAKAOPAY_READY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오페이 결제 준비 에러"),
    KAKAOPAY_APPROVE_REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "TID가 REDIS 안에 없습니다."),
    KAKAOPAY_APPROVE_FEIGN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오페이 승인 API 통신 실패"),
    KAKAOPAY_APPROVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오페이 결제 승인 에러"),

    //채팅방 관련 에러
    CHAT_UNAUTHORIZED(HttpStatus.FORBIDDEN,"권한이 없습니다"),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND,"채팅방을 찾을 수 없습니다."),
    CHAT_FAILED(HttpStatus.CONFLICT,"서버 내부 에러. 중복된 레코드가 존재합니다.");


    private final HttpStatus status;
    private final String message;
}
