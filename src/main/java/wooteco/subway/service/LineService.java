package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateNameException;
import java.util.List;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public Line createLine(final Line line, final Section section) {
        validateDuplicateNameExist(line);
        validateStationsNames(section);
        final Line savedLine = lineDao.save(line);
        final Section sectionToSave = new Section(
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance(),
                savedLine.getId()
        );
        sectionDao.save(sectionToSave);
        return savedLine;
    }

    @Transactional(readOnly = true)
    public List<Line> getAllLines() {
        return lineDao.findAll();
    }

    @Transactional(readOnly = true)
    public Line getLineById(final Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 노선 ID입니다."));
    }

    @Transactional
    public void update(final Long id, final Line line) {
        final Line result = getLineById(id);
        validateUpdatedName(line, result);
        result.update(line);

        lineDao.update(id, result);
    }

    @Transactional
    public void delete(final Long id) {
        final int affectedRows = lineDao.deleteById(id);

        if (affectedRows == 0) {
            throw new DataNotFoundException("존재하지 않는 노선 id 입니다.");
        }
    }

    private void validateUpdatedName(final Line newLine, final Line result) {
        if (!result.getName().equals(newLine.getName())) {
            validateDuplicateNameExist(newLine);
        }
    }

    private void validateDuplicateNameExist(final Line line) {
        if (lineDao.existByName(line.getName())) {
            throw new DuplicateNameException("이미 존재하는 노선입니다.");
        }
    }

    private void validateStationsNames(final Section section) {
        final Station upStation = stationDao.findById(section.getUpStation().getId())
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 지하철 역입니다."));
        final Station downStation = stationDao.findById(section.getDownStation().getId())
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 지하철 역입니다."));

        if (upStation.getName().equals(downStation.getName())) {
            throw new DuplicateNameException("한 구간의 지하철 역들의 이름은 중복될 수 없습니다.");
        }
    }
}
