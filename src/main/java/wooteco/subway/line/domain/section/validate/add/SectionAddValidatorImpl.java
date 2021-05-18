package wooteco.subway.line.domain.section.validate.add;

import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.domain.section.Sections;

public class SectionAddValidatorImpl implements SectionAddValidator {

    public void validatePossibleToAdd(Sections sections, Section newSection) {
        boolean existUpStation = sections.isExistStationId(newSection.getUpStationId());
        boolean existDownStation = sections.isExistStationId(newSection.getDownStationId());
        validateNotExistSectionOfStation(existUpStation, existDownStation);
        validateAlreadyExistSectionOfStation(existUpStation, existDownStation);
    }

    private void validateNotExistSectionOfStation(boolean existUpStation, boolean existDownStation) {
        if (!existUpStation && !existDownStation) {
            throw new IllegalArgumentException("연결할 수 있는 역이 구간내에 없습니다.");
        }
    }

    private void validateAlreadyExistSectionOfStation(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 존재합니다.");
        }
    }
}
