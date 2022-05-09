package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
class SectionDaoTest {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	private DataSource dataSource;
	private SectionDao sectionDao;
	private Station upStation;
	private Station downStation;

	@BeforeEach
	void init() {
		StationDao stationDao = new JdbcStationDao(dataSource, jdbcTemplate);
		sectionDao = new JdbcSectionDao(dataSource, jdbcTemplate, stationDao);
		Long upStationId = stationDao.save(new Station("강남역"));
		Long downStationId = stationDao.save(new Station("역삼역"));
		upStation = new Station(upStationId, "강남역");
		downStation = new Station(downStationId, "역삼역");
	}

	@DisplayName("지하철 구간을 저장한다.")
	@Test
	void save() {
		Section section = new Section(upStation, downStation, 10);
		Long sectionId = sectionDao.save(1L, section);
		assertThat(sectionId).isGreaterThan(0);
	}

	@DisplayName("id로 지하철 구간을 조회한다.")
	@Test
	void findById() {
		Section section = new Section(upStation, downStation, 10);
		Long sectionId = sectionDao.save(1L, section);

		Section foundSection = sectionDao.findById(sectionId);
		assertThat(foundSection.getId()).isEqualTo(sectionId);
		assertThat(foundSection.getUpStationId()).isEqualTo(upStation.getId());
		assertThat(foundSection.getDownStationId()).isEqualTo(downStation.getId());
	}

	@DisplayName("지하철 노선 id로 구간을 조회한다.")
	@Test
	void findByLineId() {
		Section section = new Section(upStation, downStation, 10);
		Long sectionId = sectionDao.save(1L, section);

		List<Section> sections = sectionDao.findByLineId(1L);
		Section foundSection = sections.get(0);
		assertThat(foundSection.getId()).isEqualTo(sectionId);
		assertThat(foundSection.getUpStationId()).isEqualTo(upStation.getId());
		assertThat(foundSection.getDownStationId()).isEqualTo(downStation.getId());
	}

	@DisplayName("구간을 수정한다.")
	@Test
	void updateSection() {
		Section section = new Section(upStation, downStation, 10);
		Long sectionId = sectionDao.save(1L, section);

		Section updatedSection = new Section(sectionId, downStation, upStation, 7);
		sectionDao.update(updatedSection);

		Section findSection = sectionDao.findById(sectionId);
		assertThat(findSection).isEqualTo(updatedSection);
	}

	@DisplayName("구간을 삭제한다.")
	@Test
	void remove() {
		Section section = new Section(upStation, downStation, 10);
		Long sectionId = sectionDao.save(1L, section);
		sectionDao.remove(sectionId);

		assertThat(sectionDao.findByLineId(1L)).isEmpty();
	}
}