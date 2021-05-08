package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.line.NotFoundLineException;
import wooteco.subway.service.dto.LineServiceDto;

@Service
public class LineService {

    private static final int NOT_FOUND = 0;

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineServiceDto createLine(@Valid final LineServiceDto lineServiceDto) {
        Line line = new Line(lineServiceDto.getName(), lineServiceDto.getColor());
        Line saveLine = lineDao.create(line);
        return new LineServiceDto(saveLine.getId(), saveLine.getName(), saveLine.getColor());
    }

    public List<LineServiceDto> findAll() {
        return lineDao.showAll()
            .stream()
            .map(it -> new LineServiceDto(it.getId(), it.getName(), it.getColor()))
            .collect(Collectors.toList());
    }

    public LineServiceDto findOne(@Valid final LineServiceDto lineServiceDto) {
        Line line = lineDao.show(lineServiceDto.getId());
        return new LineServiceDto(line.getId(), line.getName(), line.getColor());
    }

    public void update(@Valid final LineServiceDto lineServiceDto) {
        Line line = new Line(lineServiceDto.getName(), lineServiceDto.getColor());

        if (lineDao.update(lineServiceDto.getId(), line) == NOT_FOUND) {
            throw new NotFoundLineException();
        }
    }

    public void delete(@Valid final LineServiceDto lineServiceDto) {
        if (lineDao.delete(lineServiceDto.getId()) == NOT_FOUND) {
            throw new NotFoundLineException();
        }
    }
}
