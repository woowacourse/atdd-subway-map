package wooteco.subway.admin.domain.line.relation;

public class InvalidLineStationException extends IllegalArgumentException {
    static final String NOT_EXIST_ID =  "해당 아이디를 가진 LineStation이 존재하지 않습니다. ID = ";

    public InvalidLineStationException(String s) {
        super(s);
    }

    public InvalidLineStationException(String s, Long id) {
        this(s + id.toString());
    }
}
