package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public long save(final LineRequest lineRequest) {
        Line line = convertLine(lineRequest);
        validateLine(line);
        return lineDao.save(line);
    }

    public List<LineResponse> findAll() {
        return convertLineResponses(lineDao.findAll());
    }

    public LineResponse find(final Long id) {
        return lineDao.find(id)
                .map(LineResponse::of)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선입니다."));
    }

    @Transactional
    public void update(final long id, final LineRequest lineRequest) {
        Line line = convertLine(lineRequest);
        validateLine(line);
        validateExistedLine(id);
        lineDao.update(id, line);
    }

    @Transactional
    public void delete(final Long id) {
        validateExistedLine(id);
        lineDao.delete(id);
    }

    private void validateLine(final Line line) {
        validateName(line);
        validateColor(line);
    }

    private void validateName(final Line line) {
        if (lineDao.existLineByName(line.getName())) {
            throw new IllegalArgumentException("지하철 노선 이름이 중복됩니다.");
        }
    }

    private void validateColor(final Line line) {
        if (lineDao.existLineByColor(line.getColor())) {
            throw new IllegalArgumentException("지하철 노선 색상이 중복됩니다.");
        }
    }

    private void validateExistedLine(final Long id) {
        if (!lineDao.existLineById(id)) {
            throw new IllegalArgumentException("존재하지 않는 지하철 노선입니다.");
        }
    }

    private Line convertLine(final LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor());
    }

    private List<LineResponse> convertLineResponses(final List<Line> lines) {
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }
}
