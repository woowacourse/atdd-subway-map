package wooteco.subway.line.domain;


import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotFoundException;

import java.util.*;

public class Sections {
    private static final int LINE_MIN_SIZE = 1;

    private final List<Section> sections;
    private final Map<Long, Long> connect = new HashMap<>();

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
        sections.forEach(section -> connect.put(section.getUpStationId(), section.getDownStationId()));
    }

    public List<Long> getStationIds() {
        Set<Long> stationIds = new HashSet<>();
        connect.forEach((upStationId, downStationId) -> {
            stationIds.add(upStationId);
            stationIds.add(downStationId);
        });
        return new ArrayList<>(stationIds);
    }

    public boolean containUpStationId(final Long upStationId) {
        return connect.containsKey(upStationId);
    }

    public boolean containDownStationId(final Long downStationId) {
        return connect.containsValue(downStationId);
    }

    public void isValidateSection(final Long upStationId, final Long downStationId) {
        List<Long> stationIds = getStationIds();
        if (containUpStationId(upStationId) && containDownStationId(downStationId)) {
            throw new DuplicateException("이미 노선에 등록되어있는 구간입니다.");
        }
        if (!(stationIds.contains(upStationId) || stationIds.contains(downStationId))) {
            throw new NotFoundException("상행선, 하행선 둘다 현재 노선에 존재하지 않습니다.");
        }
    }

    public void validateDeleteStation(final Long stationId) {
        List<Long> stationIds = getStationIds();
        if (!stationIds.contains(stationId)) {
            throw new NotFoundException("해당 station id는 입력받은 Line id에 속해있지 않습니다.");
        }
        if (sections.size() <= LINE_MIN_SIZE) {
            throw new IllegalArgumentException("해당 라인이 포함하고 있는 구간이 1개 이하입니다");
        }
    }

    public Long getDownStationId(final Long upStationId) {
        return connect.get(upStationId);
    }
    public Long getUpStationId(final Long downStationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findFirst().get().getUpStationId();
    }

    public int getDistance(final Long upStationId, final Long beforeConnectedStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .filter(section -> section.getDownStationId().equals(beforeConnectedStationId))
                .findFirst().get().getDistance();
    }
}
