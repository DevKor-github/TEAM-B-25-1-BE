package com.ODG.ODG_back.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    MEETING_NOT_FOUND("404", "해당 초대 코드의 모임이 존재하지 않습니다."),
    PARTICIPANT_NOT_FOUND("404", "해당 참가자가 존재하지 않습니다."),
    PLACE_NOT_FOUND("404", "해당 장소가 존재하지 않습니다."),
    RECOMMENDED_MIDPOINT_NOT_FOUND("404", "해당 모임에 대한 중간지점이 존재하지 않습니다."),

    DATA_INTEGRITY_VIOLATION("400", "중복 혹은 유효하지 않은 데이터입니다."),
    BAD_REQUEST("400", "잘못된 요청입니다."),
    INVALID_ARGUMENTS("400", "잘못된 인자가 전달되었습니다."),

    UNAUTHORIZED("401", "인증되지 않았습니다."),
    INTERNAL_SERVER_ERROR("500", "서버 내부 오류가 발생했습니다."),

    EXTERNAL_API_CONNECTION_FAILED("503", "외부 API에 연결 실패했습니다."),
    EXTERNAL_API_RESPONSE_ERROR("502", "외부 API 응답 상태 오류");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
