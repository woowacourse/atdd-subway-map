package wooteco.subway.line.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;

import java.util.List;

@Repository
public class LineRepository {
    private final LineDao lineDao;
    private final SectionRepository sectionRepository;

    public LineRepository(LineDao lineDao, SectionRepository sectionRepository) {
        this.lineDao = lineDao;
        this.sectionRepository = sectionRepository;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("[ERROR] 존재하지 않는 노선입니다."));
        line.initSections(sectionRepository.findAllByLineId(id));
        return line;
    }
}
