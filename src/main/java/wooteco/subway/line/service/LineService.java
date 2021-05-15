package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.line.LineDuplicationException;
import wooteco.subway.exception.line.NoLineException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.domain.Station;

import java.util.List;

@Service
@Transactional
public class LineService {

    private final SectionService sectionService;
    private final LineDao lineDao;

    public LineService(SectionService sectionService, LineDao lineDao) {
        this.sectionService = sectionService;
        this.lineDao = lineDao;
    }

    public Line add(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());

        validateDuplicatedName(line.getName());
        validateDuplicatedColor(line.getColor());
        Line savedLine = lineDao.save(line);

        Section section = new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionService.addInitial(savedLine.getId(), section);
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

    @Transactional(readOnly = true)
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    @Transactional(readOnly = true)
    public Line findById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(NoLineException::new);
    }

    public void update(Long id, LineRequest lineRequest) {
        validateId(id);
        validateDuplicatedName(lineRequest.getName());
        validateDuplicatedColor(lineRequest.getColor());
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void delete(Long id) {
        validateId(id);
        sectionService.deleteByLine(id);
        lineDao.delete(id);
    }

    @Transactional(readOnly = true)
    public List<Station> findStationsByLineId(Long id) {
        validateId(id);
        return sectionService.sortedStationIds(id);
    }

    @Transactional(readOnly = true)
    public void validateId(Long lineId) {
        lineDao.findById(lineId)
            .orElseThrow(NoLineException::new);
    }
}
