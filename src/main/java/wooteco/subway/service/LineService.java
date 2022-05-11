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
import wooteco.subway.domain.Sections;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.service.dto.SectionRequest;
import wooteco.subway.service.dto.StationResponse;

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

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
        Line line = convertLine(lineRequest);
        Section section = convertSection(lineRequest);

        validateLine(line);

        long lineId = lineDao.save(line);
        sectionDao.save(lineId, section);
        return find(lineId);
    }

    @Transactional
    public void saveSection(final Long lineId, final SectionRequest sectionRequest) {
        validateStationInSection(sectionRequest);

        Section newSection = convertSection(sectionRequest);
        Sections sections = new Sections(sectionDao.findAllById(lineId));
        sections.add(newSection);

//        sectionDao.replace(sections);
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
    public void update(final long id, final LineRequest lineRequest) {
        Line line = convertLine(lineRequest);
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

    private void validateStationInSection(final SectionRequest sectionRequest) {
        if (!stationDao.existStationById(sectionRequest.getUpStationId())) {
            throw new IllegalArgumentException("상행역이 존재하지 않습니다.");
        }
        if (!stationDao.existStationById(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException("하행역이 존재하지 않습니다.");
        }
    }

    private void validateExistedLine(final Long id) {
        if (!lineDao.existLineById(id)) {
            throw new IllegalArgumentException("존재하지 않는 지하철 노선입니다.");
        }
    }

    private Line convertLine(final LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor());
    }

    private Section convertSection(final LineRequest lineRequest) {
        return new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    private Section convertSection(final SectionRequest sectionRequest) {
        return new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }

    private List<StationResponse> convertStationResponses(final List<Station> stations) {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }
}
