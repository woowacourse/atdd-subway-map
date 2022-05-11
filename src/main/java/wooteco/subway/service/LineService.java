package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateNameException;
import java.util.List;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line createLine(final Line line, final Section section) {
        validateDuplicateName(line);
        final Line savedLine = lineDao.save(line);
        final Section sectionToSave = new Section(
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance(),
                savedLine.getId()
        );
        sectionDao.save(sectionToSave);
        return savedLine;
    }

    public List<Line> getAllLines() {
        return lineDao.findAll();
    }

    public Line getLineById(final Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 노선 ID입니다."));
    }

    public void update(final Long id, final Line newLine) {
        validateDuplicateName(newLine);
        final Line result = getLineById(id);
        result.update(newLine);

        lineDao.update(id, result);
    }

    public void delete(final Long id) {
        lineDao.deleteById(id);
    }

    private void validateDuplicateName(final Line line) {
        if (lineDao.existByName(line.getName())) {
            throw new DuplicateNameException("이미 존재하는 노선입니다.");
        }
    }
}
