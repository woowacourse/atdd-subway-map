package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.SectionDto;

@Repository
public class LineRepository {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineRepository(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Long save(Line line) {
        LineDto savedLine = lineDao.save(LineDto.from(line));
        List<SectionDto> sectionDtos = line.getSections().stream()
                .map(section -> SectionDto.of(section, savedLine.getId()))
                .collect(Collectors.toList());
        sectionDao.saveAll(sectionDtos);
        return savedLine.getId();
    }
}
