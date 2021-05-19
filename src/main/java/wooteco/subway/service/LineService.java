package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.controller.dto.LineEditRequest;
import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.EntityNotFoundException;

@Transactional
@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationService stationService;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationService stationService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public Line save(final LineRequest lineRequest) {
        checkDuplicateLineName(lineRequest.getName());

        final Station upStation = stationService.findById(lineRequest.getUpStationId());
        final Station downStation = stationService.findById(lineRequest.getDownStationId());
        final Section section = new Section(upStation, downStation, lineRequest.getDistance());
        final Line line = lineRequest.toEntity(section);

        final Line newLine = lineDao.save(line);
        sectionDao.save(newLine.getId(), section);

        return newLine;
    }

    public void checkDuplicateLineName(final String name) {
        boolean existsName = lineDao.findByName(name).isPresent();
        if (existsName) {
            throw new DuplicateNameException("이미 존재하는 노선 이름입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    @Transactional(readOnly = true)
    public Line findById(final Long id) {
        return lineDao.findById(id)
                      .orElseThrow(() -> new EntityNotFoundException("해당 ID와 일치하는 노선이 존재하지 않습니다."));
    }

    public void update(final Long id, final LineEditRequest lineEditRequest) {
        final Line beforeLine = findById(id);
        validateUpdateName(beforeLine, lineEditRequest.getName());
        final Line afterLine = lineEditRequest.toEntity(beforeLine);
        lineDao.updateTo(afterLine);
    }

    @Transactional(readOnly = true)
    void validateUpdateName(Line beforeLine, String editName) {
        final boolean onlyChangeContent = beforeLine.isNameEquals(editName);
        final boolean isPresentName = lineDao.findByName(editName).isPresent();
        if (!onlyChangeContent && isPresentName) {
            throw new DuplicateNameException("이미 존재하는 노선 이름입니다.");
        }
    }

    public void delete(final Long id) {
        lineDao.delete(id);
    }
}
