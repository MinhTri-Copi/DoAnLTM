package com.example.doanltm.Service;

import com.example.doanltm.Database.UserDAO;
import com.example.doanltm.Model.LoginRequest;
import com.example.doanltm.Model.LoginResponse;
import com.example.doanltm.Model.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private static final int PORT = 8888;
    private static final int MAX_CLIENTS = 50;
    
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean isRunning = false;
    
    public TCPServer() {
        threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            System.out.println("🚀 TCP Server đang chạy trên port " + PORT);
            System.out.println("⏳ Đang chờ client kết nối...\n");
            
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("✅ Client mới kết nối: " + clientSocket.getInetAddress());
                
                // Xử lý client trong thread pool
                threadPool.execute(new ClientHandler(clientSocket));
            }
            
        } catch (IOException e) {
            if (isRunning) {
                System.err.println("❌ Lỗi Server: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public void stop() {
        try {
            isRunning = false;
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (threadPool != null) {
                threadPool.shutdown();
            }
            System.out.println("🛑 Server đã dừng!");
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi dừng server: " + e.getMessage());
        }
    }
    
    /**
     * Inner class xử lý từng client
     */
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private UserDAO userDAO;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.userDAO = new UserDAO();
        }
        
        @Override
        public void run() {
            try {
                // Khởi tạo streams
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                
                // Lắng nghe request từ client
                while (true) {
                    Object request = in.readObject();
                    
                    if (request instanceof LoginRequest) {
                        handleLoginRequest((LoginRequest) request);
                    }
                    // TODO: Thêm các request khác (Register, GetSchedule, UpdateSchedule...)
                }
                
            } catch (EOFException e) {
                System.out.println("📤 Client đã ngắt kết nối: " + clientSocket.getInetAddress());
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("❌ Lỗi xử lý client: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }
        
        private void handleLoginRequest(LoginRequest request) {
            System.out.println("📥 Nhận login request: " + request);
            
            try {
                // Xác thực với database
                User user = userDAO.authenticate(request.getEmail(), request.getMatKhau());
                
                LoginResponse response;
                if (user != null) {
                    // Đăng nhập thành công - KHÔNG gửi mật khẩu về client
                    user.setMatKhau(null);
                    response = new LoginResponse(true, "Đăng nhập thành công!", user);
                    System.out.println("✅ Login thành công cho user: " + user.getEmail());
                } else {
                    response = new LoginResponse(false, "Email hoặc mật khẩu không đúng!");
                    System.out.println("❌ Login thất bại cho email: " + request.getEmail());
                }
                
                // Gửi response về client
                out.writeObject(response);
                out.flush();
                System.out.println("📤 Đã gửi response về client\n");
                
            } catch (IOException e) {
                System.err.println("❌ Lỗi khi gửi response: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        private void closeConnection() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
                System.out.println("🔌 Đã đóng kết nối client");
            } catch (IOException e) {
                System.err.println("❌ Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }
    
    // Main method để chạy server
    public static void main(String[] args) {
        TCPServer server = new TCPServer();
        
        // Thêm shutdown hook để đóng server khi tắt chương trình
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n⚠️ Đang tắt server...");
            server.stop();
        }));
        
        server.start();
    }
}
