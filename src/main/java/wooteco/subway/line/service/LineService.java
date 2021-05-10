package wooteco.subway.line.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.exception.DuplicationException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.api.dto.LineUpdateRequest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.line.api.dto.LineResponse;
import wooteco.subway.line.model.Line;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.model.Section;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.model.Station;

import javax.validation.Valid;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    @Autowired
    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        long createdId = lineDao.save(line);
        Line newLine = lineDao.findLineById(createdId);
        sectionDao.save(createdId, lineRequest);
        return new LineResponse(newLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return LineResponse.listOf(lines);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    public void update(Long id, @Valid LineUpdateRequest lineRequest) {
        Line updatedLine = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(updatedLine);
    }

    @Transactional(readOnly = true)
    public LineResponse showLineById(Long id) {
        Line line = lineDao.findLineById(id);
        return new LineResponse(line);
    }
}
