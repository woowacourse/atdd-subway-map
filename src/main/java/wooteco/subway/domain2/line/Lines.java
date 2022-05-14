package wooteco.subway.domain2.line;

import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.entity.RegisteredStationEntity;

public class Lines {

    private final Map<Long, Line> value;

    private Lines(Map<Long, Line> value) {
        this.value = value;
    }

    public static Lines of(List<RegisteredStationEntity> entities) {
        Map<Long, Line> value = new HashMap<>();
        Map<Long, List<RegisteredStationEntity>> lines = groupStationsByLineId(entities);
        for (Long lineId : lines.keySet()) {
            value.put(lineId, Line.of(lines.get(lineId)));
        }
        return new Lines(value);
    }

    private static Map<Long, List<RegisteredStationEntity>> groupStationsByLineId(List<RegisteredStationEntity> entities) {
        return entities.stream()
                .collect(groupingBy(RegisteredStationEntity::getLineId));
    }

    public List<Line> toLine() {
        return new ArrayList<>(value.values());
    }

    @Override
    public String toString() {
        return "Lines{" + "value=" + value + '}';
    }
}
