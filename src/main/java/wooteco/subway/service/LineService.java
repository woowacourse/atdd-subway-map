package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.service.dto.LineServiceDto;
import wooteco.subway.domain.Line;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineServiceDto createLine(final LineServiceDto lineServiceDto) {
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

    public LineServiceDto findOne(final LineServiceDto lineServiceDto) {
        Line line = lineDao.show(lineServiceDto.getId());
        return new LineServiceDto(line.getId(), line.getName(), line.getColor());
    }

    public void update(final LineServiceDto lineServiceDto) {
        Line line = new Line(lineServiceDto.getName(), lineServiceDto.getColor());

        if (lineDao.update(lineServiceDto.getId(), line) == 0) {
            throw new EmptyResultDataAccessException(0);
        }
    }

    public void delete(final LineServiceDto lineServiceDto) {
        if (lineDao.delete(lineServiceDto.getId()) == 0) {
            throw new NotFoundLineException("[ERROR] 해당노선이 존재하지 않습니다.");
        }
    }
}
