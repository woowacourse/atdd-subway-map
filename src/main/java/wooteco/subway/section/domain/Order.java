package wooteco.subway.section.domain;

import java.util.List;

public class Order {

    private final List<Long> orderedIds;

    public Order(List<Long> orderedIds) {
        this.orderedIds = orderedIds;
    }

    public boolean isFirstSection(final Section section) {
        return isFirst(section.backStationId()) || isLast(section.frontStationId());
    }

    public boolean isFirst(final Long stationId){
        final Long firstStationId = orderedIds.get(0);
        return firstStationId.equals(stationId);
    }

    private boolean isLast(final Long stationId) {
        final Long lastStationId = orderedIds.get(orderedIds.size() - 1);
        return lastStationId.equals(stationId);
    }
}
