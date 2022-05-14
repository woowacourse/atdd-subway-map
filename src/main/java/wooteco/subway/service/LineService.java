package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Line create(Line line, Section section) {
        validateNameExist(line);
        final Line savedLine = lineDao.save(line);
        sectionDao.save(new Section(section.getUpStation(), section.getDownStation(), section.getDistance(), savedLine.getId()));
        return savedLine;
    }

    @Transactional(readOnly = true)
    public List<Line> getAll() {
        return lineDao.findAll();
    }

    @Transactional(readOnly = true)
    public Line getById(Long id) {
        return extractLine(lineDao.findById(id));
    }

    @Transactional
    public void modify(Long id, Line line) {
        extractLine(lineDao.findById(id));
        validateNameExist(line);
        lineDao.update(id, line);
    }

    private void validateNameExist(Line line) {
        if (lineDao.findByName(line.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 같은 이름의 노선이 존재합니다.");
        }
    }

    @Transactional
    public void remove(Long id) {
        extractLine(lineDao.findById(id));
        lineDao.deleteById(id);
    }

    private Line extractLine(Optional<Line> wrappedLine) {
        return wrappedLine.orElseThrow(() -> new IllegalArgumentException("해당 노선이 존재하지 않습니다."));
    }
}
