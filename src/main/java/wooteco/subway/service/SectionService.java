package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private static final String NOT_UPDATE_MESSAGE = "업데이트 되지 않았습니다.";

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
        Optional<Section> connectedPoint = sections.addSection(inputSection);

        Long id = jdbcSectionDao.save(inputSection);
        if (connectedPoint.isPresent()) {
            update(lineId, connectedPoint.get());
        }
        return new Section(id, lineId, upStationId, downStationId, distance);
    }

    public List<Section> getSectionsByLineId(long lineId) {
        return jdbcSectionDao.findSectionsByLineId(lineId);
    }

    private void update(Long lineId, Section section) {
        boolean isUpdated = jdbcSectionDao.update(lineId, section);
        if (!isUpdated) {
            throw new IllegalArgumentException(NOT_UPDATE_MESSAGE);
        }
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(getSectionsByLineId(lineId));
        Optional<Section> section = sections.deleteSection(stationId);
        if (section.isPresent()) {
            jdbcSectionDao.save(section.get());
        }
        jdbcSectionDao.delete(stationId, lineId);
    }
}
