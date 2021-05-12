package wooteco.subway.line.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.line.LineDuplicatedNameException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.line.Line;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.line.dto.response.LineResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final Logger log = LoggerFactory.getLogger(LineService.class);

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public LineCreateResponse save(LineCreateRequest lineCreateRequest) {
        validatesNameDuplication(lineCreateRequest);
        Line line = new Line(lineCreateRequest.getName(), lineCreateRequest.getColor());
        Line newLine = lineDao.save(line);
        log.info("{} 노선 생성 성공", newLine.getName());
        return new LineCreateResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    // TODO exist 쿼리로 변경
    private void validatesNameDuplication(LineCreateRequest lineCreateRequest) {
        lineDao.findByName(lineCreateRequest.getName())
//                .orElseThrow(LineDuplicatedNameException::new);
                .ifPresent(l -> {
                    throw new LineDuplicatedNameException();
                });
    }

    public LineResponse findBy(Long id) {
        Line newLine = findById(id);
        log.info("{} 노선 조회 성공", newLine.getName());
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    private Line findById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(LineNotFoundException::new);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        log.info("지하철 모든 노선 조회 성공");
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = findById(id);

        validatesNameDuplicationExceptOriginalName(lineUpdateRequest, line);

        line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());

        lineDao.update(line);
        log.info("노선 정보 수정 완료");
    }

    private void validatesNameDuplicationExceptOriginalName(LineUpdateRequest lineUpdateRequest, Line line) {
        lineDao.findByNameAndNotInOriginalName(lineUpdateRequest.getName(), line.getName())
                .ifPresent(l -> {
                    throw new LineDuplicatedNameException();
                });
    }

    @Transactional
    public void delete(Long id) {
        lineDao.delete(id);
        log.info("노선 삭제 성공");
    }
}
