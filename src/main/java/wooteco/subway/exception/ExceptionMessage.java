package wooteco.subway.exception;

public enum ExceptionMessage {
    DUPLICATED_STATION_NAME("중복된 이름의 역은 저장할 수 없습니다."),
    DUPLICATED_LINE_NAME("중복된 이름의 노선은 저장할 수 없습니다."),
    BLANK_STATION_NAME("역의 이름이 공백이 되어서는 안됩니다."),
    BLANK_LINE_NAME("노선의 이름이 공백이 되어서는 안됩니다."),
    UNKNOWN_DELETE_STATION_FAIL("알 수 없는 이유로 역을 삭제하지 못했습니다."),
    UNKNOWN_DELETE_LINE_FAIL("알 수 없는 이유로 노선을 삭제하지 못했습니다."),
    NOT_FOUND_LINE_BY_ID("해당 ID에 맞는 노선을 찾지 못했습니다."),
    OVER_MAX_LENGTH_STATION_NAME("역의 이름이 %d자를 넘어서는 안됩니다."),
    ;

    private final String content;

    ExceptionMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}
