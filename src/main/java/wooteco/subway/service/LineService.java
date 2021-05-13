package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineDao;
import wooteco.subway.domain.line.SortedStations;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionDao;
import wooteco.subway.domain.station.StationDao;
import wooteco.subway.web.dto.LineResponse;
import wooteco.subway.web.dto.SectionRequest;
import wooteco.subway.web.dto.StationResponse;
import wooteco.subway.web.exception.NotFoundException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse add(Line line, SectionRequest request) {
        Long lineId = addLine(line);
        sectionDao.save(new Section(lineId, request.toEntity()));
        return findById(lineId);
    }

    private Long addLine(Line line) {
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public LineResponse findById(Long id) {
        Line line = findLine(id);

        List<Section> sections = sectionDao.listByLineId(line.getId());

        List<StationResponse> stations = stationDao.stationsFilteredById(stationIds(sections))
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());

        List<StationResponse> sortedStations = new SortedStations(sections, stations).get();

        return new LineResponse(line, sortedStations);
    }

    private List<Long> stationIds(List<Section> sections) {
        List<Long> stationIds = new ArrayList<>();
        for (Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }
        return stationIds.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public void update(Long id, Line line) {
        findLine(id);
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        findLine(id);
        lineDao.delete(id);
    }

    private Line findLine(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 노선입니다"));
    }
}
