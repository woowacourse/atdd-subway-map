package wooteco.subway.station.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.controller.dto.StationCreateDto;
import wooteco.subway.station.controller.dto.StationDto;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationRepository;
import wooteco.subway.station.exception.StationException;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationRepository stationRepository;

    public StationService(final StationDao stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationDto save(final StationCreateDto stationInfo) {
        final Station requestedStation = new Station(stationInfo.getName());
        final Optional<Station> station = stationRepository.findByName(requestedStation.getName());

        if (station.isPresent()) {
            throw new StationException("이미 존재하는 역 이름입니다.");
        }

        final Station createdStation = stationRepository.save(requestedStation);
        return StationDto.of(createdStation);
    }

    public List<StationDto> showAll() {
        final List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .map(StationDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(final Long id) {
        findById(id);
        stationRepository.delete(id);
    }

    public Station findById(final Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new StationException("존재하지 않는 역입니다"));
    }

    public List<Station> findAllById(final List<Long> stationIds) {
        return stationIds.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }
}
