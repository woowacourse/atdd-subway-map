package wooteco.subway.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionJdbcDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.ClientException;

@Service
public class SectionService {

    private final SectionJdbcDao sectionJdbcDao;

    public SectionService(SectionJdbcDao sectionJdbcDao) {
        this.sectionJdbcDao = sectionJdbcDao;
    }

    public void saveSection(Long id, SectionRequest sectionRequest, Sections sections, LineResponse line) {
        validateSameStation(sectionRequest);

        if (sections.isExistSection()) {
            sections.validateSaveCondition(sectionRequest, line);
            if (sections.isAddSectionMiddle(sectionRequest)) {
                addSectionMiddle(id, sectionRequest, sections);
                return;
            }
        }
        sectionJdbcDao.save(id, new Section(0L, id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance()));
    }

    private void validateSameStation(SectionRequest sectionRequest) {
        if (Objects.equals(sectionRequest.getDownStationId(), sectionRequest.getUpStationId())) {
            throw new ClientException("상행역과 하행역이 같을 수 없습니다.");
        }
    }

    private void addSectionMiddle(Long id, SectionRequest sectionRequest, Sections sections) {
        for (Section section : sections.getSections()) {
            if (section.isSameUpStationId(sectionRequest)) {
                sectionJdbcDao.save(id, section.createBySameUpStationId(id, sectionRequest));
                sectionJdbcDao.delete(id, section);

                section.updateSameUpStationId(sectionRequest);
                sectionJdbcDao.save(id, section);
                return;
            }

            if (section.isSameDownStationId(sectionRequest)) {
                sectionJdbcDao.save(id, section.createBySameDownStationId(id, sectionRequest));
                sectionJdbcDao.delete(id, section);

                section.updateSameDownStationId(sectionRequest);
                sectionJdbcDao.save(id, section);
                return;
            }
        }
    }
}
