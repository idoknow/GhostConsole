package top.idoknow.ghost.console.net.protocol;

import top.idoknow.ghost.console.subject.Subject;

import java.net.Socket;

/**
 * Defines a standard handler.
 * @author Rock Chin
 */
public abstract class AbstractHandler extends Thread{
    protected Subject subject;
    public Subject getSubject(){
        return subject;
    }
    public void updateSubject(Subject subject){
        this.subject=subject;
    }

    protected Socket socket=null;
    public Socket getSocket(){
        return this.socket;
    }

    //processor of this handler
    private AbstractProcessor processor;

    /**
     * Bind a processor implement for this handler
     * @param processor a implement of AbstractProcessor
     */
    public void bindProcessor(AbstractProcessor processor){
        this.processor=processor;
    }

    public AbstractProcessor getProcessor(){
        return processor;
    }



    protected DataProxy dataProxy;
    public DataProxy getDataProxy(){
        return dataProxy;
    }

    public abstract void dispose();
}
