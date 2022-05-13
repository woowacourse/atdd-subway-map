package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidSubwayResourceException;

public class NonExistenceSectionDeletion extends InvalidSubwayResourceException {

    private static final String MESSAGE = "존재하지 않는 구간은 삭제할 수 없습니다.";

    private static final NonExistenceSectionDeletion INSTANCE = new NonExistenceSectionDeletion();

    public static NonExistenceSectionDeletion getInstance() {
        return INSTANCE;
    }

    private NonExistenceSectionDeletion() {
        super(MESSAGE);
    }
}
