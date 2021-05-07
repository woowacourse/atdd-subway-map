package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionH2Dao;

import java.util.List;

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

    private void validateDuplication(String name) {
        if (isDuplicated(name)) {
            throw new LineDuplicationException();
        }
    }

    private boolean isDuplicated(String name) {
        return lineDao.findByName(name).isPresent();
    }

    public List<Line> lines() {
        return lineDao.findAll();
    }

    public Line line(Long id) {
        return lineDao.findById(id);
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
