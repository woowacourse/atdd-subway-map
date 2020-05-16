package wooteco.subway.admin.station.domain;

import wooteco.subway.admin.common.exception.SubwayException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Stations {
    private Set<Station> stations;

    public Stations(final Set<Station> stations) {
        this.stations = stations;
    }

    public Set<Station> getStations() {
        return stations;
    }

    public String findNameById(Long stationId) {
        return findById(stationId)
                .getName();
    }

    public Station findById(final Long stationId) {
        return stations.stream()
                .filter(station -> station.isSameId(stationId))
                .findFirst()
                .orElseThrow(() -> new SubwayException(stationId + " : 일치하는 역이 없습니다."));
    }

    public void checkCreatableEdge(final List<Long> stationIds) {
        List<Long> persistIds = getPersistIds();
        boolean notContain = !persistIds.containsAll(stationIds);
        if (notContain) {
            throw new SubwayException(stationIds + " : 생성할수 없는 구간 값 입니다.");
        }
    }

    private List<Long> getPersistIds() {
        return stations.stream()
                .map(Station::getId)
                .collect(Collectors.toList());
    }


}
