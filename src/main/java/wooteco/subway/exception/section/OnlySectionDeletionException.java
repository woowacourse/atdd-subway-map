package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidSubwayResourceException;

public class OnlySectionDeletionException extends InvalidSubwayResourceException {

    private static final String MESSAGE = "구간이 하나인 노선의 구간은 삭제할 수 없습니다.";

    private static final OnlySectionDeletionException INSTANCE = new OnlySectionDeletionException();

    public static OnlySectionDeletionException getInstance() {
        return INSTANCE;
    }

    private OnlySectionDeletionException() {
        super(MESSAGE);
    }
}
