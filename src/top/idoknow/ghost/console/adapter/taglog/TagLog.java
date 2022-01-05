package top.idoknow.ghost.console.adapter.taglog;

import top.idoknow.ghost.console.ioutil.FileIO;

import java.io.File;
import java.util.*;

public class TagLog {
    public static class tagOwner{
        public static class tag{
            String name;
            long time=0;
        }
        ArrayList<tag> tags=new ArrayList<>();
        public void addTag(String tagName){
            if (tags.size()>0&&tags.get(tags.size()-1).name.equals(tagName)){//跟上一次一样
                tags.get(tags.size()-1).time=new Date().getTime();
            }else{//不一样或无任何tag
                tag tag=new tag();
                tag.name=tagName;
                tag.time=new Date().getTime();
                tags.add(tag);
            }
        }
    }
    private final Map<String,tagOwner> allOwner=new LinkedHashMap<>();

    public static boolean enable=false;
    public static boolean isEnable(){
        return enable;
    }

    public Map<String, tagOwner> getAllOwner() {
        return allOwner;
    }

    private void addOwner(String indexName){
        if (!isEnable()){
            return;
        }
        allOwner.put(indexName,new tagOwner());
    }
    public void addTag(String ownerName,String tag){
        if (!isEnable()){
            return;
        }
        if(!allOwner.containsKey(ownerName))
            addOwner(ownerName);
        allOwner.get(ownerName).addTag(tag);
    }
    //ownerName:time tag,time2 tag2;ownerName:time tag;
    public void pack()throws Exception{
        if (!isEnable()){
            return;
        }
        StringBuilder fileStr=new StringBuilder();
        for(String ownerName:allOwner.keySet()){
            StringBuilder aownerStr=new StringBuilder(ownerName+":");
            tagOwner owner=allOwner.get(ownerName);
            int index=0;
            for(tagOwner.tag tag:owner.tags){
                aownerStr.append(tag.time)
                        .append(" ")
                        .append(tag.name);
                aownerStr.append(index==owner.tags.size()-1?"":",");
                index++;
            }
            fileStr.append(aownerStr)
                    .append(";");
        }
        FileIO.write("tagLog.txt",fileStr.toString(),true);
    }
    //ownerName:time tag,time2 tag2;ownerName:time tag;
    public void load()throws Exception{
        if (!isEnable()){
            return;
        }
        allOwner.clear();
        if(!new File("tagLog.txt").exists()){
            return;
        }
        String[] owners = FileIO.read("tagLog.txt").split(";");
        for(String aowner:owners){
            tagOwner tagOwner=new tagOwner();
            String[] nameAndTags =aowner.split(":");
            String[] tags =nameAndTags[1].split(",");
            for(String atag:tags){
                String[] tagInfo =atag.split(" ");
                tagOwner.tag tag=new tagOwner.tag();
                tag.name=tagInfo[1];
                tag.time=Long.parseLong(tagInfo[0]);
                tagOwner.tags.add(tag);
            }
            allOwner.put(nameAndTags[0],tagOwner);
        }
    }
}
