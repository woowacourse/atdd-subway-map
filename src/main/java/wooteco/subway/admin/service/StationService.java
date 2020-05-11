package wooteco.subway.admin.service;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.domain.exception.DuplicationNameException;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Long findStationId(final String name) {
        return stationRepository.findByName(name)
                .map(Station::getId)
                .orElseGet(()->null);
    }

    public Station save(final String stationName) {
        validateName(stationName);
        Station station = new Station(stationName);
        return stationRepository.save(station);
    }

    private void validateName(final String stationName) {
        stationRepository.findByName(stationName)
                .ifPresent(station -> {
                    throw new DuplicationNameException();
                });
    }

    public List<Station> findAll() {
        return Lists.newArrayList(stationRepository.findAll());
    }

    public void deleteById(final Long id) {
        stationRepository.deleteById(id);
    }
}
