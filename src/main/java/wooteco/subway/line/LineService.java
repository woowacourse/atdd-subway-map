package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.LineDto;
import wooteco.subway.line.dto.LineIdDto;
import wooteco.subway.line.dto.NonIdLineDto;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineDto createLine(final NonIdLineDto nonIdLineDto) {
        checkExistedNameAndColor(nonIdLineDto);
        Line line = new Line(nonIdLineDto.getName(), nonIdLineDto.getColor());
        Line saveLine = lineDao.save(line);
        return new LineDto(saveLine.getId(), saveLine.getName(), saveLine.getColor());
    }

    private void checkExistedNameAndColor(NonIdLineDto nonIdlineDto) {
        String name = nonIdlineDto.getName();
        String color = nonIdlineDto.getColor();

        if (lineDao.countByColor(color) != 0) {
            throw new NotFoundLineException("[ERROR] 해당하는 노선의 색이 존재합니다.");
        }

        if (lineDao.countByName(name) != 0) {
            throw new NotFoundLineException("[ERROR] 해당하는 노선의 이름이 존재합니다.");
        }
    }

    public List<LineDto> findAll() {
        return lineDao.showAll()
            .stream()
            .map(it -> new LineDto(it.getId(), it.getName(), it.getColor()))
            .collect(Collectors.toList());
    }

    public LineDto findOne(final LineIdDto lineIdDto) {
        Line line = lineDao.show(lineIdDto.getId());
        return new LineDto(line.getId(), line.getName(), line.getColor());
    }

    public void update(final LineDto lineDto) {
        Line line = new Line(lineDto.getName(), lineDto.getColor());

        if (lineDao.update(lineDto.getId(), line) == 0) {
            throw new EmptyResultDataAccessException(0);
        }
    }

    public void delete(final LineDto lineDto) {
        if (lineDao.delete(lineDto.getId()) == 0) {
            throw new NotFoundLineException("[ERROR] 해당노선이 존재하지 않습니다.");
        }
    }
}
