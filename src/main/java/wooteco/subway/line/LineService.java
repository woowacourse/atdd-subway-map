package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DuplicateLineException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.LineDto;
import wooteco.subway.line.dto.LineIdDto;
import wooteco.subway.line.dto.NonIdLineDto;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineDto createLine(final NonIdLineDto nonIdLineDto) {
        String name = nonIdLineDto.getName();
        String color = nonIdLineDto.getColor();

        checkExistedNameAndColor(name, color);
        Line line = new Line(name, color);

        Line saveLine = lineDao.save(line);
        return new LineDto(saveLine.getId(), saveLine.getName(), saveLine.getColor());
    }

    private void checkExistedNameAndColor(String name, String color) {
        if (lineDao.countByColor(color) != 0) {
            throw new DuplicateLineException("[ERROR] 해당하는 노선의 색이 존재합니다.");
        }
        if (lineDao.countByName(name) != 0) {
            throw new DuplicateLineException("[ERROR] 해당하는 노선의 이름이 존재합니다.");
        }
    }

    public List<LineDto> findAll() {
        return lineDao.showAll()
            .stream()
            .map(it -> new LineDto(it.getId(), it.getName(), it.getColor()))
            .collect(Collectors.toList());
    }

    public LineDto findOne(final LineIdDto lineIdDto) {
        Line line = lineDao.show(lineIdDto.getId()).get();
        return new LineDto(line.getId(), line.getName(), line.getColor());
    }

    public void update(final LineDto lineDto) {
        String name = lineDto.getName();
        String color = lineDto.getColor();

        final Line line = lineDao.show(lineDto.getId())
            .orElseThrow(() -> new EmptyResultDataAccessException(0));
        checkUpdatedNameAndColor(lineDto);

        line.changeColor(color);
        line.changeName(name);
        lineDao.update(lineDto.getId(), line);
    }

    private void checkUpdatedNameAndColor(final LineDto lineDto) {
        String color = lineDto.getColor();
        String name = lineDto.getName();
        long id = lineDto.getId();
        Line line = lineDao.show(id).get();

        checkUpdatedColor(line, color);
        checkUpdatedName(line, name);
    }

    private void checkUpdatedColor(Line existedLine, String color) {
        String existedColor = existedLine.getColor();
        if ((lineDao.countByColor(color) != 0) && (!existedColor.equals(color))) {
            throw new DuplicateLineException("[ERROR] 해당하는 노선의 색이 존재합니다.");
        }
    }

    private void checkUpdatedName(Line existedLine, String name) {
        String existedName = existedLine.getName();
        if ((lineDao.countByName(name) != 0) && (!existedName.equals(name))) {
            throw new DuplicateLineException("[ERROR] 해당하는 노선의 이름이 존재합니다.");
        }
    }

    public void delete(final LineDto lineDto) {
        if (lineDao.delete(lineDto.getId()) == 0) {
            throw new NotFoundLineException("[ERROR] 해당노선이 존재하지 않습니다.");
        }
    }
}
