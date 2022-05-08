package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineDto;
import wooteco.subway.repository.LineRepository;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final LineDao lineDao;

    public LineService(LineRepository lineRepository, LineDao lineDao) {
        this.lineRepository = lineRepository;
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        try {
            LineDto lineDto = lineDao.save(LineDto.from(line));
            return new Line(lineDto.getId(), lineDto.getName(), lineDto.getColor());
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 노선 이름입니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll().stream()
                .map(lineDto -> new Line(lineDto.getId(), lineDto.getName(), lineDto.getColor()))
                .collect(Collectors.toList());
    }

    public Line findById(Long id) {
        try {
            return lineRepository.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EmptyResultDataAccessException("존재하지 않는 노선입니다", 1);
        }
    }

    public Line update(Long id, Line line) {
        try {
            LineDto lineDto = lineDao.update(id, LineDto.from(line));
            return new Line(lineDto.getId(), line.getName(), line.getColor());
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 노선 이름입니다.");
        }
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
