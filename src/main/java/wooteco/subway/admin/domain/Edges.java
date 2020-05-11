package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Edges implements Iterable<Edge> {
    private static final int FIRST = 0;
    private static final String VALIDATE_FIRST_EDGE_EXCEPTION_MESSAGE = "시작역부터 입력해주세요.";
    private static final String NO_SUCH_EDGE_EXCEPTION_MESSAGE = "존재하지 않는 역입니다.";
    private static final String EMPTY_EDGE_EXCEPTION_MESSAGE = "구간이 존재하지 않습니다.";
    private static final String REQUIRE_STATION_EXCEPTION_MESSAGE = "다음 역을 입력해주세요.";

    @MappedCollection(idColumn = "line_id", keyColumn = "line_key")
    private List<Edge> edges = new ArrayList<>();

    public Edges() {
    }

    public Edges(List<Edge> edges) {
        this.edges = edges;
    }

    public void addEdge(Edge edge) {
        if (edges.isEmpty()) {
            validateFirstEdge(edge);
        }
        validateEdge(edge);
        int insertionIndex = edges.size();

        if (findSamePreStationEdge(edge.getPreStationId()).isPresent()) {
            Edge nextEdge = findSamePreStationEdge(edge.getPreStationId()).get();
            insertionIndex = edges.indexOf(nextEdge);
            nextEdge.updatePreStationId(edge.getStationId());
        }
        edges.add(insertionIndex, edge);
    }

    public void removeEdge(Long id) {
        Edge currentEdge = findSameStationEdge(id);
        if (findSamePreStationEdge(id).isPresent()) {
            Edge nextEdge = findSamePreStationEdge(currentEdge.getPreStationId()).get();
            nextEdge.updatePreStationId(currentEdge.getPreStationId());
        }
        edges.remove(currentEdge);
    }

    private Optional<Edge> findSamePreStationEdge(Long id) {
        return edges.stream()
                .filter(value -> value.getPreStationId() == id)
                .findFirst();
    }

    private Edge findSameStationEdge(Long id) {
        return edges.stream()
                .filter(value -> id.equals(value.getStationId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_EDGE_EXCEPTION_MESSAGE));
    }

    public List<Long> findStationsId() {
        return edges.stream()
                .map(Edge::getStationId)
                .collect(Collectors.toList());
    }

    private void validateFirstEdge(Edge edge) {
        if (edge.getPreStationId() != null) {
            throw new IllegalArgumentException(VALIDATE_FIRST_EDGE_EXCEPTION_MESSAGE);
        }
    }

    private void validateEdge(Edge edge) {
        if (edge.getStationId() == null) {
            throw new IllegalArgumentException(REQUIRE_STATION_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public Iterator<Edge> iterator() {
        return edges.iterator();
    }
}
