package wooteco.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.constant.DuplicateException;
import wooteco.subway.exception.constant.NotExistException;

import java.util.List;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line saveAndGet(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        if (lineDao.existByName(name) || lineDao.existByColor(name)) {
            throw new DuplicateException();
        }
        long savedLineId = lineDao.save(new Line(name, color));
        sectionDao.save(new Section(upStationId, downStationId, distance, savedLineId));
        return new Line(savedLineId, name, color);
    }

    public Line saveAndGet(Line line, Section section) {
        if (lineDao.existByName(line.getName()) || lineDao.existByColor(line.getColor())) {
            throw new DuplicateException();
        }
        long savedLineId = lineDao.save(line);
        section.setLineId(savedLineId);
        sectionDao.save(section);
        return new Line(savedLineId, line.getName(), line.getColor());
    }

    @Transactional(readOnly = true)
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    @Transactional(readOnly = true)
    public Line findById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(NotExistException::new);
    }

    public Line update(Long id, String name, String color) {
        Line line = findById(id);

        if (isDuplicateName(line, name)) {
            throw new DuplicateException();
        }

        lineDao.update(new Line(id, name, color));

        return new Line(id, name, color);
    }

    private boolean isDuplicateName(Line line, String name) {
        return !line.isSameName(name) && lineDao.existByName(name);
    }

    public void deleteById(Long id) {
        if (!lineDao.existById(id)) {
            throw new NotExistException();
        }
        lineDao.deleteById(id);
    }
}
