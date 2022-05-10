package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.exception.LineDuplicateException;
import wooteco.subway.exception.LineNotFoundException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Line create(final LineRequest lineRequest) {
        final Line targetLine = lineRequest.toEntity();
        checkDuplicateName(targetLine);
        final Line createdLine = lineDao.save(targetLine);

        final Section targetSection = new Section(createdLine);
        sectionDao.save(targetSection);

        return createdLine;
    }

    private void checkDuplicateName(final Line line) {
        if (lineDao.existsName(line)) {
            throw new LineDuplicateException("이미 존재하는 노선입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(final Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new LineNotFoundException("해당 노선이 없습니다."));
    }

    @Transactional
    public void update(final Long id, final LineRequest lineRequest) {
        findById(id);
        final Line updatedLine = lineRequest.toEntity(id);
        checkDuplicateName(updatedLine);
        lineDao.update(updatedLine);
    }

    @Transactional
    public void delete(final Long id) {
        final Line targetLine = findById(id);
        lineDao.deleteById(targetLine.getId());
    }
}
