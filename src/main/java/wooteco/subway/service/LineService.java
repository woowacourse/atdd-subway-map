package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SimpleLineRequest;
import wooteco.subway.dto.SimpleLineResponse;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public SimpleLineResponse create(LineRequest lineRequest) {
        Line line = lineDao.save(lineRequest);
        sectionDao.save(lineRequest, line.getId());
        return new SimpleLineResponse(line);
    }

    public List<SimpleLineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(SimpleLineResponse::new)
                .collect(Collectors.toList());
    }

    public SimpleLineResponse findById(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다."));
        return new SimpleLineResponse(line);
    }

    public void update(Long id, SimpleLineRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
    }
}
