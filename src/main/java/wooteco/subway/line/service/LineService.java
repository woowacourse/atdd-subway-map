package wooteco.subway.line.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.api.dto.LineDetailsResponse;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.line.api.dto.LineResponse;
import wooteco.subway.line.api.dto.LineUpdateRequest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.model.Line;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.model.Section;
import wooteco.subway.section.model.Sections;
import wooteco.subway.station.api.dto.StationResponse;
import wooteco.subway.station.dao.StationDao;

import java.util.List;

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

    public LineDetailsResponse createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        long createdId = lineDao.save(line);
        Section section = sectionFromLineRequest(lineRequest, createdId);
        sectionDao.save(section);
        return getLineDetailsResponse(createdId);
    }

    private Section sectionFromLineRequest(LineRequest lineRequest, long createdId) {
        return Section.builder()
                .line(new Line(createdId, lineRequest.getName(), lineRequest.getColor()))
                .upStation(stationDao.findStationById(lineRequest.getUpStationId()))
                .downStation(stationDao.findStationById(lineRequest.getDownStationId()))
                .distance(lineRequest.getDistance())
                .build();
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return LineResponse.listOf(lines);
    }

    public void deleteById(Long id) {
        sectionDao.deleteAllByLineId(id);
        lineDao.deleteById(id);
    }

    public void update(Long id, LineUpdateRequest lineRequest) {
        Line updatedLine = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(updatedLine);
    }

    @Transactional(readOnly = true)
    public LineDetailsResponse showLineById(Long id) {
        return getLineDetailsResponse(id);
    }

    private LineDetailsResponse getLineDetailsResponse(long createdId) {
        Line newLine = lineDao.findLineById(createdId);
        List<Section> foundSections = sectionDao.findSectionsByLineId(createdId);
        Sections sections = new Sections(foundSections);

        return new LineDetailsResponse(newLine, StationResponse.listOf(sections.sortedStations()));
    }
}
