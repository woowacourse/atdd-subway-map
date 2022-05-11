package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DataNotFoundException;

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

    public LineResponse save(LineRequest lineRequest) {
        validateLineRequest(lineRequest);

        Line line = lineRequest.toLine();
        Long savedLineId = lineDao.save(line);
        sectionDao.save(lineRequest.toSection(savedLineId));
        return findById(savedLineId);
    }

    private void validateLineRequest(LineRequest lineRequest) {
        validateNameDuplication(lineRequest.getName());
        validateStationExistence(lineRequest.getUpStationId());
        validateStationExistence(lineRequest.getDownStationId());
    }

    private void validateNameDuplication(String name) {
        if (lineDao.existByName(name)) {
            throw new IllegalArgumentException("중복된 지하철 노선 이름입니다.");
        }
    }

    private void validateStationExistence(Long stationId) {
        if (!stationDao.existById(stationId)) {
            throw new DataNotFoundException("존재하지 않는 지하철입니다.");
        }
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        validateExistence(id);
        Line line = lineDao.findById(id);
        return LineResponse.of(line);
    }

    public void update(Long id, LineRequest lineRequest) {
        validateExistence(id);
        validateNameDuplication(lineRequest.getName());
        Line line = lineRequest.toLine(id);
        lineDao.update(line);
    }

    private void validateExistence(Long id) {
        if (!lineDao.existById(id)) {
            throw new DataNotFoundException("존재하지 않는 지하철 노선입니다.");
        }
    }

    public void delete(Long id) {
        validateExistence(id);
        lineDao.delete(id);
    }
}
