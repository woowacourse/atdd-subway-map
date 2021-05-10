package wooteco.subway.domain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class OrderedStationIds {
    private final Deque<Long> orderedStationIds;

    public OrderedStationIds(final Deque<Long> orderedStationIds) {
        this.orderedStationIds = orderedStationIds;
    }

    public static OrderedStationIds of(final Line line, final List<Section> sections) {
        Long stationId = line.getTopStationId();

        Deque<Long> orderedStationIds = new ArrayDeque<>();
        orderedStationIds.add(stationId);

        while (!isEnd(line.getBottomStationId(), stationId)) {
            for (Section section : sections) {
                if (section.getUpStationId().equals(stationId)) {
                    stationId = section.getDownStationId();
                    break;
                }
            }

            orderedStationIds.add(stationId);
        }
        return new OrderedStationIds(orderedStationIds);
    }

    private static boolean isEnd(final Long bottomStationId, final Long postStationId) {
        return bottomStationId.equals(postStationId);
    }

    public Deque<Long> getOrderedStationIds() {
        return orderedStationIds;
    }
}
