package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;

@Service
public class LineService {

    private static final String DUPLICATE_NAME_ERROR = "이미 같은 이름의 노선이 존재합니다.";
    private static final String NOT_EXIST_ERROR = "해당 노선이 존재하지 않습니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Optional<Line> wrappedStation = lineDao.findByName(lineRequest.getName());
        if (wrappedStation.isPresent()) {
            throw new DuplicateKeyException(DUPLICATE_NAME_ERROR);
        }
        Line savedLine = lineDao.save(line);
        return LineResponse.of(savedLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> allLines = lineDao.findAll();
        return allLines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Optional<Line> wrappedLine = lineDao.findById(id);
        checkLineExist(wrappedLine);
        return LineResponse.of(wrappedLine.get());
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        checkLineExist(lineDao.findById(id));
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(id, newLine);
    }

    public void deleteLine(Long id){
        checkLineExist(lineDao.findById(id));
        lineDao.deleteById(id);
    }

    private void checkLineExist(Optional<Line> wrappedLine) {
        if (wrappedLine.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_ERROR);
        }
    }
}
