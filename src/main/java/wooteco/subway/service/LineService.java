package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.entity.LineEntity;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineService(final LineDao lineDao,
                       final StationService stationService,
                       final SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Line register(final String name,
                         final String color,
                         final Long upStationId,
                         final Long downStationId,
                         final int distance) {
        try {
            final Station upStation = stationService.searchById(upStationId);
            final Station downStation = stationService.searchById(downStationId);
            return saveLineAndFirstSection(name, color, upStation, downStation, distance);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateNameException("[ERROR] 이미 존재하는 노선 이름입니다.");
        }
    }

    private Line saveLineAndFirstSection(final String name,
                                         final String color,
                                         final Station upStation,
                                         final Station downStation,
                                         final int distance) {
        final LineEntity savedLineEntity = lineDao.save(LineEntity.createWithoutId(name, color));
        final Sections sections = new Sections(new Section(savedLineEntity.getId(), upStation, downStation, distance));
        final Line line = new Line(savedLineEntity.getId(), name, color, sections);
        sectionService.resisterFirst(line.getId(), upStation.getId(), downStation.getId(), distance);
        return line;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Line searchById(final Long id) {
        final LineEntity lineEntity = lineDao.findById(id)
                .orElseThrow(() -> new NotFoundLineException("[ERROR] 노선이 존재하지 않습니다"));
        final Sections sections = sectionService.searchSectionsByLineId(id);
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), sections);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Line> searchAll() {
        return lineDao.findAll()
                .stream()
                .map(lineEntity ->
                        new Line(
                                lineEntity.getId(),
                                lineEntity.getName(),
                                lineEntity.getColor(),
                                sectionService.searchSectionsByLineId(lineEntity.getId())
                        )
                ).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void modify(final Long id, final String name, final String color) {
        final Optional<LineEntity> lineEntityToBeModified = lineDao.findById(id);
        lineEntityToBeModified.ifPresentOrElse(
                lineEntity -> {
                    updateLineData(id, name, color);
                },
                () -> {
                    throw new NotFoundLineException("[ERROR] 노선이 존재하지 않습니다");
                }
        );
    }

    private void updateLineData(final Long id, final String name, final String color) {
        try {
            final Sections sections = sectionService.searchSectionsByLineId(id);
            final Line newLine = new Line(id, name, color, sections);
            lineDao.update(new LineEntity(newLine));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateNameException("[ERROR] 이름이 중복되어 데이터를 수정할 수 없습니다.");
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void remove(final Long id) {
        lineDao.deleteById(id);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void resisterSection(final Long lineId,
                                final Long upStationId,
                                final Long downStationId,
                                final Integer distance) {
        sectionService.resister(lineId, upStationId, downStationId, distance);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeStation(final Long lineId, final Long stationId) {
        sectionService.removeStation(lineId, stationId);
    }
}
