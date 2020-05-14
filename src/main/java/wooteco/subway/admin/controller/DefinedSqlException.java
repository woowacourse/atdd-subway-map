package wooteco.subway.admin.controller;

public class DefinedSqlException extends RuntimeException {
    static final String DUPLICATED_NAME = "이미 저장된 이름입니다.";

    public DefinedSqlException(String s) {
        super(s);
    }
}
