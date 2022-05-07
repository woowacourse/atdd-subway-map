package wooteco.subway.exception;

public class StationDuplicateException extends RuntimeException {

    private static final String DUPLICATED_MESSAGE = "이미 존재하는 지하철역입니다.";

    public StationDuplicateException() {
        super(DUPLICATED_MESSAGE);
    }

    public StationDuplicateException(final String inputtedData) {
        super(DUPLICATED_MESSAGE + " " + inputtedData);
    }
}
