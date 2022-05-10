package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    static final String NAME_DUPLICATE_EXCEPTION_MESSAGE = "이름이 중복된 노선은 만들 수 없습니다.";
    static final String COLOR_DUPLICATE_EXCEPTION_MESSAGE = "색깔이 중복된 노선은 만들 수 없습니다.";

    private final LineDao lineDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse insertLine(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        validateRequest(line);
        Line newLine = lineDao.insert(line);

        Station upStation = stationDao.findById(lineRequest.getUpStationId());
        Station downStation = stationDao.findById(lineRequest.getDownStationId());

        StationResponse upStationResponse = new StationResponse(lineRequest.getUpStationId(), upStation.getName());
        StationResponse downStationResponse = new StationResponse(lineRequest.getDownStationId(),
                downStation.getName());

        return new LineResponse(newLine.getId(), lineRequest.getName(), lineRequest.getColor(),
                List.of(upStationResponse, downStationResponse));
    }

    private void validateRequest(Line line) {
        validateDuplicateName(line);
        validateDuplicateColor(line);
    }

    private void validateDuplicateName(Line line) {
        if (lineDao.existByName(line)) {
            throw new IllegalArgumentException(NAME_DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    private void validateDuplicateColor(Line line) {
        if (lineDao.existByColor(line)) {
            throw new IllegalArgumentException(COLOR_DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        validateRequest(new Line(lineRequest.getName(), lineRequest.getColor()));
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineDao.delete(id);
    }
}
