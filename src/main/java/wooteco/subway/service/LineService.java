package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Service
public class LineService {
    static final String DUPLICATE_EXCEPTION_MESSAGE = "이름이나 색깔이 중복된 노선은 만들 수 없습니다.";
    static final String NO_EXIST_STATION = "존재하지 않는 역입니다.";

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Line create(Line line, Section section) {
        try {
            Line savedLine = lineDao.save(line);
            Section savedSection = sectionDao.save(
                    new Section(savedLine.getId(), section.getUpStationId(), section.getDownStationId(),
                            section.getDistance()));
            Station upStation = stationDao.findById(savedSection.getUpStationId());
            Station downStation = stationDao.findById(savedSection.getDownStationId());
            return new Line(savedLine, List.of(upStation, downStation));
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(DUPLICATE_EXCEPTION_MESSAGE);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException(NO_EXIST_STATION);
        }
    }

    public List<Line> showAll() {
        return lineDao.findAll();
    }

    public Line show(Long id) {
        return lineDao.findById(id);
    }

    @Transactional
    public void update(Line line) {
        try {
            lineDao.update(line);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
