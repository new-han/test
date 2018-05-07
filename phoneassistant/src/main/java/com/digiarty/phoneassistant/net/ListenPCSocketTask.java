package com.digiarty.phoneassistant.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/***
 *
 * Created on：28/04/2018
 *
 * Created by：henmory
 *
 * Description:
 *
 *
 **/
public class ListenPCSocketTask implements ITask {

    private static Logger logger = LoggerFactory.getLogger(ListenPCSocketTask.class);

    private Boolean serverListenSocketFlag = FALSE;
    private ServerSocket serverSocket;

    @Override
    public void run() {

        Thread.currentThread().setName("监听pc端socket线程");
        logger.debug("线程id = " + Thread.currentThread().getId() + "的线程开始启动，监听socket连接");

        serverSocket = createServerSocketWaitingForPCToConnect();
        if (null != serverSocket) {
            logger.debug("服务器socket信息: serverSocket = " + serverSocket.toString());
        } else {
            NetTaskManager.notifyNetTaskManagerClearAllTask();
            logger.debug("线程id = " + Thread.currentThread().getId() + "的线程销毁");
            return;
        }

        NetTaskManager.newTaskToSendAndroidServerPortToPC(serverSocket.getLocalPort());

        logger.debug("开始监听PC端socket....");

        serverListenSocketFlag = TRUE;
        while (serverListenSocketFlag ) {

            if (serverSocket.isClosed()) {
                logger.debug("接收数据前，连接已经断开");
                System.out.println("接收数据前，连接已经断开");
                break;
            }

            Socket socketToCommunicateWithPC = listenPCConnectAndCreateNewSocketForPCConnection(serverSocket);
            if (socketToCommunicateWithPC != null){
                logger.debug("开始与PC端通信");
                NetTaskManager.newTaskToCommunicateWithPC(socketToCommunicateWithPC);
            }else{
                logger.debug("未知原因监听socket出现问题");
                break;
            }
        }
        NetTaskManager.notifyNetTaskManagerClearAllTask();
        logger.debug("线程id = " + Thread.currentThread().getId() + "的线程销毁");
    }


    public ServerSocket createServerSocketWaitingForPCToConnect() {
        return serverSocket = ServerSocketWrap.createSocketForListen(ServerConfig.AndroidConfig.getPort());
    }

    private Socket listenPCConnectAndCreateNewSocketForPCConnection(ServerSocket serverSocket) {

        Socket socketToCommunicateWithPC = ServerSocketWrap.listenAndCreatSocketForNewConnection(serverSocket);

        if (socketToCommunicateWithPC != null) {
            logger.debug("出现新的PC连接,为其创建新的socket");
            logger.debug("新创建的socket信息: socketToCommunicateWithClient = " + socketToCommunicateWithPC.toString());
        } else {
            return null;
        }
        return socketToCommunicateWithPC;
    }


    private void closeListenSocket(ServerSocket serverSocket) {
        serverListenSocketFlag = FALSE;
        ServerSocketWrap.closeListenSocket(serverSocket);
    }
    @Override
    public void closeCurrentTask(){
        logger.debug("资源在回收");
        serverListenSocketFlag = false;
        closeListenSocket(serverSocket);
        logger.debug("资源释放完成");
    }

}
