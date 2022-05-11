package wooteco.subway.service;

import org.springframework.stereotype.Service;
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

    public Line create(Line line, Section section) {
        if (checkExistByName(line.getName())) {
            throw new IllegalArgumentException("이미 같은 이름의 노선이 존재합니다.");
        }

        final Line savedLine = lineDao.save(line);
        sectionDao.save(new Section(section.getUpStation(), section.getDownStation(), section.getDistance(), savedLine.getId()));
        return savedLine;
    }

    public List<Line> queryAll() {
        return lineDao.findAll();
    }

    public Line queryById(Long id) {
        return extractLine(lineDao.findById(id));
    }

    public void modify(Long id, Line line) {
        extractLine(lineDao.findById(id));
        lineDao.update(id, line);
    }

    public void remove(Long id) {
        extractLine(lineDao.findById(id));
        lineDao.deleteById(id);
    }

    private boolean checkExistByName(String name) {
        return lineDao.findByName(name).isPresent();
    }

    private Line extractLine(Optional<Line> wrappedLine) {
        return wrappedLine.orElseThrow(() -> new IllegalArgumentException("해당 노선이 존재하지 않습니다."));
    }
}
