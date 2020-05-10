package wooteco.subway.admin.line.domain;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Lines implements Iterable<Line> {
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

    @Override
    public Iterator<Line> iterator() {
        return lines.iterator();
    }

    @Override
    public void forEach(final Consumer<? super Line> action) {
        lines.forEach(action);
    }

    @Override
    public Spliterator<Line> spliterator() {
        return lines.spliterator();
    }
}
