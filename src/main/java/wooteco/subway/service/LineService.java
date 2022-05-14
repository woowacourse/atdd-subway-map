package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NoSuchStationException;

@Service
public class LineService {

    private final LineDao lineDao;

    private final StationDao stationDao;

    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse save(LineRequest request) {
        checkExistAllStations(request.getUpStationId(), request.getDownStationId());

        Long lineId = lineDao.save(new Line(request.getName(), request.getColor()));
        sectionDao.save(
                new Section(lineId, request.getUpStationId(), request.getDownStationId(), request.getDistance()));

        return new LineResponse(lineId, request.getName(), request.getColor(), getStationResponsesByLineId(lineId));
    }

    private void checkExistAllStations(Long upStationId, Long downStationId) {
        try {
            stationDao.findById(upStationId);
            stationDao.findById(downStationId);
        } catch (EmptyResultDataAccessException exception) {
            throw new NoSuchStationException();
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();

        return lines.stream()
                .map(it -> new LineResponse(
                        it.getId(), it.getName(), it.getColor(), getStationResponsesByLineId(it.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);

        return new LineResponse(id, line.getName(), line.getColor(), getStationResponsesByLineId(id));
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    @Transactional
    public void deleteById(Long id) {
        sectionDao.deleteAllByLineId(id);
        lineDao.deleteById(id);
    }

    private List<StationResponse> getStationResponsesByLineId(Long id) {
        Sections sections = new Sections(sectionDao.findAllByLineId(id));

        return sections.getSortedStationId().stream()
                .map(stationDao::findById)
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }
}
