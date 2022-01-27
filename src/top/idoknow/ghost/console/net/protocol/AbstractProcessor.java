package top.idoknow.ghost.console.net.protocol;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.subject.Subject;
import top.idoknow.ghost.console.util.Debug;

import java.util.HashMap;

/**
 * Defines processor for net data processing.
 * Handler calls process method provided by AbstractProcessor after receiving a section of data.
 * @author Rock Chin
 */
public abstract class AbstractProcessor {
    public static class CommandNotFoundException extends Exception{
        public CommandNotFoundException(String message){
            super(message);
        }
    }

    private final Subject absProcessorSubject=new Subject("AbstractProcessor",Subject.CONSOLE);

    private final AbstractHandler handler;
    public AbstractProcessor(AbstractHandler handler){
        this.handler=handler;
    }
    //stores preset commands
    private final HashMap<String,AbstractCommand> commands=new HashMap<>();
    private AbstractCommand defaultCommand=null;
    /**
     * Register a command.
     * @param name index of this command
     * @param command an implement of AbstractCommand
     */
    public void register(String name,AbstractCommand command){
        commands.put(name,command);
    }

    public void defaultCommand(AbstractCommand command){
        this.defaultCommand=command;
    }

    public HashMap<String,AbstractCommand> getCommandsCopy(){
        return (HashMap<String, AbstractCommand>) commands.clone();
    }
    /**
     * Parse income data.
     * @param data data received by handler
     * @return data sections parsed and split by parse method
     *         ,the index 0 of this array will be the key to find specific command.
     */
    protected abstract String[] parse(String data)throws Exception;

    /**
     * Wait for a section of data to be processed
     * @param data data section
     * @throws Exception any occurred exception while processing
     */
    public void run(String data)throws Exception{
        String[] spt=parse(data);
        AbstractCommand target;
//        Debug.debug(absProcessorSubject,"looking for:"+spt[0]);
        if (commands.containsKey(spt[0])){
            target=commands.get(spt[0]);
        }else if (defaultCommand!=null){
            target=defaultCommand;
        }else {
            throw new CommandNotFoundException("No default command and no such command index:"+spt[0]);
        }
        target.process(spt,handler,data);
    }

    /**
     * Process data in a new thread
     * @param data data section
     * @return handle of the thread
     * @throws Exception any occurred exception while creating a new thread
     */
    public Thread start(String data)throws Exception{
        Thread temp=new Thread(()->{
            try {
                run(data);
            }catch (Exception e){
                e.printStackTrace();
                LogMgr.log(LogMgr.ERROR,handler.getSubject(),"Process","Failed to process data:"+data
                        +"\n"+ ConsoleMain.getErrorInfo(e));
            }
        });
        temp.start();
        return temp;
    }
}
