package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.LineCreateRequest;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.section.SectionRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
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

    public Long save(LineCreateRequest request) {
        String name = request.getName();
        if (lineDao.isExistName(name)) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME);
        }

        Line line = lineDao.save(name, request.getColor());
        sectionService.save(line.getId(), new SectionRequest(request));

        return line.getId();
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line, stationDao.findByLineId(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        List<Station> stations = stationDao.findByLineId(line.getId());
        return new LineResponse(line, stations);
    }

    public void deleteById(Long id) {
        lineDao.delete(id);
    }

    public void update(Long id, LineRequest request) {
        String name = request.getName();
        if (lineDao.isExistNameWithoutItself(id, name)) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME);
        }

        lineDao.update(id, name, request.getColor());
    }
}
