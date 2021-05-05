package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.LineDto;

public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineDto createLine(LineDto lineDto) {
        Line line = new Line(lineDto.getName(), lineDto.getColor());
        Line saveLine = lineDao.save(line);
        return new LineDto(saveLine.getId(), saveLine.getName(), saveLine.getColor());
    }

    public List<LineDto> findAll() {
        return lineDao.findAll()
            .stream()
            .map(it -> new LineDto(it.getId(), it.getName(), it.getColor()))
            .collect(Collectors.toList());
    }

    public LineDto findOne(long index) {
        Line line = lineDao.findOne(index);
        return new LineDto(line.getId(), line.getName(), line.getColor());
    }

    public void update(long index,LineDto lineDto) {
        Line line = new Line(lineDto.getName(), lineDto.getColor());
        lineDao.update((int) index, line);
    }

    public void delete(long index) {
        lineDao.delete(index);
    }
}
