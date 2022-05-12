package wooteco.subway.dao.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.table.SectionTable;
import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionRepository implements SectionRepository {

	private final SectionDao sectionDao;
	private final StationDao stationDao;

	public JdbcSectionRepository(SectionDao sectionDao, StationDao stationDao) {
		this.sectionDao = sectionDao;
		this.stationDao = stationDao;
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

	@Override
	public boolean existByStation(Long stationId) {
		return sectionDao.existByStationId(stationId);
	}
}
