package wooteco.subway.line.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.api.dto.LineDetailsResponse;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.line.api.dto.LineResponse;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.model.Line;
import wooteco.subway.section.model.Section;
import wooteco.subway.section.model.Sections;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.station.api.dto.StationResponse;
import wooteco.subway.station.dao.StationDao;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionRepository sectionRepository;

    public LineService(LineDao lineDao, StationDao stationDao,
        SectionRepository sectionRepository) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineDetailsResponse createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        Section section = Section.builder()
            .line(newLine)
            .upStation(stationDao.findStationById(lineRequest.getUpStationId()))
            .downStation(stationDao.findStationById(lineRequest.getDownStationId()))
            .distance(lineRequest.getDistance())
            .build();
        sectionRepository.save(section);
        return getLineDetailsResponse(newLine.getId());
    }

    @Transactional(readOnly = true)
    public LineDetailsResponse showLineById(Long id) {
        return getLineDetailsResponse(id);
    }

    private LineDetailsResponse getLineDetailsResponse(long createdId) {
        Line newLine = lineDao.findLineById(createdId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 노선 ID 입니다."));
        List<Section> findSections = sectionRepository.findSectionsByLineId(createdId);
        Sections sections = new Sections(findSections);
        return new LineDetailsResponse(newLine, StationResponse.listOf(sections.sortedStations()));
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return LineResponse.listOf(lines);
    }

    @Transactional
    public void deleteById(Long id) {
        sectionRepository.deleteAllByLineId(id);
        lineDao.deleteById(id);
    }

    public void update(Long id, LineRequest lineRequest) {
        Line updatedLine = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(updatedLine);
    }

}
