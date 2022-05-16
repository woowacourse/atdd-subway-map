package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Name;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.exception.line.DuplicateLineException;
import wooteco.subway.exception.line.NoSuchLineException;
import wooteco.subway.exception.station.NoSuchStationException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse create(final LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());
        final Line savedLine = lineDao.insert(line)
                .orElseThrow(DuplicateLineException::new);

        final Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(NoSuchStationException::new);
        final Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(NoSuchStationException::new);

        final Section section = new Section(
                savedLine,
                upStation,
                downStation,
                new Distance(request.getDistance())
        );
        sectionDao.insert(section);

        return LineResponse.of(savedLine, List.of(upStation, downStation));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(line -> {
                    final List<Station> stations = stationDao.findAllByLineId(line.getId());
                    return LineResponse.of(line, stations);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(final Long id) {
        final Line line = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);

        final List<Station> stations = findSortedStationsByLineId(line.getId());

        return LineResponse.of(line, stations);
    }

    private List<Station> findSortedStationsByLineId(final Long lineId) {
        final Sections sections = sectionDao.findAllByLineId(lineId);
        return sections.toStation();
    }

    public void updateById(final Long id, final LineRequest request) {
        final Line line = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);
        final Line updatedLine = new Line(line.getId(), new Name(request.getName()), request.getColor(), line.getSections());
        lineDao.updateById(id, updatedLine)
                .orElseThrow(DuplicateLineException::new);
    }

    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
