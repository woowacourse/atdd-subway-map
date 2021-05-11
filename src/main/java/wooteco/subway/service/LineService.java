package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.controller.request.LineAndSectionCreateRequest;
import wooteco.subway.exception.line.LineColorDuplicateException;
import wooteco.subway.exception.line.LineNameDuplicateException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.exception.section.BothEndStationsSameException;
import wooteco.subway.service.dto.LineDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineDto create(LineAndSectionCreateRequest lineAndSectionCreateRequest) {
        validate(lineAndSectionCreateRequest);
        final Long id = lineDao.insert(lineAndSectionCreateRequest.toLine());
        final Line line = lineDao.findById(id);
        sectionDao.insert(id, lineAndSectionCreateRequest.toSimpleSection());
        return new LineDto(line);
    }

    public List<LineDto> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineDto::new)
                .collect(Collectors.toList());
    }

    public LineDto findById(Long id) {
        if (!lineDao.isExistById(id)) {
            throw new LineNotFoundException();
        }
        final Line line = lineDao.findById(id);
        return new LineDto(line);
    }

    public void isExistById(Long id) {
        if (!lineDao.isExistById(id)) {
            throw new LineNotFoundException();
        }
    }

    public void updateById(Long id, LineAndSectionCreateRequest lineAndSectionCreateRequest) {
        lineDao.update(id, lineAndSectionCreateRequest.toLine());
    }

    public void deleteById(Long id) {
        lineDao.delete(id);
    }

    private void validate(LineAndSectionCreateRequest lineAndSectionCreateRequest) {
        validateSameEndStations(lineAndSectionCreateRequest.getUpStationId(),
                lineAndSectionCreateRequest.getDownStationId());
        validateDuplicateName(lineAndSectionCreateRequest.getName());
        validateDuplicateColor(lineAndSectionCreateRequest.getColor());
    }

    private void validateSameEndStations(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new BothEndStationsSameException();
        }
    }

    private void validateDuplicateName(String name) {
        if (lineDao.isExistByName(name)) {
            throw new LineNameDuplicateException();
        }
    }

    private void validateDuplicateColor(String color) {
        if (lineDao.isExistByColor(color)) {
            throw new LineColorDuplicateException();
        }
    }
}
