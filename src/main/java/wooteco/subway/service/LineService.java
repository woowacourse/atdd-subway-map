package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private final JdbcLineDao jdbcLineDao;

    public LineService(JdbcLineDao jdbcLineDao) {
        this.jdbcLineDao = jdbcLineDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        Long id = jdbcLineDao.save(name, color);
        return new LineResponse(id, name, color);
    }

    public List<LineResponse> getLines() {
        List<Line> lines = jdbcLineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = jdbcLineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        jdbcLineDao.updateById(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        jdbcLineDao.deleteById(id);
    }
}
