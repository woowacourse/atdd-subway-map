package wooteco.subway.exception;

public class StationNameDuplicateException extends SubwayValidationException {

    private static final String DUPLICATED_MESSAGE = "이미 존재하는 지하철역 이름입니다 : ";

    public StationNameDuplicateException(String duplicateName) {
        super(DUPLICATED_MESSAGE + duplicateName);
    }
}
