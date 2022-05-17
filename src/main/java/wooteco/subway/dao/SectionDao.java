package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Section save(Section section);

    void deleteById(Long id);

    Section findById(Long id);

    List<Section> findByLineId(Long id);

    void update(Section section);

    Section findByLineIdAndUpStationId(Long lineId, Long upStationId);

    Section findByLineIdAndDownStationId(Long lineId, Long downStationId);
}
