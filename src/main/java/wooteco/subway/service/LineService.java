package wooteco.subway.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
        Line line = new Line(request.getName(), request.getColor());
        Long lineId = lineDao.save(line);
        sectionDao.save(new Section(lineId, request.getUpStationId(), request.getDownStationId(), request.getDistance()));
        List<StationResponse> stations = getStationResponsesByLineId(lineId);

        return new LineResponse(lineId, line.getName(), line.getColor(), stations);
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
