package wooteco.subway.line;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineResponses;
import wooteco.subway.section.Section;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.dto.StationResponse;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        Section section = new Section(newLine.getId(), lineRequest.getUpStationId(),
            lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionDao.save(section);
        return new LineResponse(newLine.getId(), newLine.getName(),
            newLine.getColor(), sectionToStations(sectionDao.findSectionsByLineId(newLine.getId())));
    }

    private List<StationResponse> sectionToStations(List<Section> sections) {
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return LineResponses.from(lines).toList();
    }

    // TODO 구간에 포함된 역 목록 응답
    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
            new ArrayList<>());
    }

    public void updateById(Long id, LineRequest lineRequest) {
        Line persistedLine = lineDao.findById(id);
        Line updatedLine = new Line(persistedLine.getId(), lineRequest.getName(),
            lineRequest.getColor());
        lineDao.update(updatedLine);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
        sectionDao.deleteAllById(id);
    }
}
