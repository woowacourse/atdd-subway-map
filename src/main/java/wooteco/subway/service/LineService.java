package wooteco.subway.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
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
        String color = request.getColor();
        String name = request.getName();
        Long upStationId = request.getUpStationId();
        Long downStationId = request.getDownStationId();
        int distance = request.getDistance();

        checkExistAllStations(upStationId, downStationId);

        Line line = new Line(name, color);
        Long lineId = lineDao.save(line);
        sectionDao.save(new Section(lineId, upStationId, downStationId, distance));
        List<StationResponse> stations = getStationResponsesByLineId(lineId);

        return new LineResponse(lineId, line.getName(), line.getColor(), stations);
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
        List<StationResponse> stations = getStationResponsesByLineId(id);

        return new LineResponse(id, line.getName(), line.getColor(), stations);
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteById(Long id) {
        sectionDao.deleteAllByLineId(id);
        lineDao.deleteById(id);
    }

    private List<StationResponse> getStationResponsesByLineId(Long id) {
        Set<Long> stationIds = new HashSet<>();
        sectionDao.findAllByLineId(id).forEach(section -> {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        });

        return stationIds.stream()
                .map(stationDao::findById)
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }
}
