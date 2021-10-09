package top.idoknow.ghost.console.net.slave;

import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.AbstractProcessor;
import top.idoknow.ghost.console.net.slave.operations.*;

/**
 * Process data from slave
 * @author Rock Chin
 */
public class SlaveProcessor extends AbstractProcessor {
    public SlaveProcessor(AbstractHandler handler) {
        super(handler);

        register("!alive",new CommandAlive());
        register("!alives",new CommandAlives());
        register("!finish",new CommandFinish());
        register("!info",new CommandInfo());
        register("!name",new CommandName());
        register("!sendpicurl",new CommandSendPicURL());
        register("!startup",new CommandStartup());
        register("!version",new CommandVersion());
    }

    @Override
    public String[] parse(String data) throws Exception {
        return data.split(" ");
    }
}
