package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.EntityNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line save(final Line line, final Section section) {
        checkDuplicateLineName(line);
        final Line newLine = lineDao.save(line);
        final Section sectionWithNewLineId = Section.ofLineId(newLine.getId(), section);
        sectionDao.save(sectionWithNewLineId);
        return newLine;
    }

    private void checkDuplicateLineName(final Line line) {
        boolean existsName = lineDao.findByName(line.getName()).isPresent();
        if (existsName) {
            throw new DuplicateNameException("이미 존재하는 노선 이름입니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(final Long id) {
        return lineDao.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 노선이 존재하지 않습니다."));
    }

    public void update(final Long id, final Line line) {
        final boolean onlyChangeContent = findById(id).getName().equals(line.getName());
        final boolean isPresentName = lineDao.findByName(line.getName()).isPresent();
        if (!onlyChangeContent && isPresentName) {
            throw new DuplicateNameException("이미 존재하는 노선 이름입니다.");
        }
        lineDao.update(id, line);
    }

    public void delete(final Long id) {
        lineDao.delete(id);
    }

    public Set<Long> findStationIdsByLineId(final Long id) {
        final List<Section> sections = sectionDao.findAllById(id);

        Set<Long> stationsIds = new HashSet<>();
        for (Section section : sections) {
            stationsIds.add(section.getUpStationId());
            stationsIds.add(section.getDownStationId());
        }

        return stationsIds;
    }
}
