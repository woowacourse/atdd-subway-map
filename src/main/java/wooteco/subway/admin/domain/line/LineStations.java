package wooteco.subway.admin.domain.line;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LineStations {
    private final Set<LineStation> lineStations;

    public LineStations(Set<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void add(LineStation lineStation) {
        if (lineStations.isEmpty() && lineStation.isFirstNode()) {
            lineStations.add(lineStation);
            return;
        }
        if (lineStation.isFirstNode()) {
            lineStations.stream()
                .filter(LineStation::isFirstNode)
                .findFirst()
                .orElseThrow(AssertionError::new)
                .updatePreLineStation(lineStation.getStationId());
        } else {
            LineStation preNodeOfInput = lineStations.stream()
                .filter(station -> station.isPreNodeOf(lineStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "ID = " + lineStation.getPreStationId() + "인 역이 존재하지 않습니다.")
                );

            lineStations.stream()
                .filter(preNodeOfInput::isPreNodeOf)
                .findFirst()
                .ifPresent(station -> station.updatePreLineStation(lineStation.getStationId()));
        }

        lineStations.add(lineStation);
    }

    public void remove(Long stationId) {
        LineStation nodeToRemove = lineStations.stream()
            .filter(lineStation -> lineStation.isEqualStationId(stationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "ID = " + stationId + "인 역이 존재하지 않습니다.")
            );

        lineStations.stream()
            .filter(nodeToRemove::isPreNodeOf)
            .findFirst()
            .ifPresent(lineStation -> lineStation.updatePreLineStation(nodeToRemove.getPreStationId()));

        lineStations.remove(nodeToRemove);
    }

    public List<Long> getSortedStationsId() {
        List<Long> sortedStationsId = new ArrayList<>();
        if (lineStations.isEmpty()) {
            return sortedStationsId;
        }

        LineStation preNode = lineStations.stream()
            .filter(LineStation::isFirstNode)
            .findFirst()
            .orElseThrow(AssertionError::new);
        sortedStationsId.add(preNode.getStationId());

        while (sortedStationsId.size() < lineStations.size()) {
            LineStation currentNode = lineStations.stream()
                .filter(preNode::isPreNodeOf)
                .findFirst()
                .orElseThrow(AssertionError::new);
            sortedStationsId.add(currentNode.getStationId());
            preNode = currentNode;
        }
        return sortedStationsId;
    }

    public Set<LineStation> getLineStations() {
        return lineStations;
    }
}
