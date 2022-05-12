package wooteco.subway.dao.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.table.LineTable;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineRepository implements LineRepository {

	private final LineDao lineDao;
	private final SectionRepository sectionRepository;

	public JdbcLineRepository(LineDao lineDao, SectionRepository sectionRepository) {
		this.lineDao = lineDao;
		this.sectionRepository = sectionRepository;
	}

	@Override
	public Long save(Line line) {
		long lineId = lineDao.save(LineTable.from(line));
		line.getSections().forEach(section -> sectionRepository.save(lineId, section));
		return lineId;
	}

	@Override
	public List<Line> findAll() {
		return lineDao.findAll().stream()
			.map(LineTable::toEntity)
			.map(line -> line.createWithSection(sectionRepository.findByLineId(line.getId())))
			.collect(Collectors.toList());
	}

	@Override
	public Line findById(Long id) {
		return lineDao.findById(id)
			.toEntity()
			.createWithSection(sectionRepository.findByLineId(id));
	}

	@Override
	public void update(Line line) {
		lineDao.update(LineTable.from(line));
	}

	@Override
	public void remove(Long id) {
		lineDao.remove(id);
	}

	@Override
	public Boolean existsByName(String name) {
		return lineDao.existsByName(name);
	}
}
