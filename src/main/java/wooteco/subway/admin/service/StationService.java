package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.domain.exception.BlankNotAllowedInStationNameException;
import wooteco.subway.admin.domain.exception.NumberNotAllowedInStationNameException;
import wooteco.subway.admin.domain.exception.RequireStationNameException;
import wooteco.subway.admin.dto.request.StationCreateRequest;
import wooteco.subway.admin.dto.response.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class StationService {
    private static final String NUMBER_REGEX = "[0-9]+";
    private static final String BLANK_SPACE = " ";

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public StationResponse createStation(StationCreateRequest request) {
        validate(request);
        Station station = request.toStation();
        Station persistStation = stationRepository.save(station);

        return StationResponse.of(persistStation);
    }

    public void deleteById(Long id) {
        stationRepository.deleteById(id);
    }

    private void validate(StationCreateRequest request) {
        String stationName = request.getName();
        if (stationName == null || stationName.isEmpty()) {
            throw new RequireStationNameException();
        }
        if (Pattern.compile(NUMBER_REGEX).matcher(stationName).find()) {
            throw new NumberNotAllowedInStationNameException();
        }
        if (stationName.contains(BLANK_SPACE)) {
            throw new BlankNotAllowedInStationNameException();
        }
    }
}