package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionSaveRequest;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.domain.LineException;
import wooteco.subway.exception.notfound.LineNotFoundException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineService(LineDao lineDao, SectionService sectionService, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    public LineResponse save(final LineRequest request) {
        try {
            LineEntity saved = lineDao.save(new LineEntity(null, request.getName(), request.getColor()));
            sectionService.save(new SectionSaveRequest(saved.getId(), request.getUpStationId(),
                    request.getDownStationId(), request.getDistance()));
            return createResponseFrom(saved);
        } catch (DuplicateKeyException e) {
            throw new LineException(ExceptionMessage.DUPLICATED_LINE_NAME.getContent());
        }
    }

    private LineResponse createResponseFrom(LineEntity entity) {
        List<Section> sections = sectionService.findByLineId(entity.getId());
        Line line = new Line(entity.getId(), entity.getName(), entity.getColor(), sections);
        return LineResponse.of(line, getSortedStationsBySections(sections));
    }

    private List<Station> getSortedStationsBySections(List<Section> sections) {
        return stationService.getSortedStations(new Sections(sections));
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
                .map(this::createResponseFrom)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return lineDao.findById(id)
                .map(this::createResponseFrom)
                .orElseThrow(LineNotFoundException::new);
    }

    public void updateById(final Long id, final LineRequest request) {
        lineDao.update(new LineEntity(id, request.getName(), request.getColor()));
    }

    public void deleteById(final Long id) {
        sectionService.deleteByLineId(id);
        lineDao.deleteById(id);
    }
}
