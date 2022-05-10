package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionRepository;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public void create(final Long lineId, final SectionRequest sectionRequest) {

    }
}
