package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {

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

    private void validateAddSectionCondition(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new IllegalArgumentException("[ERROR] 상,하행 Station이 구간에 모두 포함된 경우 추가할 수 없습니다.");
        }
        if (!existUpStation && !existDownStation) {
            throw new IllegalArgumentException("[ERROR] 상,하행 Station 모두 구간에 존재하지 않는다면 추가할 수 없습니다.");
        }
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

    public List<Section> getSections() {
        return List.copyOf(sections);
    }

    public boolean hasSection(Section section) {
        return sections.stream()
            .anyMatch(it -> it.equals(section));
    }
}
