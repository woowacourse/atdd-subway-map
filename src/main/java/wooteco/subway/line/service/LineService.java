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
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.model.Section;
import wooteco.subway.section.model.SectionRepository;
import wooteco.subway.section.model.Sections;
import wooteco.subway.station.api.dto.StationResponse;
import wooteco.subway.station.dao.StationDao;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final SectionRepository sectionRepository;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao,
        SectionRepository sectionRepository) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
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
        sectionDao.save(section);
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

//    private List<Section> mapToSections(List<Section> sectionDtos) {
//        return sectionDtos.stream()
//            .map(sectionDto -> new Section(lineDao.findLineById(sectionDto.getLineId())
//                .orElseThrow(() -> new NotFoundException("존재하지 않는 노선 ID 입니다.")),
//                stationDao.findStationById(sectionDto.getUpStationId()),
//                stationDao.findStationById(sectionDto.getDownStationId()),
//                sectionDto.getDistance()))
//            .collect(Collectors.toList());
//    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return LineResponse.listOf(lines);
    }

    @Transactional
    public void deleteById(Long id) {
        sectionDao.deleteAllByLineId(id);
        lineDao.deleteById(id);
    }

    public void update(Long id, LineRequest lineRequest) {
        Line updatedLine = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(updatedLine);
    }

}
