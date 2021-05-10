package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionH2Dao;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionH2Dao sectionH2Dao;

    private LineService(LineDao lineDao, SectionH2Dao sectionH2Dao) {
        this.lineDao = lineDao;
        this.sectionH2Dao = sectionH2Dao;
    }

    public Line add(LineRequest lineRequest) {
        List<Line> lines = lineDao.findAll();
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDuplicatedLine(lines, line);
        Line savedLine = lineDao.save(line);
        Section section = new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionH2Dao.save(savedLine.getId(), section);
        return savedLine;
    }

    private void validateDuplicatedLine(List<Line> lines, Line newLine) {
        if (isDuplicatedColor(lines, newLine)) {
            throw new LineDuplicationException();
        }
    }

    private boolean isDuplicatedColor(List<Line> lines, Line newLine) {
        return lines.stream()
                .anyMatch(line -> line.isSameColor(newLine));
    }

    public List<Line> lines() {
        return lineDao.findAll();
    }

    public Optional<Line> line(Long id) {
        return lineDao.findById(id);
    }

    public void update(Long id, String name, String color) {
        lineDao.update(id, name, color);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
