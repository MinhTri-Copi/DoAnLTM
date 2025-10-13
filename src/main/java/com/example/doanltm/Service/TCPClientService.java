package com.example.doanltm.Service;

import com.example.doanltm.Model.LoginRequest;
import com.example.doanltm.Model.LoginResponse;

import java.io.*;
import java.net.Socket;

public class TCPClientService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public boolean connect() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("✅ Kết nối đến server thành công!");
            return true;
        } catch (IOException e) {
            System.err.println("❌ Không thể kết nối đến server: " + e.getMessage());
            return false;
        }
    }
    
    public LoginResponse login(LoginRequest request) {
        try {
            if (socket == null || socket.isClosed()) {
                if (!connect()) {
                    return new LoginResponse(false, "Không thể kết nối đến server!");
                }
            }
            
            // Gửi request
            out.writeObject(request);
            out.flush();
            System.out.println("📤 Đã gửi login request: " + request);
            
            // Nhận response
            LoginResponse response = (LoginResponse) in.readObject();
            System.out.println("📥 Nhận response: " + response);
            
            return response;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Lỗi khi gửi/nhận dữ liệu: " + e.getMessage());
            e.printStackTrace();
            return new LoginResponse(false, "Lỗi kết nối: " + e.getMessage());
        }
    }
    
    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("✅ Đã ngắt kết nối khỏi server!");
            }
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }
}
