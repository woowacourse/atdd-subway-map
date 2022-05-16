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
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.LineSaveRequest;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.service.dto.LineUpdateRequest;
import wooteco.subway.service.dto.StationResponse;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse save(final LineSaveRequest lineSaveRequest) {
        Line line = convertLine(lineSaveRequest);
        Section section = convertSection(lineSaveRequest);

        validateLine(line);

        long lineId = lineDao.save(line);
        sectionDao.save(lineId, section);
        return find(lineId);
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
                .map(line -> find(line.getId()))
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponse find(final Long id) {
        Line line = lineDao.find(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선입니다."));
        List<Station> stations = lineDao.findStations(id);

        List<StationResponse> stationResponses = convertStationResponses(stations);
        return LineResponse.from(line, stationResponses);
    }

    @Transactional
    public void update(final long id, final LineUpdateRequest lineUpdateRequest) {
        Line line = convertLine(lineUpdateRequest);
        validateLine(line);
        validateExistedLine(id);
        lineDao.update(id, line);
    }

    @Transactional
    public void delete(final Long id) {
        validateExistedLine(id);

        List<Station> stations = lineDao.findStations(id);
        lineDao.delete(id);
        sectionDao.delete(id);
        for (Station station : stations) {
            stationDao.delete(station.getId());
        }
    }

    private void validateLine(final Line line) {
        validateName(line);
        validateColor(line);
    }

    private void validateName(final Line line) {
        if (lineDao.existLineByName(line.getName())) {
            throw new IllegalArgumentException("지하철 노선 이름이 중복됩니다.");
        }
    }

    private void validateColor(final Line line) {
        if (lineDao.existLineByColor(line.getColor())) {
            throw new IllegalArgumentException("지하철 노선 색상이 중복됩니다.");
        }
    }

    private void validateExistedLine(final Long id) {
        if (!lineDao.existLineById(id)) {
            throw new IllegalArgumentException("존재하지 않는 지하철 노선입니다.");
        }
    }

    private Line convertLine(final LineSaveRequest lineSaveRequest) {
        return new Line(lineSaveRequest.getName(), lineSaveRequest.getColor());
    }

    private Line convertLine(final LineUpdateRequest lineUpdateRequest) {
        return new Line(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
    }

    private Section convertSection(final LineSaveRequest lineSaveRequest) {
        return new Section(lineSaveRequest.getUpStationId(), lineSaveRequest.getDownStationId(), lineSaveRequest.getDistance());
    }

    private List<StationResponse> convertStationResponses(final List<Station> stations) {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }
}
