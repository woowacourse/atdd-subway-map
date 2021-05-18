package wooteco.subway.line.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineDao;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private LineDao lineDao;

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(namedParameterJdbcTemplate);
        sectionDao = new SectionDao(namedParameterJdbcTemplate);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void save() {
        final Line createdLine = lineDao.save(new Line("2호선", "black"));
        final Section section = Section.Builder()
            .lineId(createdLine.getId())
            .upStationId(2L)
            .downStationId(4L)
            .distance(10)
            .build();
        final Section createdSection = sectionDao.save(section);

        assertThat(createdSection)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(section);
    }

    @DisplayName("동일한 노선의 전체 구간을 조회한다.")
    @Test
    void findByLineId() {
        final Line createdLine = lineDao.save(new Line("2호선", "black"));
        final List<Section> sections = new ArrayList<>(Arrays.asList(
            Section.Builder().lineId(createdLine.getId()).upStationId(2L).downStationId(4L).distance(10).build(),
            Section.Builder().lineId(createdLine.getId()).upStationId(4L).downStationId(6L).distance(10).build()
        ));
        sections.forEach(section -> sectionDao.save(section));

        assertThat(sectionDao.findByLineId(createdLine.getId()))
            .usingElementComparatorIgnoringFields("id")
            .isEqualTo(sections);
    }

    @DisplayName("특정 이름의 구간을 조회한다.")
    @Test
    void findByName() {
        final Line createdLine = lineDao.save(new Line("2호선", "black"));
        final Section section = Section.Builder()
            .lineId(createdLine.getId())
            .upStationId(2L)
            .downStationId(4L)
            .distance(10)
            .build();
        final Section createdSection = sectionDao.save(section);

        assertThat(sectionDao.findById(createdSection.getId()).get())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(section);
    }

    @DisplayName("특정 구간을 수정한다.")
    @Test
    void update() {
        final Line createdLine = lineDao.save(new Line("2호선", "black"));
        final Section section = Section.Builder()
            .lineId(createdLine.getId())
            .upStationId(2L)
            .downStationId(4L)
            .distance(10)
            .build();
        final Section createdSection = sectionDao.save(section);

        final Section updatedSection = Section.Builder()
            .id(createdSection.getId())
            .lineId(createdLine.getId())
            .upStationId(4L)
            .downStationId(6L)
            .distance(20)
            .build();
        sectionDao.update(updatedSection);

        assertThat(sectionDao.findById(createdSection.getId()).get()).isEqualTo(updatedSection);
    }

    @DisplayName("존재하지 않는 구간의 Id로 구간을 수정하면 예외가 발생한다.")
    @Test
    void updateWithAbsentId() {
        final Line createdLine = lineDao.save(new Line("2호선", "black"));
        final Long id = getAbsentId(createdLine.getId());
        final Section section = Section.Builder()
            .id(id).lineId(createdLine.getId()).upStationId(1L).downStationId(2L).build();
        assertThatThrownBy(() -> sectionDao.update(section))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessage("해당 Id의 구간이 없습니다.");
    }

    private Long getAbsentId(final Long lineId) {
        final Section section =
            Section.Builder().lineId(lineId).upStationId(2L).downStationId(4L).distance(10).build();
        final Section createdSection = sectionDao.save(section);

        sectionDao.deleteById(createdSection.getId());
        return createdSection.getId();
    }

    @DisplayName("특정 구간을 삭제한다.")
    @Test
    void delete() {
        final Line createdLine = lineDao.save(new Line("2호선", "black"));
        final Section section = Section.Builder()
            .lineId(createdLine.getId())
            .upStationId(2L)
            .downStationId(4L)
            .distance(10)
            .build();
        final Section createdSection = sectionDao.save(section);

        sectionDao.deleteById(createdSection.getId());
        assertThat(sectionDao.findById(createdSection.getId())).isEqualTo(Optional.empty());
    }

    @DisplayName("존재하지 않는 구간의 Id로 구간을 제거하면 예외가 발생한다.")
    @Test
    void deleteWithAbsentId() {
        final Line createdLine = lineDao.save(new Line("2호선", "black"));
        assertThatThrownBy(() -> sectionDao.deleteById(getAbsentId(createdLine.getId())))
            .hasMessage("해당 Id의 구간이 없습니다.")
            .isInstanceOf(DataNotFoundException.class);
    }
}