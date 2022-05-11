package wooteco.subway.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionRepository implements SectionRepository {

	private final StationDao stationDao;
	private final SectionDao sectionDao;

	public JdbcSectionRepository(StationDao stationDao, SectionDao sectionDao) {
		this.stationDao = stationDao;
		this.sectionDao = sectionDao;
	}

	@Override
	public Long save(Long lineId, Section section) {
		return sectionDao.save(SectionTable.of(lineId, section));
	}

	@Override
	public Section findById(Long id) {
		SectionTable sectionTable = sectionDao.findById(id);
		return sectionTable.toEntity(
			stationDao.findById(sectionTable.getUpStationId()),
			stationDao.findById(sectionTable.getDownStationId())
		);
	}

	@Override
	public List<Section> findByLineId(Long lineId) {
		return sectionDao.findByLineId(lineId)
			.stream()
			.map(table -> table.toEntity(
				stationDao.findById(table.getUpStationId()),
				stationDao.findById(table.getDownStationId())
			)).collect(Collectors.toList());
	}

	@Override
	public void update(Section section) {
		sectionDao.update(SectionTable.from(section));
	}

	@Override
	public void remove(Long id) {
		sectionDao.remove(id);
	}
}
