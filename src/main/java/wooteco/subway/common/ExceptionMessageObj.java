package wooteco.subway.common;

public class ExceptionMessageObj {
    private String errorMessage;

    private ExceptionMessageObj() {}

    public ExceptionMessageObj(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
