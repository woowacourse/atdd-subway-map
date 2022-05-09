package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;

import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao{

    private Long seq = 0L;
    private final List<Station> stations = new ArrayList<>();

    @Override
    public Station save(Station station) {
        validateDuplicateName(station);
        Station persistStation = createStation(station);
        stations.add(persistStation);
        return persistStation;
    }

    private void validateDuplicateName(Station station) {
        if (stations.contains(station)) {
            throw new DuplicateKeyException("이미 존재하는 데이터 입니다.");
        }
    }

    private Station createStation(Station station) {
        return new Station(++seq, station.getName());
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public boolean deleteById(Long id) {
        return stations.removeIf(it -> it.getId().equals(id));
    }
}
