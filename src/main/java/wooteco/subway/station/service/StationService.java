package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.station.repository.StationRepository;
import wooteco.subway.station.repository.dto.StationDto;
import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationDto save(StationDto stationDto) {
        Station station = stationRepository.save(new Station(stationDto.getName()));
        return StationDto.from(station);
    }

    public List<StationDto> findAll() {
        List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .map(station -> StationDto.from(station))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationRepository.delete(id);
    }
}
