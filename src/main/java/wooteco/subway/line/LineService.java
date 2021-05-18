package wooteco.subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.line.LineDuplicationException;
import wooteco.subway.exception.line.LineNonexistenceException;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.StationResponse;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final SectionService sectionService;
    private final LineDao lineDao;

    public LineService(SectionService sectionService, LineDao lineDao) {
        this.sectionService = sectionService;
        this.lineDao = lineDao;
    }

    @Transactional
    public Line add(LineRequest lineRequest) {
        validate(lineRequest);

        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line savedLine = lineDao.save(line);

        Section section = new Section(lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance());
        sectionService.addInitial(savedLine.getId(), section);

        return savedLine;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(LineNonexistenceException::new);
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        validate(lineRequest);
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    private void validate(LineRequest lineRequest) {
        validateDuplicatedName(lineRequest.getName());
        validateDuplicatedColor(lineRequest.getColor());
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

    @Transactional
    public void delete(Long id) {
        lineDao.delete(id);
    }

    public List<StationResponse> sortedStationsByLineId(Long id) {
        return sectionService.sortedStationIds(id);
    }

    public void validateId(Long lineId) {
        lineDao.findById(lineId)
                .orElseThrow(LineNonexistenceException::new);
    }
}
