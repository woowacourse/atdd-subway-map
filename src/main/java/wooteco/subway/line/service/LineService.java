package wooteco.subway.line.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.line.DuplicateLineException;
import wooteco.subway.exception.line.NotFoundLineException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Color;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Name;
import wooteco.subway.line.dto.CreateLineDto;
import wooteco.subway.line.dto.LineServiceDto;

@Service
public class LineService {

    private static final int NOT_FOUND = 0;

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public Line save(final Line line) {
        Line rawLine = line;
        return lineDao.save(rawLine);
    }

    public Line show(final Long lineId) {
        return lineDao.show(lineId)
            .orElseThrow(() -> new NotFoundLineException());
    }

    private void checkExistedNameAndColor(CreateLineDto createLineDto) {
        String name = createLineDto.getName();
        String color = createLineDto.getColor();

        if (lineDao.countByColor(color) != 0) {
            throw new DuplicateLineException();
        }

        if (lineDao.countByName(name) != 0) {
            throw new DuplicateLineException();
        }
    }

    public List<LineServiceDto> findAll() {
        return lineDao.showAll()
            .stream()
            .map(LineServiceDto::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void update(@Valid final LineServiceDto lineServiceDto) {
        Line line = show(lineServiceDto.getId());
        Name name = new Name(lineServiceDto.getName());
        Color color = new Color(lineServiceDto.getColor());
        line.update(name, color);

        if (lineDao.update(line) == NOT_FOUND) {
            throw new NotFoundLineException();
        }
    }

    @Transactional
    public void delete(@Valid final LineServiceDto lineServiceDto) {
        if (lineDao.delete(lineServiceDto.getId()) == NOT_FOUND) {
            throw new NotFoundLineException();
        }
    }
}
