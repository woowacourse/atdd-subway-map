package wooteco.subway.admin.domain.line.relation;

import static java.util.stream.Collectors.*;
import static wooteco.subway.admin.domain.line.relation.InvalidLineStationException.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LineStations {
    private final Set<LineStation> lineStations;

    public LineStations(Set<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void add(LineStation newStation) {
        if (!lineStations.isEmpty() && newStation.isFirstNode()) {
            LineStation firstNode = getFirstNode();
            firstNode.updatePreStationIdTo(newStation.getStationId());
        }
        if (newStation.isNotFirstNode()) {
            LineStation preNodeOfNewStation = getPreNodeOf(newStation);
            Optional<LineStation> nextNodeOfNewStation = getNextNodeOf(preNodeOfNewStation);
            nextNodeOfNewStation
                .ifPresent(station -> station.updatePreStationIdTo(newStation.getStationId()));
        }
        lineStations.add(newStation);
    }

    private LineStation getFirstNode() {
        return lineStations.stream()
            .filter(LineStation::isFirstNode)
            .findFirst()
            .orElseThrow(AssertionError::new);
    }

    private LineStation getPreNodeOf(LineStation newLineStation) {
        return lineStations.stream()
            .filter(station -> station.isPreNodeOf(newLineStation))
            .findFirst()
            .orElseThrow(() ->
                new InvalidLineStationException(NOT_EXIST_ID, newLineStation.getPreStationId())
            );
    }

    private Optional<LineStation> getNextNodeOf(LineStation station) {
        return lineStations.stream()
            .filter(station::isPreNodeOf)
            .findFirst();
    }

    public void remove(Long stationId) {
        LineStation nodeToRemove = getNodeToRemove(stationId);
        Optional<LineStation> nextOfRemoveNode = getNextNodeOf(nodeToRemove);

        nextOfRemoveNode.ifPresent(nextOfRemove ->
            nextOfRemove.updatePreStationIdTo(nodeToRemove.getPreStationId())
        );

        lineStations.remove(nodeToRemove);
    }

    private LineStation getNodeToRemove(Long stationId) {
        return lineStations.stream()
            .filter(lineStation -> lineStation.isEqualStationId(stationId))
            .findFirst()
            .orElseThrow(() -> new InvalidLineStationException(NOT_EXIST_ID, stationId));
    }

    public List<Long> getSortedStationsId() {
        List<Long> sortedStationsId = new ArrayList<>();
        if (lineStations.isEmpty()) {
            return sortedStationsId;
        }

        LineStation preNode = getFirstNode();
        sortedStationsId.add(preNode.getStationId());

        while (getNextNodeOf(preNode).isPresent()) {
            LineStation currentNode = getNextNodeOf(preNode).get();
            sortedStationsId.add(currentNode.getStationId());
            preNode = currentNode;
        }

        return sortedStationsId;
    }

    public Set<LineStation> getLineStations() {
        return lineStations;
    }

    public List<Long> getIds() {
        return lineStations.stream()
            .map(LineStation::getStationId)
            .collect(toList());
    }
}
