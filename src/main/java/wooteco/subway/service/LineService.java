package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.Stations;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.repository.LineRepository;

@Service
@Transactional
public class LineService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineRepository lines;

    public LineService(SectionDao sectionDao, StationDao stationDao, LineRepository lines) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lines = lines;
    }

    public LineResponse save(final LineRequest request) {
        try {
            Line savedLine = saveLine(request);
            saveSection(savedLine.getId(), request);
            List<Station> stations = getStationsByLine(savedLine);
            return LineResponse.of(savedLine, stations);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_LINE_NAME.getContent());
        }
    }

    private Line saveLine(LineRequest request) {
        Line requestLine = new Line(request.getName(), request.getColor());
        return lines.save(requestLine);
    }

    private void saveSection(Long lineId, LineRequest request) {
        Section section = new Section(lineId, request.getUpStationId(), request.getDownStationId(),
                request.getDistance());
        sectionDao.save(section);
    }

    private List<Station> getStationsByLine(Line line) {
        List<Section> sectionsPerLine = sectionDao.findByLineId(line.getId());
        List<Long> stationIds = new Sections(sectionsPerLine).getSortedStationId();
        return getSortedStationsBy(stationIds);
    }

    private List<Station> getSortedStationsBy(List<Long> ids) {
        Stations stations = new Stations(stationDao.findByIds(ids));
        return stations.sortBy(ids);
    }

    public List<LineResponse> findAll() {
        return lines.findAll().stream()
                .map(line -> LineResponse.of(line, getStationsByLine(line)))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = getLineFromDao(id);
        return LineResponse.of(line, getStationsByLine(line));
    }

    private Line getLineFromDao(Long id) {
        return lines.findById(id)
                .map(lineEntity -> new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor()))
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_LINE_BY_ID.getContent()));
    }

    public void updateById(final Long id, final LineRequest request) {
        final Line updateLine = new Line(request.getName(), request.getColor());
        lines.update(id, updateLine);
    }

    public void deleteById(final Long id) {
        lines.deleteById(id);
    }
}
