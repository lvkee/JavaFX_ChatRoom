/*
package sample;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class ChatServer extends Application {
    StringProperty valueProperty;
    // 声明服务器端套接字ServerSocket
    ServerSocket serverSocket;
    // 输入流列表集合
    ArrayList<BufferedReader> bReaders = new ArrayList<BufferedReader>();
    // 输出流列表集合
    ArrayList<PrintWriter> pWriters = new ArrayList<PrintWriter>();
    // 聊天信息链表集合
    LinkedList<String> msgList = new LinkedList<String>();

    private final Label label_prompt = new Label("当前状态:");
    private final Label label_port = new Label("端口:");
    private Label label_count = new Label();
    private TextField textField_port = new TextField();
    private final Button button = new Button("设置");
    private int NUM = 0;
    Pane pane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        pane = new Pane();
        HBox hBox_set = new HBox(label_port, textField_port, button);
        hBox_set.setSpacing(5);
        hBox_set.setLayoutY(10);
        hBox_set.setLayoutX(5);
        label_prompt.setLayoutX(20);
        label_prompt.setLayoutY(80);
        label_count.setLayoutX(20);
        label_count.setLayoutY(60);
        valueProperty = new SimpleStringProperty("当前客户端连接数: " + NUM);
        label_count.textProperty().bind(valueProperty);

        button.setOnAction(event -> {
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
                } else {
                    label_prompt.setText("当前状态: 端口错误");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pane.getChildren().addAll(hBox_set, label_prompt);
        pane.getChildren().add(label_count);

        pane.setPrefSize(250, 100);
        Scene scene = new Scene(pane);
        primaryStage.setTitle("聊天室服务端");
        primaryStage.getIcons().add(new Image("file:src/res/server.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 接收客户端Socket套接字线程
    static class AcceptSocketThread extends Thread {
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
                        // 将输入流添加到输入流列表集合中
                        bReaders.add(bReader);
                        // 开启一个线程接收该客户端的聊天信息
                        new GetMsgFromClient(bReader).start();

                        // 获取Socket对象的输出流，并添加到输入出流列表集合中
                        pWriters.add(new PrintWriter(socket.getOutputStream()));
                        NUM++;
                        valueProperty.set("当前客户端连接数: " + NUM);
                        System.out.println(label_count.getText());
                        System.out.println(NUM);
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
                }
            }
        }
    }

    // 给所有客户发送聊天信息的线程
    static class SendMsgToClient extends Thread {
        public void run() {
            while (this.isAlive()) {
                try {
                    // 如果信息链表集合不空（还有聊天信息未发送）
                    if (!msgList.isEmpty()) {
                        // 取信息链表集合中的最后一条,并移除
                        String msg = msgList.removeLast();
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

    public static void main(String[] args) {
        launch(args);
    }

}
*/
