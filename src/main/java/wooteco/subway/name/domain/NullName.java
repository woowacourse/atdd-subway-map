package wooteco.subway.name.domain;

public class NullName implements Name{
    public NullName() {
    }

    @Override
    public String name() {
        throw new IllegalStateException("저는 Null객체예요. 동작을 수행할 수 없어요");
    }

    @Override
    public boolean sameName(String name) {
        throw new IllegalStateException("저는 Null객체예요. 동작을 수행할 수 없어요");
    }

    @Override
    public Name changeName(String name) {
        throw new IllegalStateException("저는 Null객체예요. 동작을 수행할 수 없어요");
    }
}
