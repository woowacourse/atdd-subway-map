package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.entity.LineEntity;

@Service
public class LineService {

    private static final String DUPLICATE_NAME_ERROR = "이미 같은 이름의 노선이 존재합니다.";
    private static final String NOT_EXIST_ERROR = "해당 노선이 존재하지 않습니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        LineEntity lineEntity = lineRequest.toLineEntity();
        Optional<LineEntity> wrappedLineEntity = lineDao.findByName(lineRequest.getName());
        if (wrappedLineEntity.isPresent()) {
            throw new DuplicateKeyException(DUPLICATE_NAME_ERROR);
        }
        LineEntity savedLineEntity = lineDao.save(lineEntity);
        Line savedLine = new Line(savedLineEntity.getId(), savedLineEntity.getName(), savedLineEntity.getColor());
        return LineResponse.of(savedLine);
    }

    public List<LineResponse> findAllLines() {
        List<LineEntity> allLineEntities = lineDao.findAll();
        return allLineEntities.stream()
                .map(lineEntity -> new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor()))
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Optional<LineEntity> wrappedLineEntity = lineDao.findById(id);
        checkLineExist(wrappedLineEntity);
        LineEntity lineEntity = wrappedLineEntity.get();
        Line line = new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
        return LineResponse.of(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        checkLineExist(lineDao.findById(id));
        LineEntity lineEntity = lineRequest.toLineEntity();
        lineDao.update(id, lineEntity);
    }

    public void deleteLine(Long id) {
        checkLineExist(lineDao.findById(id));
        lineDao.deleteById(id);
    }

    private void checkLineExist(Optional<LineEntity> wrappedLine) {
        if (wrappedLine.isEmpty()) {
            throw new NoSuchElementException(NOT_EXIST_ERROR);
        }
    }
}
