package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.utils.exception.NameDuplicatedException;
import wooteco.subway.utils.exception.SubwayException;

@Transactional
@Service
public class StationService {

    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public StationService(StationRepository stationRepository,
                          SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public StationResponse save(final StationRequest stationRequest) {
        String name = stationRequest.getName();
        validateDuplicateName(stationRepository.isNameExists(name), name);

        Station station = stationRepository.save(new Station(name));
        return new StationResponse(station.getId(), station.getName());
    }

    private void validateDuplicateName(final boolean isDuplicateName, final String name) {
        if (isDuplicateName) {
            throw new NameDuplicatedException(NameDuplicatedException.NAME_DUPLICATE_MESSAGE + name);
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> showStations() {
        return stationRepository.findAll().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteStation(final Long id) {
        if (sectionRepository.isStationExist(id)) {
            throw new SubwayException("[ERROR] 역이 구간에 존재하여 삭제할 수 없습니다.");
        };
        stationRepository.deleteById(id);
    }
}
