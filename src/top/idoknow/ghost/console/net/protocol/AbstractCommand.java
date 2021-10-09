package top.idoknow.ghost.console.net.protocol;

/**
 * A preset command to be stored and call in processor.
 * @author Rock Chin
 */
public abstract class AbstractCommand {
    /**
     * Process parsed data.
     * @param params params[0] is the index of this command
     * @param handler handler which this protocol of
     * @param rawData raw string data
     */
    public abstract void process(String[] params,AbstractHandler handler,String rawData)throws Exception;
}
