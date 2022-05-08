package wooteco.subway.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private static final String DUPLICATED_NAME_ERROR_MESSAGE = "중복된 이름이 존재합니다.";
    private static final String NONE_LINE_ERROR_MESSAGE = "해당 ID의 노선은 존재하지 않습니다.";
    private static final String INVALID_DISTANCE_ERROR_MESSAGE = "유효하지 않은 거리입니다.";
    private static final String NONE_SECTION_ERROR_MESSAGE = "존재하지 않는 역입니다.";
    private static final int MIN_DISTANCE = 1;

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse save(LineCreateRequest line) {
        validDuplicatedName(line.getName());
        Long lineId = lineDao.save(line);

        SectionRequest sectionRequest = SectionRequest.from(lineId, line);
        validSection(sectionRequest);
        sectionDao.save(sectionRequest);

        List<StationResponse> stations = generateStationResponses(line.getDownStationId(), line.getUpStationId());
        return new LineResponse(lineId, line.getName(), line.getColor(), stations);
    }

    private void validSection(SectionRequest sectionRequest) {
        validDistance(sectionRequest.getDistance());
        validStation(sectionRequest.getDownStationId());
        validStation(sectionRequest.getUpStationId());
    }

    private void validDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(INVALID_DISTANCE_ERROR_MESSAGE);
        }
    }

    private void validStation(Long id) {
        int count = stationDao.countById(id);
        if (count < 1) {
            throw new IllegalArgumentException(NONE_SECTION_ERROR_MESSAGE);
        }
    }

    private List<StationResponse> generateStationResponses(Long... ids) {
        return Arrays.stream(ids)
                .map(id -> StationResponse.from(stationDao.findById(id)))
                .collect(Collectors.toUnmodifiableList());
    }

    public void update(Long id, LineRequest lineRequest) {
        validDuplicatedName(lineRequest.getName());
        lineDao.update(id, lineRequest);
    }

    private void validDuplicatedName(String name) {
        if (lineDao.countByName(name) > 0) {
            throw new IllegalArgumentException(DUPLICATED_NAME_ERROR_MESSAGE);
        }
    }

    public LineResponse findById(Long id) {
        try {
            Line line = lineDao.findById(id);
            return LineResponse.from(line);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException(NONE_LINE_ERROR_MESSAGE);
        }
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
