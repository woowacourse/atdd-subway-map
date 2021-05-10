package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.exception.NoLineException;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionH2Dao;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.StationResponse;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {

    private final SectionService sectionService;
    private final LineDao lineDao;
    private final SectionH2Dao sectionH2Dao;

    private LineService(SectionService sectionService, LineDao lineDao, SectionH2Dao sectionH2Dao) {
        this.sectionService = sectionService;
        this.lineDao = lineDao;
        this.sectionH2Dao = sectionH2Dao;
    }

    public Line add(LineRequest lineRequest) {
        List<Line> lines = lineDao.findAll();
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDuplicatedLine(lines, line);
        Line savedLine = lineDao.save(line);
        Section section = new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionH2Dao.save(savedLine.getId(), section);
        return savedLine;
    }

    private void validateDuplication(String name) {
        if (isDuplicated(name)) {
            throw new LineDuplicationException();
        }
    }

    private boolean isDuplicated(String name) {
        return lineDao.findByName(name).isPresent();
    }

    public List<Line> lines() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(NoLineException::new);
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }

    public List<StationResponse> sortedStationsByLineId(Long id) {
        return sectionService.sortedStationIds(id);
    }
}
