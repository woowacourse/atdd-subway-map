package wooteco.subway.admin.line.domain;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lines {
    private final List<Line> lines;

    public Lines(final List<Line> lines) {
        this.lines = lines;
    }

    public List<Long> getAllEdgeStationId() {
        return lines.stream()
                .map(Line::getEdgesStationIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public Stream<Line> stream() {
        return lines.stream();
    }

}
