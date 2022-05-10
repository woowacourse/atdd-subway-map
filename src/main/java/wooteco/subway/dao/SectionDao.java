package wooteco.subway.dao;

public interface SectionDao<T> {
    T save(T Section);

    int deleteSection(Long lineId, Long stationId);
}
