package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.service.dto.SectionSaveRequest;
import wooteco.subway.repository.entity.LineEntity;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.domain.LineException;
import wooteco.subway.exception.notfound.LineNotFoundException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;
    private final StationService stationService;
    private final LineRepository lineRepository;

    public LineService(LineDao lineDao, SectionService sectionService, StationService stationService,
                       LineRepository lineRepository) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    public LineResponse save(final LineRequest request) {
        try {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            Section section = new Section(null, upStation, downStation, request.getDistance());
            Line line = new Line(request.getName(), request.getColor(), List.of(section));
            Line saved = lineRepository.save(line);
            return createResponseFrom(saved);
        } catch (DuplicateKeyException e) {
            throw new LineException(ExceptionMessage.DUPLICATED_LINE_NAME.getContent());
        }
    }

    private LineResponse createResponseFrom(Line line) {
        return LineResponse.of(line, line.getSortedStations());
    }

    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream()
                .map(this::createResponseFrom)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id);
        List<Station> sortedStations = line.getSortedStations();
        return LineResponse.of(line, sortedStations);
    }

    public void updateById(final Long id, final LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        Section section = new Section(null, id, upStation, downStation, request.getDistance());
        Line updated = new Line(id, request.getName(), request.getColor(), List.of(section));
        lineRepository.update(updated);
    }

    public void deleteById(final Long id) {
        sectionService.deleteByLineId(id);
        lineDao.deleteById(id);
    }
}
