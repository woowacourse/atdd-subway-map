package wooteco.subway.line.domain.section.validate.delete;

import wooteco.subway.line.domain.section.Sections;

public class SectionDeleteValidatorImpl implements SectionDeleteValidator {
    private static final int DELETE_LIMIT_SIZE = 1;

    @Override
    public void validateDeleteSection(Sections sections, Long stationId) {
        validateExistStationId(sections, stationId);
        validateDeleteSize(sections);
    }

    private void validateExistStationId(Sections sections, Long stationId) {
        if (sections.isExistStationId(stationId)) {
            return;
        }
        throw new IllegalArgumentException("삭제하려는 역을 포함하는 구간이 존재하지 않습니다.");
    }

    private void validateDeleteSize(Sections sections) {
        if (sections.toList().size() <= DELETE_LIMIT_SIZE) {
            throw new IllegalStateException("구간이 하나 이하일 때는 삭제할 수 없습니다.");
        }
    }
}
