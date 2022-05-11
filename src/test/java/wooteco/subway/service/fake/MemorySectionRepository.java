package wooteco.subway.service.fake;

import java.util.List;

import wooteco.subway.dao.repository.SectionRepository;
import wooteco.subway.domain.Section;

public class MemorySectionRepository implements SectionRepository {

	@Override
	public Long save(Long lineId, Section section) {
		return null;
	}

	@Override
	public Section findById(Long id) {
		return null;
	}

	@Override
	public List<Section> findByLineId(Long lineId) {
		return null;
	}

	@Override
	public void update(Section section) {

	}

	@Override
	public void remove(Long id) {

	}
}
