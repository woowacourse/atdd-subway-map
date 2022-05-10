package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    private final List<Section> sections;

    public Sections() {
        this(new ArrayList<>());
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        if (!sections.isEmpty()) {
            validateSection(section);
        }
        sections.add(section);
    }

    private void validateSection(Section addSection) {
        boolean existUpStation = existStationByStationId(addSection.getUpStationId());
        boolean existDownStation = existStationByStationId(addSection.getDownStationId());

        validateBothStationsIncludeInLine(existUpStation, existDownStation);
        validateBothStationsExcludeInLine(existUpStation, existDownStation);
    }

    private boolean existStationByStationId(Long stationId) {
        return sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(stationId)
                        || section.getDownStationId().equals(stationId));
    }

    private void validateBothStationsExcludeInLine(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 모두 노선에 포함되어 있습니다.");
        }
    }

    private void validateBothStationsIncludeInLine(boolean existUpStation, boolean existDownStation) {
        if (!(existUpStation || existDownStation)) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 노선에 포함되어있지 않습니다.");
        }
    }
}
