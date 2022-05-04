package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.LineDaoImpl;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.exception.ClientException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        validateDuplicateLine(lineRequest);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    private void validateDuplicateLine(LineRequest lineRequest) {
        Optional<Line> optional = lineDao.findAll()
                .stream()
                .filter(line -> line.getName().equals(lineRequest.getName()))
                .findAny();
        if (optional.isPresent()) {
            throw new ClientException("이미 등록된 지하철노선입니다.");
        }
    }

    public List<LineResponse> findLines() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineDao.find(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public int updateLine(Long id, LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return lineDao.update(id, line);
    }

    public int deleteLine(Long id) {
        return lineDao.delete(id);
    }
}
