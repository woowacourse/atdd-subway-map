package wooteco.subway.line.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.exception.line.LineDuplicatedNameException;
import wooteco.subway.line.Line;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.line.dto.response.LineResponse;
import wooteco.subway.line.dto.response.LineStationsResponse;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.Station;
import wooteco.subway.station.service.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final Logger log = LoggerFactory.getLogger(LineService.class);

    private final LineDao lineDao;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, StationService stationService, SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineCreateResponse create(LineCreateRequest request) {
        Line line = new Line(request.getName(), request.getColor());
        Line newLine = lineDao.save(line);
        Section newSection = addInitialSection(request, newLine);
        return new LineCreateResponse(newLine, newSection);
    }

    private Section addInitialSection(LineCreateRequest request, Line newLine) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        if (upStation.equals(downStation)) {
            throw new SubwayException("같은 역을 등록할 수 없습니다.");
        }
        Section section = new Section(newLine, upStation, downStation, request.getDistance());
        return sectionService.add(section);
    }

    @Transactional
    public void addSection(Long id, SectionCreateRequest request) {
        Line line = lineSetting(id);
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        Section section = new Section(line, upStation, downStation, request.getDistance());
        line.addSection(section);
        sectionService.synchronizeDB(line);
    }

    private Line lineSetting(Long id) {
        Line line = lineDao.findById(id);
        Sections sections = sectionService.findByLine(line);
        line.updateSections(sections);
        return line;
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        log.info("지하철 모든 노선 조회 성공");
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineStationsResponse findBy(Long id) {
        Line line = lineSetting(id);
        sectionSetting(line.getSections());
        return new LineStationsResponse(line);
    }

    private void sectionSetting(Sections sections) {
        sections.getSections().forEach(section -> {
            Line line = lineDao.findById(section.lineId());
            Station upStation = stationService.findById(section.upStationId());
            Station downStation = stationService.findById(section.downStationId());
            section.update(line, upStation, downStation);
        });
    }

    @Transactional
    public void update(Long id, LineUpdateRequest request) {
        Line line = lineDao.findById(id);

        validatesNameDuplicationExceptOriginalName(request, line);

        line.update(request.getName(), request.getColor());
        lineDao.update(line);
        log.info("노선 정보 수정 완료");
    }

    private void validatesNameDuplicationExceptOriginalName(LineUpdateRequest request, Line line) {
        boolean isExist = lineDao.existByNameAndNotInOriginalName(request.getName(), line.getName());
        if (isExist) {
            throw new LineDuplicatedNameException();
        }
    }

    @Transactional
    public void delete(Long id) {
        lineDao.delete(id);
        log.info("노선 삭제 성공");
    }

    @Transactional
    public void deleteStationInSection(Long lineId, Long stationId) {
        Line line = lineSetting(lineId);
        Station station = stationService.findById(stationId);

        line.deleteStationInSection(station);

        sectionService.synchronizeDB(line);
    }
}
