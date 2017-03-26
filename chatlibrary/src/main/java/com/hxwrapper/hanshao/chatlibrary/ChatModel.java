package com.hxwrapper.hanshao.chatlibrary;

import android.content.Context;

import com.hxwrapper.hanshao.chatlibrary.db.UserDao;
import com.hxwrapper.hanshao.chatlibrary.domain.RobotUser;
import com.hxwrapper.hanshao.chatlibrary.utils.PreferenceManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IM辅助模型层
 */
public class ChatModel {

    //数据库操作对象
    UserDao dao = null;
    protected Context context = null;
    //缓存
    protected Map<Key,Object> valueCache = new HashMap<Key,Object>();
    
    public ChatModel(Context ctx){
        context = ctx;
        PreferenceManager.init(context);
    }

    /**
     * 保存联系人
     * @param contactList
     * @return
     */
    public boolean saveContactList(List<EaseUser> contactList) {
        UserDao dao = new UserDao(context);
        dao.saveContactList(contactList);
        return true;
    }

    /**
     * 得到联系人数据
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        UserDao dao = new UserDao(context);
        return dao.getContactList();
    }

    /**
     * 保存单个联系人
     * @param user
     */
    public void saveContact(EaseUser user){
        UserDao dao = new UserDao(context);
        dao.saveContact(user);
    }
    
    /**
     * 存储当前的名称到sp
     * @param username
     */
    public void setCurrentUserName(String username){
        PreferenceManager.getInstance().setCurrentUserName(username);
    }

    /**
     * 得到当前的名称
     * @return
     */
    public String getCurrentUsernName(){
        return PreferenceManager.getInstance().getCurrentUsername();
    }

    /**
     * 获得机器人列表
     * @return
     */
    public Map<String, RobotUser> getRobotList(){
        UserDao dao = new UserDao(context);
        return dao.getRobotUser();
    }

    /**
     * 保存机器人列表数据
     * @param robotList
     * @return
     */
    public boolean saveRobotList(List<RobotUser> robotList){
        UserDao dao = new UserDao(context);
        dao.saveRobotUser(robotList);
        return true;
    }

    /**
     * 设置消息是否提醒
     * @param paramBoolean
     */
    public void setSettingMsgNotification(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    /**
     * 得到消息是否提醒
     * @return
     */
    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    /**
     * 设置消息是否有声音提醒
     * @param paramBoolean
     */
    public void setSettingMsgSound(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    /**
     * 得到是否有声音提醒
     * @return
     */
    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    /**
     * 震动设置
     * @param paramBoolean
     */
    public void setSettingMsgVibrate(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    /**
     * 得到震动状态
     * @return
     */
    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }


    public void setSettingMsgSpeaker(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSpeaker(paramBoolean);
        valueCache.put(Key.SpakerOn, paramBoolean);
    }

    public boolean getSettingMsgSpeaker() {        
        Object val = valueCache.get(Key.SpakerOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }


    public void setDisabledGroups(List<String> groups){
        if(dao == null){
            dao = new UserDao(context);
        }
        
        List<String> list = new ArrayList<String>();
        list.addAll(groups);
        for(int i = 0; i < list.size(); i++){
            if(EaseAtMessageHelper.get().getAtMeGroups().contains(list.get(i))){
                list.remove(i);
                i--;
            }
        }

        dao.setDisabledGroups(list);
        valueCache.put(Key.DisabledGroups, list);
    }
    
    public List<String> getDisabledGroups(){
        Object val = valueCache.get(Key.DisabledGroups);

        if(dao == null){
            dao = new UserDao(context);
        }
        
        if(val == null){
            val = dao.getDisabledGroups();
            valueCache.put(Key.DisabledGroups, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }
    
    public void setDisabledIds(List<String> ids){
        if(dao == null){
            dao = new UserDao(context);
        }
        
        dao.setDisabledIds(ids);
        valueCache.put(Key.DisabledIds, ids);
    }
    
    public List<String> getDisabledIds(){
        Object val = valueCache.get(Key.DisabledIds);
        
        if(dao == null){
            dao = new UserDao(context);
        }

        if(val == null){
            val = dao.getDisabledIds();
            valueCache.put(Key.DisabledIds, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }
    
    public void setGroupsSynced(boolean synced){
        PreferenceManager.getInstance().setGroupsSynced(synced);
    }
    
    public boolean isGroupsSynced(){
        return PreferenceManager.getInstance().isGroupsSynced();
    }
    
    public void setContactSynced(boolean synced){
        PreferenceManager.getInstance().setContactSynced(synced);
    }
    
    public boolean isContactSynced(){
        return PreferenceManager.getInstance().isContactSynced();
    }
    
    public void setBlacklistSynced(boolean synced){
        PreferenceManager.getInstance().setBlacklistSynced(synced);
    }
    
    public boolean isBacklistSynced(){
        return PreferenceManager.getInstance().isBacklistSynced();
    }
    
    public void allowChatroomOwnerLeave(boolean value){
        PreferenceManager.getInstance().setSettingAllowChatroomOwnerLeave(value);
    }
    
    public boolean isChatroomOwnerLeaveAllowed(){
        return PreferenceManager.getInstance().getSettingAllowChatroomOwnerLeave();
    }
   
    public void setDeleteMessagesAsExitGroup(boolean value) {
        PreferenceManager.getInstance().setDeleteMessagesAsExitGroup(value);
    }
    
    public boolean isDeleteMessagesAsExitGroup() {
        return PreferenceManager.getInstance().isDeleteMessagesAsExitGroup();
    }
    
    public void setAutoAcceptGroupInvitation(boolean value) {
        PreferenceManager.getInstance().setAutoAcceptGroupInvitation(value);
    }
    
    public boolean isAutoAcceptGroupInvitation() {
        return PreferenceManager.getInstance().isAutoAcceptGroupInvitation();
    }
    

    public void setAdaptiveVideoEncode(boolean value) {
        PreferenceManager.getInstance().setAdaptiveVideoEncode(value);
    }
    
    public boolean isAdaptiveVideoEncode() {
        return PreferenceManager.getInstance().isAdaptiveVideoEncode();
    }

    public void setPushCall(boolean value) {
        PreferenceManager.getInstance().setPushCall(value);
    }

    public boolean isPushCall() {
        return PreferenceManager.getInstance().isPushCall();
    }

    public void setRestServer(String restServer){
        PreferenceManager.getInstance().setRestServer(restServer);
    }

    public String getRestServer(){
        return  PreferenceManager.getInstance().getRestServer();
    }

    public void setIMServer(String imServer){
        PreferenceManager.getInstance().setIMServer(imServer);
    }

    public String getIMServer(){
        return PreferenceManager.getInstance().getIMServer();
    }

    public void enableCustomServer(boolean enable){
        PreferenceManager.getInstance().enableCustomServer(enable);
    }

    public boolean isCustomServerEnable(){
        return PreferenceManager.getInstance().isCustomServerEnable();
    }

    public void enableCustomAppkey(boolean enable) {
        PreferenceManager.getInstance().enableCustomAppkey(enable);
    }

    public boolean isCustomAppkeyEnabled() {
        return PreferenceManager.getInstance().isCustomAppkeyEnabled();
    }

    public void setCustomAppkey(String appkey) {
        PreferenceManager.getInstance().setCustomAppkey(appkey);
    }

    public String getCutomAppkey() {
        return PreferenceManager.getInstance().getCustomAppkey();
    }

    enum Key{
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
