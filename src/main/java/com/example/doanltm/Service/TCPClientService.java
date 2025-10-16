package com.example.doanltm.Service;

import com.example.doanltm.Model.*;
import com.example.doanltm.Request.*;
import com.example.doanltm.Response.DangKyResponse;
import com.example.doanltm.Response.GetCaLamResponse;
import com.example.doanltm.Response.GetDangKyResponse;
import com.example.doanltm.Response.HuyDangKyResponse;

import java.io.*;
import java.net.Socket;

public class TCPClientService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isConnected = false;
    
    /**
     * Kết nối đến server (giữ kết nối lâu dài)
     */
    public synchronized boolean connect() {
        if (isConnected && socket != null && !socket.isClosed()) {
            System.out.println("✅ Đã có kết nối sẵn sàng!");
            return true;
        }
        
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            System.out.println("✅ Kết nối đến server thành công!");
            return true;
        } catch (IOException e) {
            System.err.println("❌ Không thể kết nối đến server: " + e.getMessage());
            isConnected = false;
            return false;
        }
    }
    
    /**
     * Kiểm tra kết nối và reconnect nếu cần
     */
    private synchronized boolean ensureConnection() {
        if (!isConnected || socket == null || socket.isClosed()) {
            System.out.println("⚠️ Kết nối bị mất, đang thử kết nối lại...");
            return connect();
        }
        return true;
    }
    
    /**
     * Đăng nhập
     */
    public synchronized LoginResponse login(LoginRequest request) {
        if (!ensureConnection()) {
            return new LoginResponse(false, "Không thể kết nối đến server!");
        }
        
        try {
            out.writeObject(request);
            out.flush();
            System.out.println("📤 Đã gửi login request: " + request);
            
            LoginResponse response = (LoginResponse) in.readObject();
            System.out.println("📥 Nhận response: " + response);
            
            return response;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Lỗi khi gửi/nhận dữ liệu: " + e.getMessage());
            e.printStackTrace();
            isConnected = false;
            
            // Thử reconnect và gửi lại
            if (ensureConnection()) {
                return login(request);
            }
            return new LoginResponse(false, "Lỗi kết nối: " + e.getMessage());
        }
    }
    
    /**
     * Lấy danh sách ca làm
     */
    public synchronized GetCaLamResponse getCaLam(GetCaLamRequest request) {
        if (!ensureConnection()) {
            return new GetCaLamResponse(false, "Không thể kết nối đến server!");
        }
        
        try {
            out.writeObject(request);
            out.flush();
            System.out.println("📤 Đã gửi get ca lam request: " + request);
            
            GetCaLamResponse response = (GetCaLamResponse) in.readObject();
            System.out.println("📥 Nhận response với " + (response.getCaLamList() != null ? response.getCaLamList().size() : 0) + " ca làm");
            
            return response;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Lỗi khi gửi/nhận dữ liệu: " + e.getMessage());
            e.printStackTrace();
            isConnected = false;
            
            // Thử reconnect và gửi lại
            if (ensureConnection()) {
                return getCaLam(request);
            }
            return new GetCaLamResponse(false, "Lỗi kết nối: " + e.getMessage());
        }
    }
    
    /**
     * Đăng ký ca làm
     */
    public synchronized DangKyResponse dangKyCaLam(DangKyRequest request) {
        if (!ensureConnection()) {
            return new DangKyResponse(false, "Không thể kết nối đến server!");
        }
        
        try {
            out.writeObject(request);
            out.flush();
            System.out.println("📤 Đã gửi dang ky request: " + request);
            
            DangKyResponse response = (DangKyResponse) in.readObject();
            System.out.println("📥 Nhận response: " + response.getMessage());
            
            return response;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Lỗi khi gửi/nhận dữ liệu: " + e.getMessage());
            e.printStackTrace();
            isConnected = false;
            
            // Thử reconnect và gửi lại
            if (ensureConnection()) {
                return dangKyCaLam(request);
            }
            return new DangKyResponse(false, "Lỗi kết nối: " + e.getMessage());
        }
    }
    
    /**
     * Lấy danh sách đăng ký của user
     */
    public synchronized GetDangKyResponse getDangKyByUser(GetDangKyRequest request) {
        if (!ensureConnection()) {
            return new GetDangKyResponse(false, "Không thể kết nối đến server!");
        }
        
        try {
            out.writeObject(request);
            out.flush();
            System.out.println("📤 Đã gửi get dang ky request: " + request);
            
            GetDangKyResponse response = (GetDangKyResponse) in.readObject();
            System.out.println("📥 Nhận response với " + (response.getDangKyList() != null ? response.getDangKyList().size() : 0) + " đăng ký");
            
            return response;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Lỗi khi gửi/nhận dữ liệu: " + e.getMessage());
            e.printStackTrace();
            isConnected = false;
            
            // Thử reconnect và gửi lại
            if (ensureConnection()) {
                return getDangKyByUser(request);
            }
            return new GetDangKyResponse(false, "Lỗi kết nối: " + e.getMessage());
        }
    }
    
    /**
     * Hủy đăng ký
     */
    public synchronized HuyDangKyResponse huyDangKy(HuyDangKyRequest request) {
        if (!ensureConnection()) {
            return new HuyDangKyResponse(false, "Không thể kết nối đến server!");
        }
        
        try {
            out.writeObject(request);
            out.flush();
            System.out.println("📤 Đã gửi huy dang ky request: " + request);
            
            HuyDangKyResponse response = (HuyDangKyResponse) in.readObject();
            System.out.println("📥 Nhận response: " + response.getMessage());
            
            return response;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Lỗi khi gửi/nhận dữ liệu: " + e.getMessage());
            e.printStackTrace();
            isConnected = false;
            
            // Thử reconnect và gửi lại
            if (ensureConnection()) {
                return huyDangKy(request);
            }
            return new HuyDangKyResponse(false, "Lỗi kết nối: " + e.getMessage());
        }
    }
    
    /**
     * Ngắt kết nối
     */
    public synchronized void disconnect() {
        try {
            isConnected = false;
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
    
    /**
     * Kiểm tra trạng thái kết nối
     */
    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }
}
