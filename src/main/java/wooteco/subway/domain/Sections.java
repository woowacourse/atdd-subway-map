package wooteco.subway.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Sections {

    private final List<Section> value;

    public Sections(List<Section> value) {
        this.value = value;
    }

    public List<Long> getSortedStationIds() {
        if (value == null || value.size() == 0) {
            return new LinkedList<>();
        }

        List<Long> ids = new LinkedList<>();

        Map<Long, Long> sectionMap = initByUpStationKey();
        Long nowId = value.get(0).getDownStationId();
        while (nowId != null) {
            ids.add(nowId);
            nowId = sectionMap.get(nowId);
        }

        sectionMap = initByDownStationKey();
        nowId = value.get(0).getUpStationId();
        while (nowId != null) {
            ids.add(0, nowId);
            nowId = sectionMap.get(nowId);
        }

        return ids;
    }

    private Map<Long, Long> initByUpStationKey() {
        Map<Long, Long> map = new HashMap<>();
        for (Section section : value) {
            map.put(section.getUpStationId(), section.getDownStationId());
        }
        return map;
    }

    private Map<Long, Long> initByDownStationKey() {
        Map<Long, Long> map = new HashMap<>();
        for (Section section : value) {
            map.put(section.getDownStationId(), section.getUpStationId());
        }
        return map;
    }

    /**
     * 새로운 구간을 등록하는 메서드
     *
     * @param newSection 추가하고자 하는 구간
     * @return 데이터가 변경된 Section
     */
    public Optional<Section> add(Section newSection) {
        validNewSection(newSection);

        for (Section section : value) {
            if (newSection.isSameDownStationId(section)) {
                throw new IllegalArgumentException("갈림길을 생성할 수 없습니다.");
            }

            if (newSection.isSameUpStationId(section)) {
                section.updateUpStationId(newSection.getDownStationId());
                section.reduceDistance(newSection);
                return Optional.of(section);
            }
        }
        return Optional.empty();
    }

    private void validNewSection(Section section) {
        Set<Long> ids = findStationIds();
        if (ids.contains(section.getDownStationId()) && ids.contains(section.getUpStationId())) {
            throw new IllegalArgumentException("해당 구간은 이미 등록되어 있습니다.");
        }
    }

    private Set<Long> findStationIds() {
        Set<Long> ids = new HashSet<>();
        for (Section section : value) {
            ids.add(section.getUpStationId());
            ids.add(section.getDownStationId());
        }
        return ids;
    }

    /**
     * 기존 구간을 삭제하는 메서드
     *
     * @param stationId 삭제하고자 하는 역
     * @return 삭제로 인해 변경 사항이 있는 Section
     */
    public Optional<Section> findUpdateWhenRemove(Long stationId) {
        validSize();
        Section removedSection = findByDownStationId(stationId);

        for (Section section : value) {
            if (section.isSameUpStationId(stationId)) {
                section.updateUpStationId(removedSection.getUpStationId());
                section.addDistance(removedSection);
                return Optional.of(section);
            }
        }
        return Optional.empty();
    }

    public Section findByDownStationId(Long stationId) {
        return value.stream()
                .filter(section -> section.isSameDownStationId(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 stationId를 하행역으로 둔 구간은 존재하지 않습니다."));
    }

    private void validSize() {
        if (value.size() == 0) {
            throw new IllegalArgumentException("section 목록이 존재하지 않습니다.");
        }
    }

    public List<Section> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Sections{" +
                "value=" + value +
                '}';
    }
}
