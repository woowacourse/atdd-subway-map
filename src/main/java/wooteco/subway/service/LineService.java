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
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.duplicatename.LineDuplicateException;

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

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
        validateDuplicate(lineRequest);
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final Line newLine = lineDao.save(line);
        saveSection(newLine.getId(), lineRequest);
        return LineResponse.of(newLine, getStationsFromSection(newLine.getId()));
    }

    private void validateDuplicate(final LineRequest lineRequest) {
        if (hasDuplicateLine(lineRequest)) {
            throw new LineDuplicateException("이미 등록된 지하철 노선이름 입니다.");
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


    private List<StationResponse> getStationsFromSection(long lineId) {
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Station> stations = sections.getSortedStations();
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public int updateLine(final Long id, final LineRequest lineRequest) {
        validateDuplicate(lineRequest);
        lineDao.findById(id);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return lineDao.update(id, line);
    }


    public List<LineResponse> findLines() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line, getStationsFromSection(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        Line line = lineDao.findById(id);
        return LineResponse.of(line, getStationsFromSection(line.getId()));
    }

    public int deleteLine(final Long id) {
        lineDao.findById(id);
        return lineDao.delete(id);
    }
}
