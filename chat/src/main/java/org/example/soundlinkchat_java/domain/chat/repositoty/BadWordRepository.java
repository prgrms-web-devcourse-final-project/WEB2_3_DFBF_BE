package org.example.soundlinkchat_java.domain.chat.repositoty;

import java.util.Set;

// 건전한 인터넷 문화를 만들어 갑시다. 제대로 숭 하네요
public class BadWordRepository {
    public static final Set<String> BAD_WORDS = Set.of(
            "시발","병신","염병","느금","ㅅㅂ","ㅂㅅ","ㄴㄱㅁ",
            "개새끼","빨갱이","좆","썅"
    );
}
