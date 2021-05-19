package wooteco.subway.station.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.RequestException;
import wooteco.subway.section.domain.SectionRepository;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationRepository;
import wooteco.subway.station.service.dto.StationCreateDto;
import wooteco.subway.station.service.dto.StationDto;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public StationService(StationRepository stationRepository, SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public StationDto save(final StationCreateDto stationInfo) {
        final Station requestedStation = new Station(stationInfo.getName());
        final Optional<Station> station = stationRepository.findByName(requestedStation.getName());

        if (station.isPresent()) {
            throw new RequestException("이미 존재하는 역 이름입니다.");
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

        if (sectionRepository.existingByStationId(id)) {
            throw new RequestException("노선의 구간에 추가된 역은 지울 수 없습니다.");
        }

        stationRepository.delete(id);
    }

    public Station findById(final Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new RequestException("존재하지 않는 역입니다"));
    }

    public List<Station> findAllById(final List<Long> stationIds) {
        return stationIds.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }
}
