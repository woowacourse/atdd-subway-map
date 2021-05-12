package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.repository.StationRepository;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class StationService {
    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse createStation(final String name) {
        Station station = new Station(name);
        if (stationRepository.doesNameExist(station)) {
            throw new DuplicateStationNameException();
        }
        station = stationRepository.save(station);
        return StationResponse.toDto(station);
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationRepository.findAll();
        return StationResponse.toDtos(stations);
    }

    public void delete(final Long id) {
        if (stationRepository.doesIdNotExist(id)) {
            throw new NoSuchStationException();
        }
        stationRepository.deleteById(id);
    }

    public List<Station> getUpAndDownStations(final Long upStationId, final Long downStationId) {
        String upStationName = findNameById(upStationId);
        String downStationName = findNameById(downStationId);

        return Arrays.asList(
                new Station(upStationId, upStationName),
                new Station(downStationId, downStationName)
        );
    }

    private String findNameById(final Long id) {
        if (stationRepository.doesIdNotExist(id)) {
            throw new NoSuchStationException();
        }
        return stationRepository.findNameById(id);
    }
}
