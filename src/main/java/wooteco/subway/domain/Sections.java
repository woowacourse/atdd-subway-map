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
        validateSameSection(section);
        validateContainsStation(section);
        validateNotContainsStation(section);

        if (isUpTerminus(section) || isDownTerminus(section)) {
            value.add(section);
            return;
        }

        decideForkedLoad(section);
    }

    private void validateSameSection(Section section) {
        if (value.stream().anyMatch(section::isSameUpAndDownStation)) {
            throw new IllegalArgumentException("구간이 노선에 이미 등록되어 추가할 수 없습니다.");
        }
    }

    private void validateContainsStation(Section section) {
        if (existsStationId(section.getUpStationId()) && existsStationId(section.getDownStationId())) {
            throw new IllegalArgumentException("상행역과 하행역 모두 노선에 포함되어 있으므로 추가할 수 없습니다.");
        }
    }

    private void validateNotContainsStation(Section section) {
        if (!existsStationId(section.getUpStationId()) && !existsStationId(section.getDownStationId())) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 노선에 포함되지 않으므로 추가할 수 없습니다.");
        }
    }

    private boolean isUpTerminus(Section section) {
        return section.getDownStationId().equals(getUpStationId())
                && !existsStationId(section.getUpStationId());
    }

    private boolean isDownTerminus(Section section) {
        return section.getUpStationId().equals(getDownStationId())
                && !existsStationId(section.getDownStationId());
    }

    private void decideForkedLoad(Section section) {
        if (isFrontForkedLoad(section)) {
            changeFrontSection(section);
            return;
        }

        if (isBackForkedLoad(section)) {
            changeBackSection(section);
            return;
        }

        throw new IllegalArgumentException("새로운 구간의 길이가 기존 구간의 길이 보다 크거나 같으므로 추가할 수 없습니다.");
    }

    private boolean isFrontForkedLoad(Section section) {
        if (existsStationId(section.getUpStationId())) {
            Section frontSection = findByUpStationId(section.getUpStationId());
            return frontSection.getDistance() > section.getDistance();
        }

        return false;
    }

    private void changeFrontSection(Section section) {
        Section frontSection = findByUpStationId(section.getUpStationId());
        frontSection.changeUpStation(section);
        value.add(section);
    }

    private boolean isBackForkedLoad(Section section) {
        if (existsStationId(section.getDownStationId())) {
            Section backSection = findByDownStationId(section.getDownStationId());
            return backSection.getDistance() > section.getDistance();
        }

        return false;
    }

    private void changeBackSection(Section section) {
        Section backSection = findByDownStationId(section.getDownStationId());
        backSection.changeDownStation(section);
        value.add(section);
    }

    private boolean existsStationId(Long stationId) {
        return value.stream()
                .anyMatch(section -> section.existsStation(stationId));
    }

    public void remove(Long stationId) {
        if (value.size() == 1) {
            throw new IllegalArgumentException("구간이 1개만 존재하므로 삭제가 불가능 합니다.");
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

    private void removeMiddleStation(Long stationId) {
        Section frontSection = findByDownStationId(stationId);
        Section backSection = findByUpStationId(stationId);

        Long lineId = frontSection.getLineId();
        Long upStationId = frontSection.getUpStationId();
        Long downStationId = backSection.getDownStationId();
        int distance = frontSection.getDistance() + backSection.getDistance();

        value.remove(frontSection);
        value.remove(backSection);

        value.add(new Section(lineId, upStationId, downStationId, distance));
    }

    public List<Long> getSortedStationIds() {
        List<Section> value = getSortedValue();
        List<Long> stationIds = value.stream()
                .map(Section::getUpStationId)
                .collect(toList());

        Section lastSection = value.get(value.size() - 1);
        stationIds.add(lastSection.getDownStationId());

        return stationIds;
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

    public List<Section> getSortedValue() {
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
