package wooteco.subway.domain;

import java.util.List;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateEmpty(sections);
        this.sections = sections;
    }

    private void validateEmpty(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("구간이 존재하지 않습니다.");
        }
    }

    public void validateSectionInLine(Section newSection) {
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

    public void validateSectionDistance(Section newSection) {
        if (newSection.getDistance() >= getExistSection(newSection).getDistance()) {
            throw new IllegalArgumentException("구간의 길이는 기존 역 사이의 길이보다 작아야합니다.");
        }
    }

    private Section getExistSection(Section newSection) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(newSection.getUpStationId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 구간입니다."));
    }

    public Section getUpdatedSection(Section newSection) {
        Section existSection = getExistSection(newSection);
        int newDistance = existSection.getDistance() - newSection.getDistance();

        if (!includeUpStation(newSection.getDownStationId()) && includeDownStation(newSection.getDownStationId())) {
            return new Section(existSection.getId(), existSection.getUpStationId(),
                    newSection.getUpStationId(), newDistance);
        }

        return new Section(existSection.getId(), newSection.getDownStationId(),
                existSection.getDownStationId(), newDistance);
    }

    public boolean isRequireUpdate(Section newSection) {
        if (!includeUpStation(newSection.getDownStationId()) && includeDownStation(newSection.getDownStationId())) {
            return true;
        }
        return includeUpStation(newSection.getUpStationId()) && !includeDownStation(newSection.getUpStationId());
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}
