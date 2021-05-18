package wooteco.subway.line;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineResponses;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.dto.StationResponse;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        sectionService.createSectionOfNewLine(newLine.getId(), lineRequest.getUpStationId(),
            lineRequest.getDownStationId(), lineRequest.getDistance());
        List<StationResponse> stations = sectionService.findAllByLineId(newLine.getId());
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), stations);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return LineResponses.toLineResponse(lines);
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        List<StationResponse> stationResponses = sectionService.findAllByLineId(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
            stationResponses);
    }

    @Transactional
    public void updateById(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteById(Long id) {
        lineDao.deleteById(id);
        sectionService.deleteAllByLineId(id);
    }
}
