package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.Stations;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.repository.LineRepository;

@Service
@Transactional
public class LineService {

    private final StationDao stationDao;
    private final LineRepository lines;

    public LineService(StationDao stationDao, LineRepository lines) {
        this.stationDao = stationDao;
        this.lines = lines;
    }

    public LineResponse save(final LineRequest request) {
        try {
            Line savedLine = saveLine(request);
            List<Station> stations = getStationsByLine(savedLine);
            return LineResponse.of(savedLine, stations);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_LINE_NAME.getContent());
        }
    }

    private Line saveLine(LineRequest request) {
        Section section = new Section(null, request.getUpStationId(), request.getDownStationId(),
                request.getDistance());
        Line requestLine = new Line(request.getName(), request.getColor(), List.of(section));
        return lines.save(requestLine);
    }

    private List<Station> getStationsByLine(Line line) {
        List<Long> sortedStationIds = line.getSortedStationId();
        return new Stations(stationDao.findByIds(sortedStationIds))
                .sortBy(sortedStationIds);
    }

    public List<LineResponse> findAll() {
        return lines.findAll().stream()
                .map(line -> LineResponse.of(line, getStationsByLine(line)))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lines.findById(id);
        return LineResponse.of(line, getStationsByLine(line));
    }

    public void updateById(final Long id, final LineRequest request) {
        final Line updateLine = new Line(id, request.getName(), request.getColor(), null);
        lines.update(updateLine);
    }

    public void deleteById(final Long id) {
        lines.deleteById(id);
    }
}
