package wooteco.subway.dao;

public interface UpdateDao<T> {

    Long update(T entity);

    Long save(T entity);

    Long delete(Long id);
}
