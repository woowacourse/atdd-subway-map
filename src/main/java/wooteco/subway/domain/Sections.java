package wooteco.subway.domain;

import java.util.List;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section newSection) {
        if (!sections.isEmpty()) {
            validateSectionInLine(newSection);

            if (!includeUpStation(newSection.getDownStationId()) && includeDownStation(newSection.getDownStationId())) {
                validateSectionDistance(newSection);
                // update
            }
            if (includeUpStation(newSection.getUpStationId()) && !includeDownStation(newSection.getUpStationId())) {
                validateSectionDistance(newSection);
                // update
            }
        }
        sections.add(newSection);
    }

    private void validateSectionInLine(Section newSection) {
        boolean existUpStation = existStationByStationId(newSection.getUpStationId());
        boolean existDownStation = existStationByStationId(newSection.getDownStationId());

        validateBothStationsIncludeInLine(existUpStation, existDownStation);
        validateBothStationsExcludeInLine(existUpStation, existDownStation);
    }

    private boolean existStationByStationId(Long stationId) {
        return includeUpStation(stationId) || includeDownStation(stationId);
    }

    private boolean includeUpStation(Long stationId) {
        return sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(stationId));
    }

    private boolean includeDownStation(Long stationId) {
        return sections.stream()
                .anyMatch(section -> section.getDownStationId().equals(stationId));
    }

    private void validateBothStationsIncludeInLine(boolean existUpStation, boolean existDownStation) {
        if (!(existUpStation || existDownStation)) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 노선에 포함되어있지 않습니다.");
        }
    }

    private void validateBothStationsExcludeInLine(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 모두 노선에 포함되어 있습니다.");
        }
    }

    private void validateSectionDistance(Section newSection) {
        if (newSection.getDistance() >= getExistDistance(newSection)) {
            throw new IllegalArgumentException("구간의 길이는 기존 역 사이의 길이보다 작아야합니다.");
        }
    }

    private int getExistDistance(Section newSection) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(newSection.getUpStationId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 구간입니다."))
                .getDistance();
    }
}
