package wooteco.subway.service.section;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.dto.request.section.SectionCreateRequestDto;
import wooteco.subway.controller.dto.response.section.SectionCreateResponseDto;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionCreate;

@Service
public class SectionCreateService {
    private final SectionDao sectionDao;

    public SectionCreateService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public SectionCreateResponseDto createSection(Long lineId, SectionCreateRequestDto sectionCreateRequestDto) {
        List<Section> allSectionsOfLine = sectionDao.findAllByLineId(lineId);
        Section newSection = new Section(lineId, sectionCreateRequestDto.getUpStationId(), sectionCreateRequestDto.getDownStationId(), sectionCreateRequestDto.getDistance());
        SectionCreate sectionCreate = new SectionCreate(newSection, allSectionsOfLine);
        if (sectionCreate.isConditionOfFirstOrLastInsert()) {
            Section savedSection = sectionDao.save(newSection);
            return new SectionCreateResponseDto(savedSection);
        }
        Section savedNewSection = insertNewSectionToMiddleOfSectionAndGet(sectionCreate);
        return new SectionCreateResponseDto(savedNewSection);
    }

    private Section insertNewSectionToMiddleOfSectionAndGet(SectionCreate sectionCreate) {
        Section newSplitSection = sectionCreate.getNewSectionWhenInsertNewSectionToMiddleOfSection();
        Long oldSplitSectionId = sectionCreate.getOldSplitSectionId();
        Section newSection = sectionCreate.getNewSection();
        sectionDao.deleteById(oldSplitSectionId);
        sectionDao.save(newSplitSection);
        return sectionDao.save(newSection);
    }
}
