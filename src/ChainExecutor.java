
public abstract class ChainExecutor <T extends Context> {

    protected abstract Handler<T> getStart();

    public void execute(T t) {
        getStart().execute(t);
    }
}
