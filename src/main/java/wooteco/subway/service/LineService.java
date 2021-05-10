package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.controller.request.LineRequest;
import wooteco.subway.exception.line.LineColorDuplicateException;
import wooteco.subway.exception.line.LineNameDuplicateException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.service.dto.LineDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineDto create(LineRequest lineRequest) {
        validate(lineRequest);
        final Long id = lineDao.insert(lineRequest.toEntity());
        final Line line = lineDao.findById(id);
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

    public void updateById(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.toEntity());
    }

    public void deleteById(Long id) {
        lineDao.delete(id);
    }

    private void validate(LineRequest lineRequest) {
        validateDuplicateName(lineRequest.getName());
        validateDuplicateColor(lineRequest.getColor());
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
