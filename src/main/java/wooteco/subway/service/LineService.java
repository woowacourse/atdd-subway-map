package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.response.LineResponse;

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

    public LineResponse create(final CreateLineRequest request) {
        final Long lineId = lineDao.save(request.toLine());
        sectionDao.save(
                new Section(lineId, request.getUpStationId(), request.getDownStationId(), request.getDistance()));
        return show(lineId);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showAll() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(this::findStations)
                .collect(Collectors.toList());
    }

    private LineResponse findStations(final Line line) {
        final Sections sections = sectionDao.findAllByLineId(line.getId());
        final List<Station> stations = sections.toSortedStationIds()
                .stream()
                .map(stationDao::findById)
                .collect(Collectors.toList());
        return LineResponse.of(line, stations);
    }

    @Transactional(readOnly = true)
    public LineResponse show(final Long id) {
        final Line line = lineDao.find(id);
        return findStations(line);
    }

    public void updateLine(final long id, final LineRequest request) {
        lineDao.find(id);
        lineDao.update(id, request.getName(), request.getColor());
    }

    public void deleteLine(final long id) {
        lineDao.find(id);
        lineDao.delete(id);
    }
}
