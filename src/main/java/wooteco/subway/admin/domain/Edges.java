package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.MappedCollection;
import wooteco.subway.admin.domain.exception.NoSuchStationException;
import wooteco.subway.admin.domain.exception.RequireFirstStationException;
import wooteco.subway.admin.domain.exception.RequireStationNameException;

import java.util.*;
import java.util.stream.Collectors;

public class Edges implements Iterable<Edge> {
    @MappedCollection(idColumn = "line_id", keyColumn = "line_key")
    private List<Edge> edges = new ArrayList<>();

    public Edges() {
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
                .filter(value -> Objects.equals(id, value.getPreStationId()))
                .findFirst();
    }

    private Edge findSameStationEdge(Long id) {
        return edges.stream()
                .filter(value -> id.equals(value.getStationId()))
                .findFirst()
                .orElseThrow(NoSuchStationException::new);
    }

    public List<Long> findStationsId() {
        return edges.stream()
                .map(Edge::getStationId)
                .collect(Collectors.toList());
    }

    private void validateFirstEdge(Edge edge) {
        if (edge.getPreStationId() != null) {
            throw new RequireFirstStationException();
        }
    }

    private void validateEdge(Edge edge) {
        if (Objects.isNull(edge.getStationId())) {
            throw new RequireStationNameException();
        }
    }

    @Override
    public Iterator<Edge> iterator() {
        return edges.iterator();
    }
}
