package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.notfoundexception.NotFoundLineException;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final SectionService sectionService;
    private final StationService stationService;
    private final LineDao lineDao;

    public LineService(SectionService sectionService, StationService stationService, LineDao lineDao) {
        this.sectionService = sectionService;
        this.stationService = stationService;
        this.lineDao = lineDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line line = lineRequest.createLine();
        Station upStation = stationService.findById(lineRequest.getUpStationId());
        Station downStation = stationService.findById(lineRequest.getDownStationId());

        checkDuplicated(line);

        Line newLine = lineDao.save(line);

        Section section = new Section(newLine.getId(), upStation, downStation, lineRequest.getDistance());
        sectionService.save(section);

        List<Long> stationIds = sectionService.findByLineId(newLine.getId()).getStationsId();
        List<Station> stations = stationService.findByIds(stationIds);

        return LineResponse.from(newLine, stations);
    }

    private void checkDuplicated(Line line) {
        if (checkDuplicatedLineName(line)) {
            throw new DuplicatedNameException();
        }
    }

    private boolean checkDuplicatedLineName(Line line) {
        return lineDao.findByName(line.getName()).isPresent();
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id).orElseThrow(NotFoundLineException::new);

        Sections sections = sectionService.findByLineId(line.getId());
        List<Station> stations = stationService.findByIds(sections.getStationsId());

        return LineResponse.from(line, stations);
    }

    public void delete(Long id) {
        lineDao.findById(id).orElseThrow(NotFoundLineException::new);
        lineDao.delete(id);
    }

    public void update(LineRequest lineRequest, long id) {
        Line line = lineRequest.createLine();

        lineDao.findById(id).orElseThrow(NotFoundLineException::new);
        lineDao.update(line, id);
    }
}
