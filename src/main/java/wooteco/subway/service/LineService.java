package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.AccessNoneDataException;
import wooteco.subway.exception.DataLengthException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse create(LineRequest request) {
        validateDataSize(request.getName(), request.getColor());
        validateExistStation(request.getUpStationId(), request.getDownStationId());
        validatePositiveDistance(request.getDistance());

        Line line = new Line(request.getName(), request.getColor());
        Line savedLine = lineDao.insert(line);

        Section section = new Section(savedLine.getId(),
                request.getUpStationId(), request.getDownStationId(), request.getDistance());
        sectionDao.insert(section);

        List<StationResponse> stationResponses = findStationByLineId(savedLine);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    private void validateDataSize(String name, String color) {
        if (name.isEmpty() || name.length() > 255) {
            throw new DataLengthException("노선 이름이 빈 값이거나 최대 범위를 초과했습니다.");
        }
        if (color.isEmpty() || color.length() > 20) {
            throw new DataLengthException("노선 색이 빈 값이거나 최대 범위를 초과했습니다.");
        }
    }

    private void validateExistStation(Long upStationId, Long downStationId) {
        if (!stationDao.existStationById(upStationId) || !stationDao.existStationById(downStationId)) {
            throw new IllegalArgumentException("존재하지 않는 역으로 구간을 만드는 시도가 있었습니다.");
        }
    }

    private void validatePositiveDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 사이의 거리는 0보다 커야합니다.");
        }
    }

    private List<StationResponse> findStationByLineId(Line savedLine) {
        List<Station> stations = stationDao.findAllByLineId(savedLine.getId());
        return stations.stream()
                .map(s -> new StationResponse(s.getId(), s.getName()))
                .collect(Collectors.toList());
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        Line line = lineDao.findById(lineId);
        return LineResponse.of(line);
    }

    public void update(Long lineId, LineRequest request) {
        validateExistData(lineId);
        lineDao.update(new Line(lineId, request.getName(), request.getColor()));
    }

    public void delete(Long lineId) {
        validateExistData(lineId);
        lineDao.delete(lineId);
    }

    private void validateExistData(Long lineId) {
        boolean isExist = lineDao.existLineById(lineId);
        if (!isExist) {
            throw new AccessNoneDataException();
        }
    }
}
