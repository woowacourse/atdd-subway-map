package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    static final String DUPLICATE_STATION_ERROR_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.";
    static final String NONE_DUPLICATE_STATION_ERROR_MESSAGE = "상행역과 하행역 둘 중 하나는 포함되어 있어야 합니다.";
    static final String IMPOSSIBLE_DELETE_EXCEPTION_MESSAGE = "삭제할 구간이 존재하지 않습니다.";

    private final List<Section> value;

    public Sections(List<Section> value) {
        this.value = value;
    }

    public Sections add(Section section) {
        validateExistStations(section);
        List<Section> sections = new ArrayList<>(value);
        for (Section originSection : sections) {
            // 상행종점, 하행종점
            if (originSection.isUpTerminal(section) && !getDownStationIds().contains(section.getDownStationId())
                    || originSection.isDownTerminal(section) && !getUpStationIds().contains(section.getUpStationId())) {
                sections.add(section);
                return new Sections(sections);
            }
            //상행 추가
            if (originSection.isSameUpStation(section) && originSection.isLessThanDistance(section)) {
                sections.add(section);
                Long newUpStationId = section.getDownStationId();
                Long newDownStationId = originSection.getDownStationId();
                int newDistance = originSection.getDistance() - section.getDistance();
                sections.remove(originSection);
                Section newSection = new Section(section.getLineId(), newUpStationId, newDownStationId, newDistance);
                sections.add(newSection);
                return new Sections(sections);
            }
            //하행 추가
            if (originSection.isSameDownStation(section) && originSection.isLessThanDistance(section)) {
                sections.add(section);
                Long newDownStationId = section.getUpStationId();
                Long newUpStationId = originSection.getUpStationId();
                int newDistance = originSection.getDistance() - section.getDistance();
                sections.remove(originSection);
                Section newSection = new Section(section.getLineId(), newUpStationId, newDownStationId, newDistance);
                sections.add(newSection);
                return new Sections(sections);
            }
        }
        throw new IllegalArgumentException("에러");
    }

    private void validateExistStations(Section section) {
        Set<Long> stationIds = getStationIds();
        if (stationIds.contains(section.getUpStationId()) && stationIds.contains(section.getDownStationId())) {
            throw new IllegalArgumentException(DUPLICATE_STATION_ERROR_MESSAGE);
        }
        if (!(stationIds.contains(section.getUpStationId()) || stationIds.contains(section.getDownStationId()))) {
            throw new IllegalArgumentException(NONE_DUPLICATE_STATION_ERROR_MESSAGE);
        }
    }

    public Sections delete(Long lineId, Long stationId) {
        validatePossibleToDelete(stationId);
        long size = getStationIdsToList().stream()
                .filter(it -> it == stationId)
                .count();
        if (size == 1) {
            value.removeIf(section -> section.hasStation(stationId));
            return new Sections(value);
        }
        if (size == 2) {
            Section newSection = makeNewSection(lineId, stationId);
            value.removeIf(section -> section.hasStation(stationId));
            value.add(newSection);
            return new Sections(value);
        }
        throw new IllegalStateException("에러");
    }

    private Section makeNewSection(Long lineId, Long stationId) {
        Long newDownStationId = 0L;
        Long newUpStationId = 0L;
        Integer newDistance = 0;
        for (Section section : value) {
            if (section.hasUpStation(stationId)) {
                newDownStationId = section.getDownStationId();
                newDistance += section.getDistance();
            }
            if (section.hasDownStation(stationId)) {
                newUpStationId = section.getUpStationId();
                newDistance += section.getDistance();
            }
        }
        return new Section(lineId, newUpStationId, newDownStationId, newDistance);
    }

    private void validatePossibleToDelete(Long stationId) {
        Set<Long> stationIds = getStationIds();
        if (!stationIds.contains(stationId) || stationIds.size() == 2) {
            throw new IllegalArgumentException(IMPOSSIBLE_DELETE_EXCEPTION_MESSAGE);
        }
    }

    private Set<Long> getUpStationIds() {
        return value.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<Long> getDownStationIds() {
        return value.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Long> getStationIds() {
        return value.stream()
                .map(Section::getStationId)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    public List<Long> getStationIdsToList() {
        return value.stream()
                .map(Section::getStationId)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Section> getValue() {
        return Collections.unmodifiableList(value);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "value=" + value +
                '}';
    }
}
