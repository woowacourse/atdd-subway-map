package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse findLineInfos(Long id) {
        var line = lineDao.findById(id);
        return new LineResponse(line);
    }

    public List<LineResponse> findAll() {
        var allLines = lineDao.findAll();

        return allLines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse createLine(LineRequest lineRequest) {
        var line = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        return new LineResponse(line);
    }


    public void updateById(Long id, String name, String color) {
        lineDao.update(id, name, color);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
