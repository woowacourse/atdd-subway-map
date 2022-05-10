package wooteco.subway.domain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    private final List<Section> value;

    public Sections(List<Section> value) {
        this.value = new ArrayList<>(value);
    }

    public void append(Section section) {
        validateSameStation(section);

        if ((isUpTerminus(section) || isDownTerminus(section))
                || (section.isSameUpStation(findByUpStationId(getUpStationId())) && !isFrontForkedLoad(section))
                || (section.isSameDownStation(findByDownStationId(getDownStationId())) && !isBackForkedLoad(section))) {
            value.add(section);
            return;
        }

        throw new IllegalArgumentException("구간 추가가 불가능 합니다.");
    }

    private void validateSameStation(Section section) {
        if (value.stream().anyMatch(section::isSameUpAndDownStation)) {
            throw new IllegalArgumentException("구간 추가가 불가능 합니다.");
        }
    }

    private boolean isFrontForkedLoad(Section section) {
        Section frontSection = findByUpStationId(section.getUpStationId());
        if (frontSection.getDistance() > section.getDistance()) {
            frontSection.changeUpStation(section);
            return false;
        }

        return true;
    }

    private boolean isBackForkedLoad(Section section) {
        Section backSection = findByDownStationId(section.getDownStationId());
        if (backSection.getDistance() > section.getDistance()) {
            backSection.changeDownStation(section);
            return false;
        }

        return true;
    }

    private boolean isUpTerminus(Section section) {
        return section.getDownStationId().equals(getUpStationId())
                && !existsStationId(section.getUpStationId());
    }

    private boolean isDownTerminus(Section section) {
        return section.getUpStationId().equals(getDownStationId())
                && !existsStationId(section.getDownStationId());
    }

    private boolean existsStationId(Long stationId) {
        return value.stream()
                .anyMatch(section -> section.existsStation(stationId));
    }

    private Long getUpStationId() {
        List<Long> upStationIds = getUpStationIds();
        List<Long> downStationIds = getDownStationIds();

        upStationIds.removeAll(downStationIds);
        return upStationIds.get(0);
    }

    private Long getDownStationId() {
        List<Long> upStationIds = getUpStationIds();
        List<Long> downStationIds = getDownStationIds();

        downStationIds.removeAll(upStationIds);
        return downStationIds.get(0);
    }

    private List<Long> getUpStationIds() {
        return value.stream()
                .map(Section::getUpStationId)
                .collect(toList());
    }

    private List<Long> getDownStationIds() {
        return value.stream()
                .map(Section::getDownStationId)
                .collect(toList());
    }

    private Section findByUpStationId(Long upStationId) {
        return value.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("일치하는 구간이 존재하지 않습니다."));
    }

    private Section findByDownStationId(Long downStationId) {
        return value.stream()
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("일치하는 구간이 존재하지 않습니다."));
    }

    public List<Section> getValue() {
        List<Section> sections = new ArrayList<>();
        Section firstSection = findByUpStationId(getUpStationId());

        addSection(sections, firstSection);

        return sections;
    }

    private void addSection(List<Section> sections, Section prevSection) {
        sections.add(prevSection);

        for (Section currentSection : value) {
            checkConnect(sections, prevSection, currentSection);
        }
    }

    private void checkConnect(List<Section> sections, Section prevSection, Section currentSection) {
        if (prevSection.isConnect(currentSection)) {
            addSection(sections, currentSection);
        }
    }
}
