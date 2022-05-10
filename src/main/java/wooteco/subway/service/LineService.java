package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.line.DuplicatedLineNameException;
import wooteco.subway.exception.line.InvalidLineIdException;
import wooteco.subway.exception.station.InvalidStationIdException;

@Service
public class LineService {

    private final LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Autowired
    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        final Line savedLine = saveLine(lineRequest);
        saveSection(lineRequest, savedLine.getId());
        final List<StationResponse> stationResponses =
                findStations(lineRequest.getUpStationId(), lineRequest.getDownStationId());
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor(), stationResponses);
    }

    private Line saveLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateLineName(line);
        return lineDao.save(line);
    }

    private void validateLineName(Line line) {
        if (lineDao.exists(line)) {
            throw new DuplicatedLineNameException();
        }
    }

    private void saveSection(LineRequest lineRequest, Long lineId) {
        final Section section = new Section(lineId, lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionDao.save(section);
    }

    private List<StationResponse> findStations(Long upStationId, Long downStationId) {
        validateStationId(upStationId, downStationId);
        final List<Station> stations = stationDao.findByIds(List.of(upStationId, downStationId));
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }

    private void validateStationId(Long upStationId, Long downStationId) {
        if (!stationDao.exists(upStationId) || !stationDao.exists(downStationId)) {
            throw new InvalidStationIdException();
        }
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(it -> findLineById(it.getId()))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        validateId(id);
        lineDao.deleteById(id);
    }

    public LineResponse findLineById(Long id) {
        validateId(id);
        Line line = lineDao.findById(id);
        Sections sections = new Sections(sectionDao.findByLineId(id));
        final List<StationResponse> stations = stationDao.findByIds(sections.getStationIds())
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toUnmodifiableList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    public void update(Long id, LineRequest lineRequest) {
        validateId(id);
        Line updatingLine = new Line(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(id, updatingLine);
    }

    private void validateId(Long id) {
        if (!lineDao.exists(id)) {
            throw new InvalidLineIdException();
        }
    }
}
