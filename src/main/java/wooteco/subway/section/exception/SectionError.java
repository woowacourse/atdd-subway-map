package wooteco.subway.section.exception;

public enum SectionError {
    SAME_STATION_INPUT(400, "하행과 상행이 같은 역일 수 없습니다."),
    BOTH_STATION_IN_PATH(400, "구간의 역이 둘다 노선에 포함되어 있습니다."),
    NONE_STATION_IN_PATH(400, "구간의 역이 둘다 노선에 포함되어 있지 않습니다."),
    NO_SECTION_FOUND(400, "해당하는 구간은 찾을 수 없습니다."),
    CANNOT_DIVIDE_ORIGIN_SECTION(400, "해당 구간을 분리하여 갈림길 방지를 할 수 없습니다."),
    UNMATCHED_ADD_ERROR(500, "잘못된 추가 요청입니다."),
    CANNOT_FIND_DIFFERENT_STATION(500, "구간으로부터 다른 역을 찾을 수 없습니다"),
    CANNOT_DELETE_SECTION_SIZE_LESS_THAN_TWO(400, "구간이 하나인 노선에서 제거를 할 수 없습니다."),
    NO_STATION_TO_DELETE(400, "해당 삭제 요청에 해당하는 역을 찾을 수가 없습니다");


    private final int statusCode;
    private final String message;

    SectionError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

}
