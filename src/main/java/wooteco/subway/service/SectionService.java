package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final JdbcSectionDao jdbcSectionDao;

    public SectionService(JdbcSectionDao jdbcSectionDao) {
        this.jdbcSectionDao = jdbcSectionDao;
    }

    public Section save(Long lineId, SectionRequest sectionRequest) {
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();
        Section inputSection = new Section(lineId, upStationId, downStationId, distance);

        Sections sections = new Sections(getSectionsByLineId(lineId));
        Section connectedPoint = sections.addSection(inputSection);

        Long id = jdbcSectionDao.save(inputSection);

        if (connectedPoint != null) {
            update(lineId, connectedPoint);
        }
        return new Section(id, lineId, upStationId, downStationId, distance);
    }

    public List<Section> getSectionsByLineId(long lineId) {
        return jdbcSectionDao.findSectionsByLineId(lineId);
    }

    private void update(Long lineId, Section section) {
        jdbcSectionDao.update(lineId, section);
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(getSectionsByLineId(lineId));
        if (sections.getSections().size() == 1) {
            throw new IllegalArgumentException();
        }
        Section section = sections.deleteSection(stationId);
        if (section != null) {
            jdbcSectionDao.save(section);
        }
        jdbcSectionDao.delete(stationId, lineId);
    }
}
