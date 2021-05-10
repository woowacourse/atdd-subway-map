package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.exception.NoLineException;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.StationResponse;

import java.util.List;

@Service
public class LineService {

    private final SectionService sectionService;
    private final LineDao lineDao;

    private LineService(SectionService sectionService, LineDao lineDao) {
        this.sectionService = sectionService;
        this.lineDao = lineDao;
    }

    public Line add(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDuplicatedName(line.getName());
        validateDuplicatedColor(line.getColor());

        Line savedLine = lineDao.save(line);
        Section section = new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionService.add(savedLine.getId(), section);
        return savedLine;
    }

    private void validateDuplicatedName(String name) {
        lineDao.findByName(name)
            .ifPresent(this::throwDuplicationException);
    }

    private void validateDuplicatedColor(String color) {
        lineDao.findByColor(color)
            .ifPresent(this::throwDuplicationException);
    }

    private void throwDuplicationException(Line line) {
        throw new LineDuplicationException();
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(NoLineException::new);
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }

    public List<StationResponse> sortedStationsByLineId(Long id) {
        return sectionService.sortedStationIds(id);
    }
}
