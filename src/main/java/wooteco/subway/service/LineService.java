package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
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
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateNameException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse save(final LineRequest lineRequest) {
        validateDuplicate(lineRequest);
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final Line newLine = lineDao.save(line);
        saveSection(newLine.getId(), lineRequest);
        return LineResponse.of(newLine, stationsOnLine(newLine.getId()));
    }

    private void validateDuplicate(final LineRequest lineRequest) {
        if (hasDuplicateLine(lineRequest)) {
            throw new DuplicateNameException("이미 등록된 지하철 노선이름 입니다.");
        }
    }

    private boolean hasDuplicateLine(final LineRequest lineRequest) {
        return lineDao.findAll()
                .stream()
                .anyMatch(line -> line.getName().equals(lineRequest.getName()));
    }

    private void saveSection(final Long lineId, final LineRequest lineRequest) {
        final Station upStation = stationDao.findById(lineRequest.getUpStationId());
        final Station downStation = stationDao.findById(lineRequest.getDownStationId());
        final Section section = new Section(lineId, upStation, downStation, lineRequest.getDistance());
        sectionDao.save(section);
    }


    private List<StationResponse> stationsOnLine(long lineId) {
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Station> stations = sections.getStations();
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public int updateLine(final Long id, final LineRequest lineRequest) {
        validateExist(id);
        validateDuplicate(lineRequest);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return lineDao.update(id, line);
    }


    public List<LineResponse> findLines() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line, stationsOnLine(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        try {
            Line line = lineDao.findById(id);
            return LineResponse.of(line, stationsOnLine(line.getId()));
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("존재하지 않는 노선입니다.");
        }
    }

    public int deleteLine(final Long id) {
        validateExist(id);
        return lineDao.delete(id);
    }

    private void validateExist(final long id) {
        try {
            lineDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("존재하지 않는 노선입니다.");
        }
    }
}
