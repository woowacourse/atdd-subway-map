package wooteco.subway.domain.station;

import java.util.List;

public interface StationRepository {

    Long saveStation(Station station);

    List<Station> findStations();

    Station findStationById(Long stationId);

    void removeStation(Long stationId);
}
