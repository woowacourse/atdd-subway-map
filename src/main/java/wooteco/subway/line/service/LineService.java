package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Sections;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse create(final LineRequest lineRequest) {
        validateName(lineRequest.getName());

        final Long id = lineDao.save(lineRequest.toLine());
        sectionDao.save(lineRequest.toLine(id), lineRequest.getDistance());

        return new LineResponse(lineDao.findById(id));
    }

    public void update(final Long id, final LineRequest lineRequest) {
        update(lineRequest.toLine(id));
    }

    public void update(final Line line) {
        if (isUpdatingName(line)) {
            validateName(line.getName());
        }
        lineDao.update(line);
    }

    private boolean isUpdatingName(final Line updating) {
        final Line old = lineDao.findById(updating.getId());
        return old.isDifferentName(updating);
    }

    private void validateName(final String name) {
        if (lineDao.isExistingName(name)) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }
    }

    public void delete(final Long id) {
        if (lineDao.isNotExist(id)) {
            throw new LineException("존재하지 않는 노선입니다.");
        }
        sectionDao.deleteAllSectionInLine(id);
        lineDao.delete(id);
    }

    public LineResponse findById(final Long id) {
        return new LineResponse(lineDao.findById(id));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public List<Long> allStationIdInLine(final Line line) {
        final Long lineId = line.getId();
        final Sections sections = new Sections(sectionDao.findSections(lineId));

        if (sections.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return sections.orderedIds(lineDao.findFirstStationId(lineId));
    }
}
