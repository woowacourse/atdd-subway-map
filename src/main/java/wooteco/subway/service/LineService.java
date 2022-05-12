package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.LineCreateRequest;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.section.SectionRequest;
import wooteco.subway.exception.BusinessException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private static final String DUPLICATE_LINE_NAME = "지하철 노선 이름이 중복될 수 없습니다.";

    private final SectionService sectionService;
    private final LineDao lineDao;
    private final StationDao stationDao;

    public LineService(SectionService sectionService, LineDao lineDao, StationDao stationDao) {
        this.sectionService = sectionService;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public LineResponse save(LineCreateRequest request) {
        validateName(request.getName());

        Line line = lineDao.save(request.getName(), request.getColor());
        sectionService.save(line.getId(), new SectionRequest(request));

        List<Station> stations = List.of(
                stationDao.findById(request.getUpStationId()),
                stationDao.findById(request.getDownStationId())
        );
        return new LineResponse(line, stations);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line, sectionService.findStationsByLineId(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        List<Station> stations = sectionService.findStationsByLineId(line.getId());
        return new LineResponse(line, stations);
    }

    public void deleteById(Long id) {
        lineDao.delete(id);
    }

    public void update(Long id, LineRequest request) {
        String name = request.getName();
        if (lineDao.isExistNameWithoutItself(id, name)) {
            throw new BusinessException(DUPLICATE_LINE_NAME);
        }

        lineDao.update(id, name, request.getColor());
    }

    private void validateName(String name) {
        if (lineDao.isExistName(name)) {
            throw new BusinessException(DUPLICATE_LINE_NAME);
        }
    }
}
