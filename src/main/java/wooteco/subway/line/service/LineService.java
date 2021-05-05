package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dao.dto.LineDto;
import wooteco.subway.line.domain.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineDto save(LineDto linedto) {
        Line line = new Line(linedto.getName(), linedto.getColor());
        return LineDto.from(lineDao.save(line));
    }

    public List<LineDto> findAll() {
        return lineDao.findAll().stream()
                .map(LineDto::from)
                .collect(Collectors.toList());
    }

    public LineDto findById(Long id) {
        return LineDto.from(lineDao.findById(id));
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }

    public void update(LineDto lineDto) {
        Line line = new Line(lineDto.getId(), lineDto.getName(), lineDto.getColor(), new ArrayList<>());
        lineDao.update(line);
    }
}
