package com.tianji.learning.constants;

public interface RedisConstants {
    /*
    * 用户签到key的前缀 用户id : 签到日期
    * */
    String SIGN_RECORD_KEY_PREFIX = "sign:uid:";

    /*
    * 排行榜key的前缀
    * */
    String POINTS_BOARD_KEY_PREFIX = "boards";
}
