package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    private final List<Section> value;

    public Sections(List<Section> value) {
        this.value = arrangeFromUpToDown(value);
    }

    private List<Section> arrangeFromUpToDown(List<Section> value) {
        List<Section> sections = new ArrayList<>();
        Section section = findTopSection(value);
        sections.add(section);

        while (sections.size() < value.size()) {
            section = findNext(section, value);
            sections.add(section);
        }
        return sections;
    }

    private Section findTopSection(List<Section> value) {
        return value.stream()
                .filter(it -> isTopSection(it, value))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("올바르지 않은 구간들 입니다."));
    }

    private boolean isTopSection(Section section, List<Section> value) {
        return value.stream()
                .allMatch(it -> !it.matchDownStationWithUpStationOf(section));
    }

    private Section findNext(Section section, List<Section> value) {
        return value.stream()
                .filter(it -> it.matchUpStationWithDownStationOf(section))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당역의 하행역이 존재하지 않습니다."));
    }

    public void add(Section newSection) {
        checkStationExist(newSection);
        checkDuplicateSection(newSection);

        if (isContainingUpStationOf(newSection)) {
            addWhenContainingUpStationOf(newSection);
            return;
        }
        if (isContainingDownStationOf(newSection)) {
            addWhenContainingDownStationOf(newSection);
            return;
        }
    }

    private void addWhenContainingUpStationOf(Section newSection) {
        if (existMatchUpStationWithUpStationOf(newSection)) {
            addBetweenWithUpStationOf(newSection);
            return;
        }
        value.add(newSection);
    }

    private void addBetweenWithUpStationOf(Section newSection) {
        Section target = value.stream()
                .filter(it -> it.matchUpStationWithUpStationOf(newSection))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException());
        int targetIndex = value.indexOf(target);
        validateDistance(target, newSection);
        target.updateUpStationId(newSection);
        target.updateDistance(newSection);
        value.add(targetIndex, newSection);
    }

    private void addWhenContainingDownStationOf(Section newSection) {
        if (existMatchDownStationWithDownStationOf(newSection)) {
            addBetweenWithDownStationOf(newSection);
            return;
        }
        value.add(0, newSection);
    }

    private void addBetweenWithDownStationOf(Section newSection) {
        Section target = value.stream()
                .filter(it -> it.matchDownStationWithDownStationOf(newSection))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException());
        int targetIndex = value.indexOf(target);
        validateDistance(target, newSection);
        target.updateDownStationId(newSection);
        target.updateDistance(newSection);
        value.add(targetIndex + 1, newSection);
    }

    private void validateDistance(Section target, Section newSection) {
        if (target.isLessThan(newSection)) {
            throw new IllegalArgumentException("추가하고자 하는 구간의 길이는 기존의 구간보다 짧아야 합니다.");
        }
    }

    private void checkStationExist(Section newSection) {
        if (!isContainingUpStationOf(newSection) && !isContainingDownStationOf(newSection)) {
            throw new IllegalArgumentException("구간 등록은 노선에 존재하는 상행역과 하행역 중 하나를 포함하고 있어야 합니다.");
        }
    }

    private void checkDuplicateSection(Section newSection) {
        if (isContainingUpStationOf(newSection) && isContainingDownStationOf(newSection)) {
            throw new IllegalArgumentException("해당 경로가 이미 존재합니다.");
        }
    }

    private boolean isContainingUpStationOf(Section newSection) {
        return existMatchUpStationWithUpStationOf(newSection) || existMatchDownStationWithUpStationOf(newSection);
    }

    private boolean isContainingDownStationOf(Section newSection) {
        return existMatchUpStationWithDownStationOf(newSection) || existMatchDownStationWithDownStationOf(newSection);
    }

    private boolean existMatchUpStationWithUpStationOf(Section newSection) {
        return value.stream()
                .anyMatch(section -> section.matchUpStationWithUpStationOf(newSection));
    }

    private boolean existMatchUpStationWithDownStationOf(Section newSection) {
        return value.stream()
                .anyMatch(section -> section.matchUpStationWithDownStationOf(newSection));
    }

    private boolean existMatchDownStationWithUpStationOf(Section newSection) {
        return value.stream()
                .anyMatch(section -> section.matchDownStationWithUpStationOf(newSection));
    }

    private boolean existMatchDownStationWithDownStationOf(Section newSection) {
        return value.stream()
                .anyMatch(section -> section.matchDownStationWithDownStationOf(newSection));
    }

    public List<Section> getValue() {
        return List.copyOf(value);
    }
}
