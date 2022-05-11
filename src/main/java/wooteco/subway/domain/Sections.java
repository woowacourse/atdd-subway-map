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
        validateStation(section);

        if ((isUpTerminus(section)
                || isDownTerminus(section))
                || existsStationId(section.getUpStationId()) && !isFrontForkedLoad(section)
                || existsStationId(section.getDownStationId()) && !isBackForkedLoad(section)) {
            value.add(section);
            return;
        }

        throw new IllegalArgumentException("구간 추가가 불가능 합니다.");
    }

    private void validateStation(Section section) {
        if (value.stream().anyMatch(section::isSameUpAndDownStation)
                || existsStationId(section.getUpStationId()) && existsStationId(section.getDownStationId())) {
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

    public void remove(Long stationId) {
        if (value.size() == 1) {
            throw new IllegalArgumentException("역 삭제가 불가능 합니다.");
        }

        if (getUpStationId().equals(stationId)) {
            value.remove(findByUpStationId(getUpStationId()));
            return;
        }

        if (getDownStationId().equals(stationId)) {
            value.remove(findByDownStationId(getDownStationId()));
            return;
        }

        removeMiddleStation(stationId);
    }

    private void removeMiddleStation(Long stationId) {
        Section frontSection = findByDownStationId(stationId);
        Section backSection = findByUpStationId(stationId);

        Long lineId = frontSection.getLineId();
        Long upStationId = frontSection.getUpStationId();
        Long downStationId = backSection.getDownStationId();
        Integer distance = frontSection.getDistance() + backSection.getDistance();

        value.remove(frontSection);
        value.remove(backSection);

        value.add(new Section(lineId, upStationId, downStationId, distance));
    }

    public List<Long> getStationIds() {
        List<Section> value = getValue();
        List<Long> stationIds = value.stream()
                .map(Section::getUpStationId)
                .collect(toList());

        Section lastSection = value.get(value.size() - 1);
        stationIds.add(lastSection.getDownStationId());

        return stationIds;
    }

    public List<Section> getValue() {
        List<Section> sections = new ArrayList<>();
        Section firstSection = findByUpStationId(getUpStationId());

        addSection(sections, firstSection);

        return sections;
    }

    private Section findByUpStationId(Long upStationId) {
        return value.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 존재하지 않습니다."));
    }

    private Section findByDownStationId(Long downStationId) {
        return value.stream()
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 존재하지 않습니다."));
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
