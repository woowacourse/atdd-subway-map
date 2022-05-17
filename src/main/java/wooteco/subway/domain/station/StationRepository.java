package wooteco.subway.domain.station;

import java.util.List;

public interface StationRepository {

    Station saveStation(Station station);

    List<Station> findStations();

    Station findStationById(Long stationId);

    void removeStation(Long stationId);
}
