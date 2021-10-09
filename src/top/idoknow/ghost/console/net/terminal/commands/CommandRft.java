package top.idoknow.ghost.console.net.terminal.commands;

import com.rft.core.server.BufferedFileReceiver;
import com.rft.core.server.FileInfo;
import top.idoknow.ghost.console.adapter.rft.RFTAdapter;
import top.idoknow.ghost.console.ioutil.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.CommandSyntaxException;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

import java.util.Map;

/**
 * RFT related operations.
 * @author Rock Chin
 */
public class CommandRft extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }

        if (params.length<2){
            throw new CommandSyntaxException("!rft <operation>");
        }

        switch (params[1]){
            case "chdir":{//change the dir where the received file be stored.
                if (params.length<3){
                    throw new CommandSyntaxException("!rft chdir <newDir>");
                }

                RFTAdapter.rftReceiver.setRootPath(params[2]);
                LogMgr.logMessage(handler.getSubject(),"RFT","Dir change to:"+params[2]);
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("Change dir to:"+params[2]).flush();
                break;
            }
            case "dir":{
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("Store root dir is:"+RFTAdapter.rftReceiver.getRootPath()).flush();
                break;
            }
            case "task":{
                Map<String, BufferedFileReceiver.ReceiveTask> taskMap=RFTAdapter.rftReceiver.getTaskMap();
                int i=0;
                for (String taskKey:taskMap.keySet()){
                    FileInfo task=taskMap.get(taskKey).getInfo();
                    ((IHasWrapper)handler).getWrapper().wrapTimeLn(taskKey+"\t"
                            +task.getName()+"\t"+task.getSize()+"\t"
                            +taskMap.get(taskKey).getReceivedSize()
                            +"\t"+task.getSavePath());
                }
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("token\tfileName\tlength\treceived\tpath");
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("List all processing rft tasks("+taskMap.size()+").").flush();
                break;
            }
            case "stop":{
                if (params.length<3){
                    throw new CommandSyntaxException("!rft stop <tokenStartWith>");
                }


                Map<String, BufferedFileReceiver.ReceiveTask> taskMap = RFTAdapter.rftReceiver.getTaskMap();
//                            Out.say("TransCmd-rft task","遍历所有正在进行的文件接收任务\ntoken    fileName    length    received   savePath");
                for (String token:taskMap.keySet()){
//                                Out.say(token+"   "+taskMap.get(token).getInfo().getName()+"   "+taskMap.get(token).getInfo().getSize()+"    "+taskMap.get(token).getReceivedSize()+"   "+ServerMain.fileReceiver.getRootPath()+taskMap.get(token).getInfo().getSavePath());
                    if(token.startsWith(params[2])){
                        RFTAdapter.rftReceiver.interruptFile(token);
                        LogMgr.logMessage(handler.getSubject(),"RFT","Interrupt task:"+token);
                        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Interrupt task:"+token).flush();
                    }
                }
                break;
            }
            default:{
                throw new CommandSyntaxException("!rft <chdir|dir|task|stop>");
            }
        }
    }
}
