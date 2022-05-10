package wooteco.subway.service;

import java.util.List;
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
        validateDistinct(lineRequest.getName());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line savedLine = dao.save(line);
        return LineResponse.from(savedLine);
    }

    private void validateDistinct(String name) {
        Optional<Line> line = dao.findByName(name);
        if (line.isPresent()) {
            throw new RowDuplicatedException("이미 존재하는 노선 이름입니다.");
        }
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
