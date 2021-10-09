package top.idoknow.ghost.console.at;

import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.protocol.MessageWrapper;
import top.idoknow.ghost.console.subject.Subject;

public class AtTaskHandler extends AbstractHandler implements IHasWrapper {

    MessageWrapper wrapper;
    public AtTaskHandler(){
        updateSubject(new Subject("atTaskHost",Subject.CONSOLE));
        wrapper=new MessageWrapper(this.getSubject());
    }
    @Override
    public void dispose() {
        return;
    }

    @Override
    public MessageWrapper getWrapper() {
        return this.wrapper;
    }
}
