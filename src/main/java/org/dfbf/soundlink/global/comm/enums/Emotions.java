package org.dfbf.soundlink.global.comm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Emotions {
    HAPPY("행복"),
    EXCITED("신남"),
    SAD("슬픔"),
    THRILLED("설렘"),
    CALM("평온"),
    ANNOYED("짜증"),
    WORRIED("걱정"),
    UNKNOWN("모름");

    private final String desc;
}