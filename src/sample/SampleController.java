package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class SampleController {


    public Label label_port;
    public TextField textField_port;
    public Button button_port;
    public Label label_prompt;
    public Label label_count;
    public TextArea textArea_listener;
    public Button button_save;
    private int NUM = 0;

    // 声明服务器端套接字ServerSocket
    ServerSocket serverSocket;
    // 输入流列表集合
    ArrayList<BufferedReader> bReaders = new ArrayList<BufferedReader>();
    // 输出流列表集合
    ArrayList<PrintWriter> pWriters = new ArrayList<PrintWriter>();
    // 聊天信息链表集合
    LinkedList<String> msgList = new LinkedList<String>();

    public void setOnAction(ActionEvent actionEvent) {
        try {
            int port = Integer.parseInt(textField_port.getText());
            if (port > 0 && port < 100000) {
                // 创建服务器端套接字ServerSocket，自定义端口
                serverSocket = new ServerSocket(port);
                label_prompt.setText("当前状态: 服务已开启 端口为:" + port);
                // 创建接收客户端Socket的线程实例，并启动
                new AcceptSocketThread().start();
                // 创建给客户端发送信息的线程实例，并启动
                new SendMsgToClient().start();
                label_count.setText("当前客户端连接数: " + NUM);
            } else {
                label_prompt.setText("当前状态: 端口错误");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pressToSave(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser(); // 创建一个文件对话框
        chooser.setTitle("保存文件"); // 设置文件对话框的标题
        chooser.setInitialDirectory(new File("D:\\")); // 设置文件对话框的初始目录
        // 创建一个文件类型过滤器
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("文本文件(*.txt)", "*.txt");
        // 给文件对话框添加文件类型过滤器
        chooser.getExtensionFilters().add(filter);
        File file = chooser.showSaveDialog(new Stage()); // 显示文件保存对话框
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file.getAbsolutePath(), false);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
            bufferedWriter.write(textArea_listener.getText(), 0, textArea_listener.getText().length());
            bufferedWriter.close();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    // 接收客户端Socket套接字线程
    class AcceptSocketThread extends Thread {
        public void run() {
            while (this.isAlive()) {
                try {
                    // 接收一个客户端Socket对象
                    Socket socket = serverSocket.accept();
                    // 建立该客户端的通信管道
                    if (socket != null) {
                        // 获取Socket对象的输入流
                        BufferedReader bReader = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        BufferedReader bufferedReader_userId = new BufferedReader(
                                new InputStreamReader(socket.getInputStream())
                        );
                        // 将输入流添加到输入流列表集合中
                        bReaders.add(bReader);
                        // 开启一个线程接收该客户端的聊天信息
                        new GetMsgFromClient(bReader).start();

                        // 获取Socket对象的输出流，并添加到输入出流列表集合中
                        pWriters.add(new PrintWriter(socket.getOutputStream()));
                        Platform.runLater(() -> {
                            //更新JavaFX的主线程的代码放在此处
                            NUM++;
                            label_count.setText("当前客户端连接数: " + NUM);
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    // 接收客户端的聊天信息的线程
    class GetMsgFromClient extends Thread {
        BufferedReader bReader;

        public GetMsgFromClient(BufferedReader bReader) {
            this.bReader = bReader;
        }

        public void run() {
            while (this.isAlive()) {
                try {
                    // 从输入流中读一行信息
                    String strMsg = bReader.readLine();
                    if (strMsg != null) {
                        // SimpleDateFormat日期格式化类，指定日期格式为"年-月-日  时:分:秒",例如"2015-11-06 13:50:26"
                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");
                        // 获取当前系统时间，并使用日期格式化类格式化为指定格式的字符串
                        String strTime = dateFormat.format(new Date());
                        // 将时间和信息添加到信息链表集合中
                        msgList.addFirst("<== " + strTime + " ==>\n" + strMsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        //更新JavaFX的主线程的代码放在此处
                        NUM--;
                        label_count.setText("当前客户端连接数: " + NUM);
                    });
                    this.stop();
                }
            }
        }
    }

    // 给所有客户发送聊天信息的线程
    class SendMsgToClient extends Thread {
        public void run() {
            while (this.isAlive()) {
                try {
                    // 如果信息链表集合不空（还有聊天信息未发送）
                    if (!msgList.isEmpty()) {
                        // 取信息链表集合中的最后一条,并移除
                        String msg = msgList.removeLast();
                        textArea_listener.appendText(msg + "\n");
                        // 对输出流列表集合进行遍历，循环发送信息给所有客户端
                        for (int i = 0; i < pWriters.size(); i++) {
                            pWriters.get(i).println(msg);
                            pWriters.get(i).flush();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
