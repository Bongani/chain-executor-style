/**
 * Super calls for all handlers that get executed as a chain
 *
 * @param <T> the context object
 */
public abstract class Handler<T> {

    private Handler<T> next;

    protected abstract void process(T context);

    protected Handler<T> getFailureHandler() {
        return null;
    }

    final void execute(T context) {
        try {
            if (shouldExecute(context)) {
                setCurrentHandlerName(context);
                process(context);
                resetCurrentHandlerName(context);
            }

            if (shouldExecuteNext(context)) {
                executeNext(context);
            }
        } catch (Exception e) {
            setExceptionMessage(e, context);
            Handler<T> failureHandler = getFailureHandler();
            if (failureHandler != null) {
                failureHandler.execute(context);
            } else {
                throw e;
            }
        }

    }

    private void executeNext(T context) {
        if (next != null) {
            next.execute(context);
        }
    }

    @SuppressWarnings("unchecked")
    public Handler setNext(Handler<T> next) {
        this.next = next;
        return next;
    }

    /**
     * If false won't execute current handler, and will jump to the next handler in the chain
     *
     * @param context
     * @return
     */
    protected Boolean shouldExecute(T context) {
        return true;
    }

    /**
     * If false wont execute next handler and proceeding handlers in the chain
     *
     * @param context
     * @return
     */
    protected Boolean shouldExecuteNext(T context) {
        return true;
    }

    /**
     * This is to set the current handlers name on the context so that on retry it can be used to skip to the correct handler.
     * The method is left empty that the other handler not using this does not need to implement it.
     *
     * @param context The context object.
     */
    protected void setCurrentHandlerName(T context) {
    }

    /**
     * This is to reset the current handlers name on the context so that the flow continues normally.
     * The method is left empty that the other handler not using this does not need to implement it.
     *
     * @param context The context object.
     */
    protected void resetCurrentHandlerName(T context) {
    }

    /**
     * On failure of the handler this method is called to set the Exception message on the context object.
     * The method is left empty that the other handlers not using this does not need to implement it.
     *
     * @param e The {@code Exception} thrown by the handler.
     * @param context The context object.
     */
    protected void setExceptionMessage(Exception e, T context) {
    }
}
