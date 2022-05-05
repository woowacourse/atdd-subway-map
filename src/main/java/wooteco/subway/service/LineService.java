package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.BadRequestException;
import wooteco.subway.exception.NotFoundException;


@Service
@Transactional(readOnly = true)
public class LineService {

    private final JdbcLineDao jdbcLineDao;

    public LineService(JdbcLineDao jdbcLineDao) {
        this.jdbcLineDao = jdbcLineDao;
    }

    @Transactional
    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDuplicateNameAndColor(line.getName(), line.getColor());
        return LineResponse.from(jdbcLineDao.save(line));
    }

    public LineResponse showById(Long id) {
        return LineResponse.from(jdbcLineDao.findById(id)
            .orElseThrow(() -> new NotFoundException("조회하려는 id가 존재하지 않습니다.")));
    }

    public List<LineResponse> showAll() {
        return jdbcLineDao.findAll().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void updateById(Long id, String name, String color) {
        validateDuplicateNameAndColor(name, color);
        jdbcLineDao.modifyById(id, new Line(name, color));
    }

    @Transactional
    public void removeById(Long id) {
        jdbcLineDao.deleteById(id);
    }

    private void validateDuplicateNameAndColor(String name, String color) {
        if (jdbcLineDao.existByNameAndColor(name, color)) {
            throw new BadRequestException("노선이 이름과 색상은 중복될 수 없습니다.");
        }
    }
}
