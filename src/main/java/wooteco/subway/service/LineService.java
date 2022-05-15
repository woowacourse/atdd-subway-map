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
import wooteco.subway.exception.NotFoundException;

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
            sectionService.save(
                    new SectionSaveRequest(saved.getId(), request.getUpStationId(), request.getDownStationId(),
                            request.getDistance()));
            List<Section> sections = sectionService.findByLineId(saved.getId());
            Line line = new Line(saved.getId(), saved.getName(), saved.getColor(), sections);
            return LineResponse.of(line, getStationsByLine(line));
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_LINE_NAME.getContent());
        }
    }

    private List<Station> getStationsByLine(Line line) {
        List<Section> sections = sectionService.findByLineId(line.getId());
        return stationService.getSortedStations(new Sections(sections));
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
                .map(lineEntity -> new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(),
                        sectionService.findByLineId(lineEntity.getId())))
                .map(line -> LineResponse.of(line, getStationsByLine(line)))
                .collect(Collectors.toList());
    }

    private Line toLine(LineEntity entity) {
        return new Line(entity.getId(), entity.getName(), entity.getColor(),
                sectionService.findByLineId(entity.getId()));
    }

    public LineResponse findById(Long id) {
        Line line = getLine(id);
        return LineResponse.of(line, getStationsByLine(line));
    }

    private Line getLine(Long id) {
        return lineDao.findById(id)
                .map(this::toLine)
                .orElseThrow(NotFoundException::new);
    }

    public void updateById(final Long id, final LineRequest request) {
        Line line = getLine(id);
        line.updateName(request.getName());
        line.updateColor(request.getColor());
        lineDao.update(LineEntity.from(line));
    }

    public void deleteById(final Long id) {
        sectionService.deleteByLineId(id);
        lineDao.deleteById(id);
    }
}
