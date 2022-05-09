package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Section;

public interface SectionDao {
	Long save(Long lineId, Section section);

	Section findById(Long id);

	List<Section> findByLineId(Long lineId);

	void update(Section section);

	void remove(Long id);
}
