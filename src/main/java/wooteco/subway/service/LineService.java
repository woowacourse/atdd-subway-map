package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.*;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = lineDao.save(lineRequest);
        sectionService.saveInitialSection(lineRequest, line);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), sectionService.makeSectionsToStations(line.getId()).sortStations());
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();

        return lines.stream()
                .map((line) -> new LineResponse(line.getId(), line.getName(), line.getColor(), sectionService.makeSectionsToStations(line.getId()).sortStations()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다."));
        return new LineResponse(line.getId(), line.getName(), line.getColor(), sectionService.makeSectionsToStations(id).sortStations());
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
    }

}
