package wooteco.subway.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(Section section) {
        this.sections = new ArrayList<>(List.of(section));
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        validateDuplicate(section.getUpStationId(), section.getDownStationId());
        validateNoExist(section.getUpStationId(), section.getDownStationId());
        if (isDownSection(section) && isAnotherSection(section)) {
            Section anotherSection = getAnotherSection(section);
            if (section.getDistance() >= anotherSection.getDistance()) {
                throw new IllegalStateException("기존 구간보다 거리가 긴 구간을 입력할 수 없습니다.");
            }
            addMiddleSection(section, anotherSection);
        }
        this.sections.add(section);
    }

    private void addMiddleSection(Section section, Section anotherSection) {
        this.sections.add(new Section(
                anotherSection.getLineId(),
                anotherSection.getUpStationId(),
                section.getDownStationId(),
                section.getDistance()
        ));
        this.sections.add(new Section(
                anotherSection.getLineId(),
                section.getDownStationId(),
                anotherSection.getDownStationId(),
                anotherSection.getDistance() - section.getDistance()
        ));
        this.sections.remove(anotherSection);
    }

    private boolean isAnotherSection(Section section) {
        return sections.stream()
                .filter(another -> section.getUpStationId().equals(another.getUpStationId()))
                .anyMatch(another -> !section.getDownStationId().equals(another.getDownStationId()));
    }

    private Section getAnotherSection(Section section) {
        return sections.stream()
                .filter(another -> section.getUpStationId().equals(another.getUpStationId()))
                .filter(another -> !section.getDownStationId().equals(another.getDownStationId()))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("다른 구간을 찾을 수 없습니다."));
    }

    public Set<Long> getStations() {
        Set<Long> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStationId());
            stations.add(section.getDownStationId());
        }
        return stations;
    }

    private void validateDuplicate(Long upStationId, Long downStationId) {
        Set<Long> stationIds = getStations();
        if (stationIds.contains(upStationId) && stationIds.contains(downStationId)) {
            throw new IllegalStateException("상행과 하행 모두 이미 저장된 지하철역인 경우는 저장할 수 없습니다.");
        }
    }

    private void validateNoExist(Long upStationId, Long downStationId) {
        Set<Long> stationIds = getStations();
        if (!stationIds.contains(upStationId) && !stationIds.contains(downStationId)) {
            throw new IllegalStateException("상행과 하행 모두 저장되지 않은 지하철역인 경우 저장할 수 없습니다.");
        }
    }

    private boolean isDownSection(Section section) {
        return sections.stream()
                .noneMatch(s -> s.isContainStationId(section.getDownStationId()));
    }

    public void remove(Long stationId) {
        final List<Section> removeCandidates = getSectionContainsStation(stationId);
        if (removeCandidates.size() == 1) {
            sections.remove(removeCandidates.get(0));
            return;
        }

        final Section upSection = getUpSection(stationId, removeCandidates);
        final Section downSection = getDownSection(stationId, removeCandidates);
        final Long lineId = upSection.getLineId();

        sections.add(new Section(
                lineId,
                downSection.getUpStationId(),
                upSection.getDownStationId(),
                downSection.getDistance() + upSection.getDistance()
        ));
        sections.remove(upSection);
        sections.remove(downSection);
    }

    public Section getUpSection(Long stationId, List<Section> candidates) {
        return candidates.stream()
                .filter(candidate -> candidate.getUpStationId().equals(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("상행 구간을 찾을 수 없습니다."));
    }

    public Section getDownSection(Long stationId, List<Section> candidates) {
        return candidates.stream()
                .filter(candidate -> candidate.getDownStationId().equals(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("하행 구간을 찾을 수 없습니다."));
    }

    public List<Section> getSectionContainsStation(Long stationId) {
        return sections.stream()
                .filter(section -> section.isContainStationId(stationId))
                .collect(Collectors.toList());
    }
}
