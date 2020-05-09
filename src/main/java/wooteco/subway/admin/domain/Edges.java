package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Edges {
    @MappedCollection(idColumn = "line_id", keyColumn = "line_key")
    private List<Edge> edges;

    public Edges(final List<Edge> edges) {
        this.edges = edges;
    }

    public static Edges empty() {
        return new Edges(new ArrayList<>());
    }

    public List<Long> getStationsId() {
        return edges.stream()
                .map(Edge::getStationId)
                .collect(Collectors.toList());
    }

    public void add(final Edge edge) {
        if (edge.hasStartStation()) {
            List<Edge> updatedEdges = new ArrayList<>();
            updatedEdges.add(edge);
            updatedEdges.addAll(this.edges);
            this.edges = updatedEdges;
            return;
        }
        List<Edge> updatedEdges = new ArrayList<>();
        for (Edge aEdge : this.edges) {
            updatedEdges.add(aEdge);
            if (aEdge.isPreStationOf(edge)) {
                updatedEdges.add(edge);
            }
        }
        this.edges = updatedEdges;
    }

    public void removeByStationId(final Long stationId) {
        this.edges.stream()
                .filter(edge -> edge.getStationId().equals(stationId))
                .findFirst()
                .ifPresent(this.edges::remove);
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }
}
