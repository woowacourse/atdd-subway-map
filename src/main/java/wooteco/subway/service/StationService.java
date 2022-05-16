package wooteco.subway.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.repository.SubwayRepository;

@Service
public class StationService {

    private static final String NAME_NOT_ALLOWED_EXCEPTION_MESSAGE = "해당 이름의 지하철역을 생성할 수 없습니다.";
    private static final String REGISTERED_STATION_EXCEPTION_MESSAGE = "노선에 등록된 역은 제거할 수 없습니다.";

    private final SubwayRepository subwayRepository;
    private final StationRepository stationRepository;

    public StationService(SubwayRepository subwayRepository, StationRepository stationRepository) {
        this.subwayRepository = subwayRepository;
        this.stationRepository = stationRepository;
    }

    public List<StationResponse> findAll() {
        return stationRepository.findAllStations()
                .stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .sorted(Comparator.comparingLong(StationResponse::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    public StationResponse save(StationRequest stationRequest) {
        String name = stationRequest.getName();
        validateUniqueName(name);

        Station newStation = stationRepository.save(new Station(name));
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    @Transactional
    public void delete(Long id) {
        Station station = stationRepository.findExistingStation(id);
        validateUnRegisteredStation(id);
        stationRepository.delete(station);
    }

    private void validateUniqueName(String name) {
        boolean isDuplicateName = stationRepository.checkExistingStationName(name);
        if (isDuplicateName) {
            throw new IllegalArgumentException(NAME_NOT_ALLOWED_EXCEPTION_MESSAGE);
        }
    }

    private void validateUnRegisteredStation(Long id) {
        boolean isUnRegistered = subwayRepository.findAllSectionsByStationId(id).isEmpty();
        if (!isUnRegistered) {
            throw new IllegalArgumentException(REGISTERED_STATION_EXCEPTION_MESSAGE);
        }
    }
}
