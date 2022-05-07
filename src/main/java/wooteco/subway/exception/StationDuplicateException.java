package wooteco.subway.exception;

public class StationDuplicateException extends SubwayValidationException {

    private static final String DUPLICATED_MESSAGE = "이미 존재하는 지하철역입니다.";

    public StationDuplicateException(String duplicateName) {
        super(DUPLICATED_MESSAGE + " : " + duplicateName);
    }
}
