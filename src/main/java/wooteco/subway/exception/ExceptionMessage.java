package wooteco.subway.exception;

public enum ExceptionMessage {
    DUPLICATED_STATION_NAME("중복된 이름의 역은 저장할 수 없습니다."),
    DUPLICATED_LINE_NAME("중복된 이름의 노선은 저장할 수 없습니다."),
    BLANK_STATION_NAME("역의 이름이 공백이 되어서는 안됩니다."),
    BLANK_LINE_NAME("노선의 이름이 공백이 되어서는 안됩니다."),
    UNKNOWN_DELETE_STATION_FAIL("알 수 없는 이유로 역을 삭제하지 못했습니다."),
    UNKNOWN_DELETE_LINE_FAIL("알 수 없는 이유로 노선을 삭제하지 못했습니다."),
    OVER_MAX_LENGTH_STATION_NAME("역의 이름이 %d자를 넘어서는 안됩니다."),
    INSERT_SECTION_NOT_MATCH("삽입할 구간이 연결될 역이 없습니다."),
    INVALID_INSERT_SECTION_DISTANCE("삽입 되는 구간의 길이가 기존의 구간보다 깁니다."),
    SAME_STATIONS_SECTION("같은 출발지와 도착지를 가진 구간은 진행 할 수 없습니다."),
    NOT_CONNECTED_SECTIONS("구간이 서로 연결되지 않았습니다."),
    INSERT_DUPLICATED_SECTION("이미 연결된 구간입니다."),
    INVALID_DIVIDE_SECTION("역을 쪼개지 못하는 경우입니다."),
    NEAR_SECTIONS_OVER_SIZE("근접한 역이 너무 많습니다."),
    SECTIONS_NOT_DELETABLE("구간을 삭제할 수 없습니다."),

    NOT_FOUND_STATION("해당 역을 찾지 못했습니다."),
    NOT_FOUND_LINE("해당 노선을 찾지 못했습니다."),
    NOT_FOUND_SECTION("해당 구간을 찾지 못했습니다.");

    private final String content;

    ExceptionMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}
