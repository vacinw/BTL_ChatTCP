package com.mycompany.lib;

import java.io.*; // Cung cấp đầu vào và đầu ra của hệ thống thông qua các luồng dữ liệu, tuần tự hóa và hệ thống tệp.
import java.net.*; // hỗ trợ thiết kế ứng dụng mạng
import javax.swing.*; // thiết kế giao diện người dùng đồ hoạ

public class ChatMessageSocket {
    // gồm socket và bảng hiển thị 
    private Socket socket; //Biến  này được sử dụng để thiết lập kết nối tới Socket Server.
    private JTextPane txpMessageBoard;//Biến này được sử dụng để hiển thị các tin nhắn trao đổi giữa Socket Client và Socket Server để người dùng có thể theo dõi và đọc được.
    
    // luồng gửi dữ liệu
    private final PrintWriter out;//In các biểu diễn được định dạng của các đối tượng thành luồng đầu ra văn bản.
    // luồng nhận dữ liệu
    private BufferedReader reader;//Đọc văn bản từ luồng nhập ký tự, đệm các ký tự để cung cấp khả năng đọc ký tự, mảng và dòng hiệu quả.
    
    // constructor này giúp thiết lập cơ sở cho kết nối socket giữa client và server trong ứng dụng chat, đồng thời cũng khởi tạo các đối tượng input/output stream để giao tiếp với server.
    public ChatMessageSocket(Socket socket, JTextPane txpMessageBoard) throws IOException {
        this.socket = socket;
        this.txpMessageBoard = txpMessageBoard;
        
        out = new PrintWriter(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        receive();//Gọi phương thức receive() để bắt đầu nhận các thông điệp từ server gửi về.
    }
    
    // phương thức nhận dữ liệu (điểu khiển bằng luồng)
    private void receive(){
        Thread th = new Thread(){
            @Override
            public void run(){
                try {
                    while(true){
                        String sms = reader.readLine();
                        if(sms != null){
                            txpMessageBoard.setText(txpMessageBoard.getText() + "\nfriend: " + sms);
                        }
                    }
                } catch (Exception e) {
                }
            }   
        };
        th.start();        
    }
    
    public void send(String sms){
        String current = txpMessageBoard.getText();
        txpMessageBoard.setText(current + "\nyou: " + sms);
        out.println(sms);
        out.flush();
    }
    
    public void close() throws IOException{
        out.close();
        reader.close();
        socket.close();
    }
}
//InputStreamReader là cầu nối từ luồng byte sang luồng ký tự: Nó đọc byte và giải mã chúng thành ký tự bằng cách sử dụng tệp charset.
//getOutputStream