package wooteco.subway.admin.domain.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LineStationConvertService {

    private final StationRepository stationRepository;

    public LineStationConvertService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Set<Station> convertStation(Line line) {
        List<Long> lineStationsId = line.getLineStationsId();

        return lineStationsId.stream()
                .map(this::findStationById)
                .collect(Collectors.toSet());
    }

    private Station findStationById(Long lineId) {
        return stationRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
    }
}
