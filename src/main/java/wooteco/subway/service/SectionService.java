package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class SectionService {

    private final StationService stationService;
    private final JdbcSectionDao jdbcSectionDao;

    public SectionService(StationService stationService, JdbcSectionDao jdbcSectionDao) {
        this.stationService = stationService;
        this.jdbcSectionDao = jdbcSectionDao;
    }

    public Long createSection(SectionRequest sectionRequest, Long lineId) {
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();

        if (jdbcSectionDao.isExistByUpStationIdAndDownStationId(upStationId, downStationId)) {
            throw new IllegalArgumentException("이미 존재하기 때문에 구간을 등록할 수 없습니다.");
        }

        checkAddingBranch(upStationId, downStationId, distance, lineId);
        return saveSection(sectionRequest, lineId);
    }

    private void checkAddingBranch(Long upStationId, Long downStationId, int distance, Long lineId) {
        Sections sections = jdbcSectionDao.findByLineIdAndStationIds(lineId, upStationId, downStationId);
        if (sections.isBlack()) {
            throw new IllegalArgumentException("연결된 역이 없기 때문에 구간을 등록할 수 없습니다.");
        }

        Section section = sections.getSectionForCombine(upStationId, downStationId);
        if (section.isSameAsDownStation(downStationId) || section.isSameAsUpStation(upStationId)) {
            addBranch(section, upStationId, downStationId, distance, lineId);
        }
    }

    private void addBranch(Section section, Long upStationId, Long downStationId, int distance, Long lineId) {
        validateDistance(section, distance);

        if (section.isSameAsDownStation(downStationId)) {
            jdbcSectionDao.updateDownStationIdByLineIdAndUpStationId(lineId, section.getUpStationId(), upStationId);
        }
        if (section.isSameAsUpStation(upStationId)) {
            jdbcSectionDao.updateUpStationIdByLineIdAndDownStationId(lineId, section.getDownStationId(), downStationId);
        }
    }

    private void validateDistance(Section section, int distance) {
        if (!section.isPossibleDistance(distance)) {
            throw new IllegalArgumentException("거리문제로 구간을 등록할 수 없습니다.");
        }
    }

    public Long saveSection(SectionRequest sectionRequest, Long lineId) {
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();

        Section newSection = new Section(lineId, upStationId, downStationId, distance);
        return jdbcSectionDao.save(newSection);
    }

    public List<StationResponse> getStationsByLineId(Long lineId) {
        return jdbcSectionDao.findByLineId(lineId)
                .getStationIds()
                .stream()
                .map(stationService::getStation)
                .collect(Collectors.toUnmodifiableList());
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        Sections sections = jdbcSectionDao.findByLineIdAndStationId(lineId, stationId);
        sections.validateLengthToDeletion();
        Section upStationSection = sections.getSectionStationIdEqualsUpStationId(stationId);
        Section downStationSection = sections.getSectionStationIdEqualsDownStationId(stationId);
        jdbcSectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
        jdbcSectionDao.updateDownStationIdByLineIdAndUpStationId(lineId, downStationSection.getUpStationId(),
                upStationSection.getDownStationId());

        return true;
    }
}
