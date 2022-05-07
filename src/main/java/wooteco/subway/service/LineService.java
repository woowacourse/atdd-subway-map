package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService {

    private static final String LINE_NOT_FOUND_EXCEPTION_MESSAGE = "해당되는 노선은 존재하지 않습니다.";
    private static final String DUPLICATE_NAME_EXCEPTION_MESSAGE = "중복되는 이름의 지하철 노선이 존재합니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(this::toLineResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponse find(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND_EXCEPTION_MESSAGE));
        return toLineResponse(line);
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        validateUniqueName(lineRequest.getName());

        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return toLineResponse(lineDao.save(line));
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        validateExistingStation(id);
        validateUniqueName(lineRequest.getName());

        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line);
    }

    @Transactional
    public void delete(Long id) {
        validateExistingStation(id);
        lineDao.deleteById(id);
    }

    private void validateExistingStation(Long id) {
        boolean isExistingStation = lineDao.findById(id).isPresent();
        if (!isExistingStation) {
            throw new NotFoundException(LINE_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    private void validateUniqueName(String name) {
        boolean isDuplicateName = lineDao.findByName(name).isPresent();
        if (isDuplicateName) {
            throw new IllegalArgumentException(DUPLICATE_NAME_EXCEPTION_MESSAGE);
        }
    }

    private LineResponse toLineResponse(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }
}
