package wooteco.subway.section.domain;

public interface SectionRepository {

    Section save(final Section section);

    Sections findAllByLineId(final Long lineId);

    void update(final Section section);

    void delete(final Long id);

    void deleteByStationId(Long stationId);

    boolean existingByStationId(Long stationId);
}
