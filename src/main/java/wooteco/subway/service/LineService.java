package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.utils.ExceptionMessage;

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
        return new LineResponse(line.getId(), line.getName(), line.getColor(), sectionService.makeSectionsToStations(line.getId()));
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();

        return lines.stream()
                .map((line) -> new LineResponse(line.getId(), line.getName(), line.getColor(), sectionService.makeSectionsToStations(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse findById(long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NO_LINE));
        return new LineResponse(line.getId(), line.getName(), line.getColor(), sectionService.makeSectionsToStations(id));
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest);
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
    }

}
