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
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.LineEntity;
import wooteco.subway.repository.entity.SectionEntity;
import wooteco.subway.repository.entity.StationEntity;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Line register(
            final String name,
            final String color,
            final Long upStationId,
            final Long downStationId,
            final int distance) {
        try {
            final LineEntity lineEntity = new LineEntity(Line.createWithoutId(name, color));
            final LineEntity savedLineEntity = lineDao.save(lineEntity);
            final Line line = new Line(savedLineEntity.getId(), savedLineEntity.getName(), savedLineEntity.getColor());
            saveSection(upStationId, downStationId, distance, line);
            return line;
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateNameException("[ERROR] 이미 존재하는 노선 이름입니다.");
        }
    }

    private void saveSection(final Long upStationId, final Long downStationId, final int distance, final Line line) {
        final StationEntity upStationEntity = stationDao.findById(upStationId)
                .orElseThrow(() -> new NotFoundStationException("[ERROR] 노선을 추가하는 도중 존재하지 않는 역으로 상행선을 지정했습니다."));
        final StationEntity downStationEntity = stationDao.findById(upStationId)
                .orElseThrow(() -> new NotFoundStationException("[ERROR] 노선을 추가하는 도중 존재하지 않는 역으로 하행선을 지정했습니다."));
        final Station upStation = new Station(upStationId, upStationEntity.getName());
        final Station downStation = new Station(downStationId, downStationEntity.getName());

        final Section section = Section.createWithoutId(line, upStation, downStation, distance);
        sectionDao.save(new SectionEntity(section));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Line searchById(final Long id) {
        LineEntity lineEntity = lineDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 노선이 존재하지 않습니다"));
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Line> searchAll() {
        return lineDao.findAll()
                .stream()
                .map(lineEntity -> new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor()))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void modify(final Long id, final String name, final String color) {
        final Optional<LineEntity> lineEntityToBeModified = lineDao.findById(id);
        lineEntityToBeModified.ifPresentOrElse(
                lineEntity -> {
                    updateLineData(id, name, color);
                },
                () -> {
                    throw new NoSuchElementException("[ERROR] 노선이 존재하지 않습니다");
                }
        );
    }

    private void updateLineData(final Long id, final String name, final String color) {
        try {
            Line newLine = new Line(id, name, color);
            lineDao.update(new LineEntity(newLine));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateNameException("[ERROR] 이름이 중복되어 데이터를 수정할 수 없습니다.");
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void remove(final Long id) {
        lineDao.deleteById(id);
    }
}
