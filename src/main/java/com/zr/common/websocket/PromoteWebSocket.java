package com.zr.common.websocket;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class PromoteWebSocket {
    private static Logger LOGGER = LoggerFactory.getLogger(PromoteWebSocket.class);
    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的WebSocket对象。实现服务端与单一客户端通信的话，使用Map来存放，其中Key可以为用户标识
     */
    private static ConcurrentHashMap<String, PromoteWebSocket> webSocketMap =
            new ConcurrentHashMap<String, PromoteWebSocket>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 存取用户登录时间
     */
    public static  ConcurrentHashMap<String, Date> loginData = new ConcurrentHashMap<>();
    /**
     * 每个用户登陆的唯一标示用户ID
     */
    private String sessionId;

    /**
     * @Author ZhangXuBin
     * @param session
     * @param sessionId
     * @throws Exception
     */
    @OnOpen
    public void  onOpen(Session session, @Param("sessionId") String sessionId)throws Exception{
        this.session = session;
        webSocketMap.put(sessionId,this);
        LOGGER.info("有新的用户连接，现在在线人数："+webSocketMap.size());
    }

    /**
     * @Description 断开连接
     * @Authoe ZhangXuBin
     * @param sessionId
     */
    @OnClose
    public void onClose(@PathParam("sessionId") String sessionId){
        webSocketMap.remove(sessionId);
        LOGGER.info("有一个连接断开，现在在线人数"+webSocketMap.size());

    }
    /**
     * @Description 收到客户端信息后调用
     * @param message 客户端发过来的信息
     * @param session
     */
    @OnMessage
    public void onMessage( String message, Session session){
        LOGGER.info("收到来自用户的信息"+message);
        for(PromoteWebSocket item :webSocketMap.values()){
            try {
            item.sendMessage(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    }

    /**
     * 给在线所有用户群发消息
     * @author ZhangXuBin
     * @param message
     */
    public void sendAllMessage(String message){
        for(PromoteWebSocket item: webSocketMap.values()) {
            try {
                item.sendMessage(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * 发生消息
     * @Author ZhangXuBin
     * @param message
     */
    public void  sendMessage(String message){
        try {
        if(this.session.isOpen()){
                this.session.getBasicRemote().sendText(message);
        }else {
            //如果客户的不在了，就除去客户端session
            webSocketMap.remove(this.sessionId);
        }
            } catch (IOException e) {
               LOGGER.info("发生信息错误",e);
            }
    }
    /**
     * 发生错误时使用
     * @Authoe ZhangXuBin
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session,Throwable error){
        LOGGER.info("发生错误",error);
    }

}
