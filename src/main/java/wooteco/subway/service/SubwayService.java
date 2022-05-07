package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class SubwayService {

    private LineDao lineDao;

    public SubwayService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine);
    }

    public List<LineResponse> getLines() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRequest.toEntity(id);
        lineDao.update(line);
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }
}
