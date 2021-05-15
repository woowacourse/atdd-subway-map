package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.OrderedStationIds;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Transactional
@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Line save(final Line line, final Section section) {
        checkDuplicateLineName(line);
        checkStationIsExist(section);
        final Line newLine = lineDao.save(line);
        final Section sectionWithNewLineId = Section.ofLineId(newLine.getId(), section);
        sectionDao.save(sectionWithNewLineId);
        return newLine;
    }

    @Transactional(readOnly = true)
    void checkDuplicateLineName(final Line line) {
        boolean existsName = lineDao.findByName(line.getName()).isPresent();
        if (existsName) {
            throw new DuplicateNameException("이미 존재하는 노선 이름입니다.");
        }
    }

    @Transactional(readOnly = true)
    void checkStationIsExist(final Section section) {
        final boolean existsUpStation = stationDao.findById(section.getUpStationId()).isPresent();
        final boolean existsDownStation = stationDao.findById(section.getDownStationId()).isPresent();
        if (!(existsUpStation && existsDownStation)) {
            throw new EntityNotFoundException("해당 ID와 일치하는 역이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    @Transactional(readOnly = true)
    public Line findById(final Long id) {
        return lineDao.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 ID와 일치하는 노선이 존재하지 않습니다."));
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

    @Transactional(readOnly = true)
    public List<Station> findStationsByLineId(final Long id) {
        final Line line = findById(id);
        final List<Section> sections = sectionDao.findAllByLineId(id);
        final Deque<Long> orderedStationIds = OrderedStationIds.of(line, sections).getOrderedStationIds();

        List<Station> orderedStations = new ArrayList<>();
        while (!orderedStationIds.isEmpty()) {
            orderedStations.add(findStationByStationId(orderedStationIds.pollFirst()));
        }

        return orderedStations;
    }

    @Transactional(readOnly = true)
    public Station findStationByStationId(final Long stationId) {
        return stationDao.findById(stationId)
                         .orElseThrow(() -> new EntityNotFoundException("해당 ID와 일치하는 역이 존재하지 않습니다."));
    }
}
