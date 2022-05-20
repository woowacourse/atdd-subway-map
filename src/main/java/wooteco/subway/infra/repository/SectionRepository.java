package wooteco.subway.infra.repository;

import java.util.List;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public interface SectionRepository {

    void save(Long lineId, Section section);

    void save(Sections sections);

    List<Sections> findAll();

    Sections findByLineId(Long lineId);
}
