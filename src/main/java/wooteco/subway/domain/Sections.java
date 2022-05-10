package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Sections {

    private static final int SECTIONS_MINIMUM_SIZE = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public void add(Section section) {
        boolean existUpStation = hasSameUpStation(section) || hasSameDownByUp(section);
        boolean existDownStation = hasSameDownStation(section) || hasSameUpByDown(section);
        validateAddSectionCondition(existUpStation, existDownStation);
        if (existUpStation) {
            addSplitByUpStation(section);
        }
        addSplitByDownStation(section);
        sections.add(section);
    }

    public void remove(long stationId) {
        Optional<Section> findSectionByUpStationId = sections.stream().filter(it -> it.findByUpStationId(stationId)).findFirst();
        Optional<Section> findSectionByDownStationId = sections.stream().filter(it -> it.findByDownStationId(stationId)).findFirst();
        validateExistStationId(findSectionByUpStationId, findSectionByDownStationId);
        validateMinimumListSize();
        if (findSectionByUpStationId.isPresent() && findSectionByDownStationId.isPresent()) {
            removeWayPointSection(findSectionByUpStationId.get(), findSectionByDownStationId.get());
            return;
        }
        if (findSectionByUpStationId.isPresent()) {
            sections.remove(findSectionByUpStationId.get());
            return;
        }
        sections.remove(findSectionByDownStationId.get());
    }

    private void removeWayPointSection(Section firstSection, Section secondSection) {
        Section newSection = new Section(secondSection.getUpStationId(), firstSection.getDownStationId(), firstSection.getDistance() + secondSection.getDistance());
        sections.remove(firstSection);
        sections.remove(secondSection);
        sections.add(newSection);
    }

    private void validateAddSectionCondition(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new IllegalArgumentException("[ERROR] 상,하행 Station이 구간에 모두 포함된 경우 추가할 수 없습니다.");
        }
        if (!existUpStation && !existDownStation) {
            throw new IllegalArgumentException("[ERROR] 상,하행 Station 모두 구간에 존재하지 않는다면 추가할 수 없습니다.");
        }
    }

    private boolean hasSameUpByDown(Section section) {
        return sections.stream().anyMatch(it -> it.isSameUpByDown(section));
    }

    private boolean hasSameDownStation(Section section) {
        return sections.stream().anyMatch(it -> it.isSameDownStation(section));
    }

    private boolean hasSameDownByUp(Section section) {
        return sections.stream().anyMatch(it -> it.isSameDownByUp(section));
    }

    private boolean hasSameUpStation(Section section) {
        return sections.stream().anyMatch(it -> it.isSameUpStation(section));
    }

    private void addSplitByUpStation(Section section) {
        if (hasSameUpStation(section)) {
            Section findSection = sections.stream().filter(it -> it.isSameUpStation(section)).findAny().get();
            validateDistance(section, findSection);
            int distance = findSection.getDistance() - section.getDistance();
            sections.add(new Section(section.getDownStationId(), findSection.getDownStationId(), distance));
            sections.remove(findSection);
        }
    }

    private void addSplitByDownStation(Section section) {
        if (hasSameDownStation(section)) {
            Section findSection = sections.stream().filter(it -> it.isSameDownStation(section)).findAny().get();
            validateDistance(section, findSection);
            int distance = findSection.getDistance() - section.getDistance();
            sections.add(new Section(findSection.getUpStationId(), section.getUpStationId(), distance));
            sections.remove(findSection);
        }
    }

    private void validateDistance(Section section, Section findSection) {
        if (!findSection.isLongDistance(section)) {
            throw new IllegalArgumentException("[ERROR] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
        }
    }

    private void validateMinimumListSize() {
        if (sections.size() <= SECTIONS_MINIMUM_SIZE) {
            throw new IllegalArgumentException("[ERROR] 최소 하나 이상의 구간이 존재하여야합니다.");
        }
    }

    private void validateExistStationId(Optional<Section> findSectionByUpStationId, Optional<Section> findSectionByDownStationId) {
        if (findSectionByUpStationId.isEmpty() && findSectionByDownStationId.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 구간으로 등록되지 않은 지하철역 정보입니다.");
        }
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }

    public boolean hasSection(Section section) {
        return sections.stream()
            .anyMatch(it -> it.equals(section));
    }
}
