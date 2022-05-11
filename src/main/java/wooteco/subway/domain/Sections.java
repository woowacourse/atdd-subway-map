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

    public Section getSameUpStationSection(Section section) {
        return getSameUpStationSection(section.getUpStationId());
    }

    public Section getSameDownStationSection(Section section) {
        return getSameDownStationSection(section.getDownStationId());
    }

    public Section getSameUpStationSection(Long stationId) {
        return sections.stream()
            .filter(it -> it.isSameUpStation(stationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 없습니다."));
    }

    public Section getSameDownStationSection(Long stationId) {
        return sections.stream()
            .filter(it -> it.isSameDownStation(stationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 없습니다."));
    }

    public void validateDeletableSize() {
        if (sections.size() == 1) {
            throw new IllegalStateException("구간이 하나 남아서 삭제 할 수 없음");
        }
    }
}
