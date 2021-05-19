package wooteco.subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.line.LineDuplicationException;
import wooteco.subway.exception.line.LineNonexistenceException;
import wooteco.subway.exception.station.StationNonexistenceException;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionDao;
import wooteco.subway.section.Sections;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Line add(LineRequest lineRequest) {
        validate(lineRequest);

        Line savedLine = saveLine(lineRequest);
        saveSection(lineRequest, savedLine);

        return savedLine;
    }

    private Line saveLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        return lineDao.save(line);
    }

    private void saveSection(LineRequest lineRequest, Line savedLine) {
        Section section = lineRequest.toSection();
        sectionDao.save(savedLine.getId(), section);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(LineNonexistenceException::new);
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        validate(lineRequest);
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    private void validate(LineRequest lineRequest) {
        validateDuplicatedName(lineRequest.getName());
        validateDuplicatedColor(lineRequest.getColor());
    }

    private void validateDuplicatedName(String name) {
        lineDao.findByName(name)
                .ifPresent(this::throwDuplicationException);
    }

    private void validateDuplicatedColor(String color) {
        lineDao.findByColor(color)
                .ifPresent(this::throwDuplicationException);
    }

    private void throwDuplicationException(Line line) {
        throw new LineDuplicationException();
    }

    @Transactional
    public void delete(Long id) {
        lineDao.delete(id);
    }

    public List<StationResponse> sortedStationsByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Long> sortedStationIds = sections.sortedStationIds();
        return sortedStationIds.stream()
                .map(this::findByStationId)
                .collect(toList());
    }

    private StationResponse findByStationId(Long stationId) {
        return new StationResponse(stationDao.findById(stationId)
                .orElseThrow(StationNonexistenceException::new)
        );
    }
}
