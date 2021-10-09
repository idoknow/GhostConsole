package top.idoknow.ghost.console.net.terminal.operations;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;

/**
 * Login to an account with token
 * @author Rock Chin
 */
public class CommandLogin extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        //TODO authorize account name and password.
    }
}
