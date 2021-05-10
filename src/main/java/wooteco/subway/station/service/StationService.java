package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.domain.Section;
import wooteco.subway.station.domain.Station;
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

    public Station createStation(final Station station) {
        if (stationRepository.isNameExist(station)) {
            throw new DuplicateStationNameException();
        }
        return stationRepository.save(station);
    }

    public List<Station> findAll() {
        return stationRepository.getStations();
    }

    public void delete(final Long id) {
        if (stationRepository.isIdNotExist(id)) {
            throw new NoSuchStationException();
        }
        stationRepository.deleteById(id);
    }

    public List<Station> getUpAndDownStations(final Section section) {
        Long upStationId = section.getUpStationId();
        Long downStationId = section.getDownStationId();

        String upStationName = findNameById(upStationId);
        String downStationName = findNameById(downStationId);

        return Arrays.asList(
                new Station(upStationId, upStationName),
                new Station(downStationId, downStationName)
        );
    }

    private String findNameById(final Long id) {
        if (stationRepository.isIdNotExist(id)) {
            throw new NoSuchStationException();
        }
        return stationRepository.findNameById(id);
    }
}
