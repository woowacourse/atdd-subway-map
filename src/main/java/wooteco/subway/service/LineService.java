package wooteco.subway.service;


import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao jdbcLineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = jdbcLineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

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

    public void delete(Long lineId) {
        lineDao.deleteById(lineId);
    }

    public void update(Long lineId, LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        validateName(newLine);

        lineDao.update(lineId, newLine);
    }

    private LineResponse createLineResponse(long lineId) {
        Line line = lineDao.findById(lineId);
        List<Station> stations = stationDao.findByLineId(lineId);
        return LineResponse.of(line, stations);
    }

    private void validateName(Line line) {
        if (lineDao.existByName(line)) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
    }
}
