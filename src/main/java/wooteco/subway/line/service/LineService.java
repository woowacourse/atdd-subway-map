package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;

import java.util.Collections;
import java.util.LinkedList;
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
        final Line line = findById(id);
        sectionDao.save(line);

        return new LineResponse(line);
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
        final Line old = findById(updating.getId());
        return old.isDifferentName(updating);
    }

    private void validateName(final String name) {
        if (lineDao.isExistingName(name)) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }
    }

    public void delete(final Long id) {
        if (lineDao.isNonExisting(id)) {
            throw new LineException("존재하지 않는 노선입니다.");
        }
        sectionDao.deleteAllSectionInLine(id);
        lineDao.delete(id);
    }

    public Line findById(final Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new LineException("존재하지 않는 노선입니다."));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public List<Long> allStationIdInLine(final Long lineId) {
        if (sectionDao.stationCountInLine(lineId) == 0) {
            return Collections.EMPTY_LIST;
        }
        return backStations(lineId, lineDao.findUpStationId(lineId), lineDao.findDownStationId(lineId));
    }

    private List<Long> backStations(Long lineId, Long frontStationId, Long downStationId) {
        final List<Long> stations = new LinkedList<>();

        while (!frontStationId.equals(downStationId)) {
            stations.add(frontStationId);
            Section next = sectionDao.findSectionByFrontStation(lineId, frontStationId);
            frontStationId = next.back();
        }
        stations.add(frontStationId);

        return stations;
    }
}
