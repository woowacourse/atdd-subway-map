package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.exception.ClientException;

public class FakeStationDao implements StationDao {

    private static final Map<String, Station> STATIONS = new HashMap<>();

    private static Long seq = 0L;

    @Override
    public Station save(StationRequest stationRequest) {
        if (STATIONS.containsKey(stationRequest.getName())) {
            throw new ClientException("이미 등록된 지하철역입니다.");
        }
        Station persistStation = new Station(++seq, stationRequest.getName());
        STATIONS.put(stationRequest.getName(), persistStation);
        return persistStation;
    }
    
    @Override
    public List<Station> findAll() {
        return STATIONS.keySet()
                .stream()
                .map(STATIONS::get)
                .collect(Collectors.toList());
    }
    
    @Override
    public int deleteStation(long id) {
        String stationName = STATIONS.keySet()
                .stream()
                .filter(key -> STATIONS.get(key).getId() == id)
                .findAny()
                .orElseThrow(() -> new ClientException("존재하지 않는 지하철입니다."));
        STATIONS.remove(stationName);
        if (STATIONS.containsKey(stationName)) {
            return 0;
        }
        return 1;
    }
}
