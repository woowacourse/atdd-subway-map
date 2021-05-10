package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.line.section.SectionRequest;
import wooteco.subway.line.section.SectionService;
import wooteco.subway.line.section.Sections;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineService(final LineDao lineDao, final SectionService sectionService, final StationService stationService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse createLine(final LineRequest lineRequest) {
        final Line line = lineDao.save(lineRequest.toEntity());

        final Long upStationId = lineRequest.getUpStationId();
        final Long downStationId = lineRequest.getDownStationId();
        final int distance = lineRequest.getDistance();

        sectionService.createSection(line.getId(), new SectionRequest(upStationId, downStationId, distance));
        return LineResponse.from(line);
    }

    public List<LineResponse> findLines() {
        return lineDao.findAll().stream().
            map(LineResponse::from).
            collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        final Line line = lineDao.findById(id).orElseThrow(() -> new DataNotFoundException("해당 Id의 노선이 없습니다."));
        final Sections sections = sectionService.findSectionsByLineId(id);
        final List<Station> stations = sections.distinctStationIds().stream()
            .map(stationService::findById)
            .collect(Collectors.toList());
        line.addStations(stations);
        return LineResponse.from(line);
    }

    public void updateLine(final Long id, final LineRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLine(final Long id) {
        lineDao.deleteById(id);
    }
}
