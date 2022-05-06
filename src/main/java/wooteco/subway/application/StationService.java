package wooteco.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.repository.StationRepository;

@Service
@Transactional
public class StationService {

    private static final String NOT_FOUND_MESSAGE = "%d와 동일한 ID의 지하철이 없습니다.";
    public static final String DUPLICATION_MESSAGE = "%s는 중복된 지하철역 이름입니다.";

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station save(String name) {
        if (stationRepository.existByName(name)) {
            throw new DuplicateException(String.format(DUPLICATION_MESSAGE, name));
        }
        return stationRepository.save(new Station(name));
    }

    public void deleteById(Long id) {
        if (!stationRepository.existById(id)) {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
        stationRepository.deleteById(id);
    }

    public Station findById(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
    }
}
