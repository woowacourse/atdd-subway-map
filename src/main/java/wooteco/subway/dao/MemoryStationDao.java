package wooteco.subway.dao;

import wooteco.subway.domain.Station;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MemoryStationDao implements StationDao {
    private List<Station> stations = new ArrayList<>();

    private AtomicLong sequence = new AtomicLong();

    @Override
    public long save(Station station) {
        stations.add(new Station(sequence.incrementAndGet(), station.getName()));
        return sequence.get();
    }

    @Override
    public List<Station> findAll() {
        return Collections.unmodifiableList(stations);
    }

    @Override
    public Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Station> findByIdIn(LinkedList<Long> sortedStations) {
        return stations.stream()
                .filter(station -> station.getId().equals(sortedStations))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public boolean existById(Long id) {
        return stations.stream()
                .anyMatch(station -> station.getId().equals(id));
    }

    @Override
    public boolean existByName(String name) {
        return stations.stream()
                .anyMatch(station -> station.getName().equals(name));
    }

    @Override
    public void deleteById(Long id) {
        Station found = findById(id).get();
        stations.remove(found);
    }

    @Override
    public void deleteAll() {
        stations.clear();
    }
}
