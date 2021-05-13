package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exceptions.LineDuplicationException;
import wooteco.subway.exceptions.LineNotFoundException;
import wooteco.subway.repository.LineDao;
import wooteco.subway.repository.SectionDao;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationService stationService;

    public LineService(LineDao lineDao, SectionDao sectionDao,
        StationService stationService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public Line createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());

        validateDuplication(line.getName());
        long lineId = lineDao.save(line);
        line.setId(lineId);

        Section section = createSection(lineRequest);
        long sectionId = sectionDao.save(section, lineId);
        section.setId(sectionId);

        line.addSection(section);

        return line;
    }

    public List<Line> findAll() {
        List<Line> lines = lineDao.findAll();
        lines.forEach(line -> {
            line.setSections(sectionDao.findByLine(line.getId()));
        });
        return lines;
    }

    public Line findById(Long id) {
        Line line = lineDao.findById(id)
            .orElseThrow(LineNotFoundException::new);
        Sections sections = sectionDao.findByLine(id);
        line.setSections(sections);
        return line;
    }

    public void editLine(Line line) {
        validateDuplication(line.getName());
        lineDao.updateLine(line);
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
        sectionDao.deleteByLineId(id);
    }

    private void validateDuplication(String name) {
        boolean isDuplicated = lineDao.findAll()
            .stream()
            .anyMatch(line -> line.getName().equals(name));
        if (isDuplicated) {
            throw new LineDuplicationException();
        }
    }

    private Section createSection(LineRequest lineRequest) {
        return new Section(
            stationService.findById(lineRequest.getUpStationId()),
            stationService.findById(lineRequest.getDownStationId()),
            lineRequest.getDistance()
        );
    }
}
