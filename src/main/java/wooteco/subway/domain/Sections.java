package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> getAllStationId() {
        List<Long> stationsId = new ArrayList<>();
        addUpStationsId(stationsId);
        addDownStationsId(stationsId);
        return stationsId.stream()
            .distinct()
            .collect(Collectors.toList());
    }

    private void addUpStationsId(List<Long> ids) {
        sections.forEach(section -> ids.add(section.getUpStationId()));
    }

    private void addDownStationsId(List<Long> ids) {
        sections.forEach(section -> ids.add(section.getDownStationId()));
    }

    public boolean isEndSection(Section section) {
        if (isExistDownStation(section.getUpStationId()) && !isExistUpStation(section.getDownStationId())) {
            return true;
        }
        return !isExistDownStation(section.getUpStationId()) && isExistUpStation(section.getDownStationId());
    }

    private boolean isExistUpStation(Long id) {
        return sections.stream()
            .anyMatch(section -> section.getUpStationId().equals(id));
    }

    private boolean isExistDownStation(Long id) {
        return sections.stream()
            .anyMatch(section -> section.getDownStationId().equals(id));
    }

    public Section getSameStationSection(Section section) {
        validateExistUpAndDownStation(section);
        if (isExistUpStation(section.getUpStationId())) {
            Section findSection = findSameUpSection(section);
            validateSectionDistance(findSection, section);
            return findSection;
        }
        if (isExistDownStation(section.getDownStationId())) {
            Section findSection = findSameDownSection(section);
            validateSectionDistance(findSection, section);
            return findSection;
        }
        throw new IllegalStateException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
    }

    private Section findSameUpSection(Section section) {
        return sections.stream()
            .filter(it -> it.getUpStationId().equals(section.getUpStationId()))
            .findFirst()
            .get();
    }

    private Section findSameDownSection(Section section) {
        return sections.stream()
            .filter(it -> it.getDownStationId().equals(section.getDownStationId()))
            .findFirst()
            .get();
    }

    private void validateExistUpAndDownStation(Section section) {
        if (isExistUpStation(section.getUpStationId()) && isExistDownStation(section.getDownStationId())) {
            throw new IllegalStateException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }
    }

    private void validateSectionDistance(Section existing, Section additional) {
        if (existing.getDistance() <= additional.getDistance()) {
            throw new IllegalStateException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }
    }
}
