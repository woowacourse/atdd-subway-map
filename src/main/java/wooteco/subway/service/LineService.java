package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private static final String DUPLICATED_NAME_ERROR_MESSAGE = "중복된 이름이 존재합니다.";
    private static final String NONE_LINE_ERROR_MESSAGE = "해당 ID의 노선은 존재하지 않습니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse save(LineCreateRequest lineCreateRequest) {
        validDuplicatedName(lineCreateRequest.getName());
        Line line = new Line(lineCreateRequest.getName(), lineCreateRequest.getColor());
        Long id = lineDao.save(line);
        return new LineResponse(id, line.getName(), line.getColor(), new ArrayList<>());
    }

    public void update(Long id, LineRequest lineRequest) {
        validDuplicatedName(lineRequest.getName());
        lineDao.update(id, lineRequest);
    }

    private void validDuplicatedName(String name) {
        if (lineDao.countByName(name) > 0) {
            throw new IllegalArgumentException(DUPLICATED_NAME_ERROR_MESSAGE);
        }
    }

    public LineResponse findById(Long id) {
        try {
            Line line = lineDao.findById(id);
            return LineResponse.from(line);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException(NONE_LINE_ERROR_MESSAGE);
        }
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
