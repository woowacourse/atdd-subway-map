package wooteco.subway.service;


import java.util.ArrayList;
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
import wooteco.subway.dto.LineResponse;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        validateName(newLine);
        long lineId = lineDao.save(newLine);

        Section newSection = new Section(
                lineId, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionDao.save(newSection);
        return createLineResponse(lineId);
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
                .map(it -> createLineResponse(it.getId()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        return createLineResponse(lineId);
    }

    @Transactional
    public void delete(Long lineId) {
        lineDao.deleteById(lineId);
    }

    @Transactional
    public void update(Long lineId, LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        validateName(newLine);

        lineDao.update(lineId, newLine);
    }

    private LineResponse createLineResponse(long lineId) {
        Line line = lineDao.findById(lineId);
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Station> stations = findStations(sections.getSortedStationId());
        return LineResponse.of(line, stations);
    }

    private List<Station> findStations(List<Long> sortedStationId) {
        List<Station> stations = new ArrayList<>();
        for (Long stationId : sortedStationId) {
            stations.add(stationDao.findById(stationId));
        }
        return stations;
    }

    private void validateName(Line line) {
        if (lineDao.existByName(line)) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
    }
}
