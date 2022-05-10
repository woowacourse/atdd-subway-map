package wooteco.subway.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.RowDuplicatedException;
import wooteco.subway.exception.RowNotFoundException;

@Service
public class LineService {

    private final LineDao dao;

    public LineService(LineDao dao) {
        this.dao = dao;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDistinct(line);
        Line savedLine = saveOrThrow(line);
        return LineResponse.from(savedLine);
    }

    private Line saveOrThrow(Line line) {
        final Optional<Line> savedLine = dao.save(line);
        if (savedLine.isEmpty()) {
            throw new RowDuplicatedException("이미 존재하는 노선 이름입니다.");
        }
        return savedLine.get();
    }

    private void validateDistinct(Line line) {
        final boolean isMatch = findNameMatchingLine(line);
        if (isMatch) {
            throw new RowDuplicatedException("이미 존재하는 노선 이름입니다.");
        }
    }

    private boolean findNameMatchingLine(Line line) {
        return dao.findAll().stream().anyMatch(it -> Objects.equals(it.getName(), line.getName()));
    }

    public List<LineResponse> findAll() {
        return dao.findAll()
            .stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    public LineResponse findOne(Long id) {
        final Optional<Line> foundLine = dao.findById(id);
        return LineResponse.from(foundLine.orElseThrow(
            () -> new RowNotFoundException("조회하고자 하는 노선이 존재하지 않습니다.")
        ));
    }

    public void update(Long id, LineRequest lineRequest) {
        final boolean isUpdated = dao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
        if (!isUpdated) {
            throw new RowNotFoundException("수정하고자 하는 노선이 존재하지 않습니다.");
        }
    }

    public void delete(Long id) {
        final boolean isDeleted = dao.delete(id);
        if (!isDeleted) {
            throw new RowNotFoundException("삭제하고자 하는 노선이 존재하지 않습니다.");
        }
    }
}
