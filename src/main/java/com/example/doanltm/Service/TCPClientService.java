package com.example.doanltm.Service;

import com.example.doanltm.Model.DangKy;
import com.example.doanltm.Request.*;
import com.example.doanltm.Response.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TCPClientService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private volatile boolean isConnected = false;
    private Thread readerThread;
    private NotificationListener notificationListener;

    // Hàng đợi để giữ các response cho các request đồng bộ
    private final BlockingQueue<Object> responseQueue = new LinkedBlockingQueue<>();

    public synchronized boolean connect() {
        if (isConnected && socket != null && !socket.isClosed()) {
            return true;
        }
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            startReaderThread(); // Bắt đầu luồng đọc duy nhất
            System.out.println("✅ Kết nối đến server thành công!");
            return true;
        } catch (IOException e) {
            System.err.println("❌ Không thể kết nối đến server: " + e.getMessage());
            isConnected = false;
            return false;
        }
    }

    public void setNotificationListener(NotificationListener listener) {
        this.notificationListener = listener;
    }

    private void startReaderThread() {
        readerThread = new Thread(() -> {
            while (isConnected) {
                try {
                    // Luồng này là nơi duy nhất đọc từ ObjectInputStream
                    Object serverMessage = in.readObject();

                    // Phân loại message: là Notification hay Response?
                    if (serverMessage instanceof RegistrationStatusChangedNotification) {
                        if (notificationListener != null) {
                            DangKy updatedDangKy = ((RegistrationStatusChangedNotification) serverMessage).getUpdatedDangKy();
                            notificationListener.onStatusChange(updatedDangKy);
                        }
                    } else if (serverMessage instanceof NewRegistrationNotification) {
                        if (notificationListener != null) {
                            notificationListener.onNewRegistration((NewRegistrationNotification) serverMessage);
                        }
                    } else {
                        // Nếu không phải là notification, đó là một response đang được chờ
                        responseQueue.put(serverMessage);
                    }
                } catch (SocketException e) {
                    if (!isConnected) {
                        System.out.println("🔌 Socket đã đóng, luồng đọc dừng lại.");
                        break;
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    if (isConnected) {
                        System.err.println("❌ Mất kết nối với server: " + e.getMessage());
                        isConnected = false;
                    }
                    break;
                }
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private synchronized <T> T sendRequest(Serializable request) {
        if (!ensureConnection()) {
            System.err.println("Không thể gửi request, mất kết nối.");
            return null; // Hoặc trả về một response lỗi chung
        }
        try {
            // Xóa hàng đợi trước khi gửi request mới
            responseQueue.clear();
            
            synchronized (out) {
                out.writeObject(request);
                out.flush();
            }

            // Chờ và lấy response từ hàng đợi, với timeout
            Object response = responseQueue.poll(10, TimeUnit.SECONDS);
            if (response == null) {
                System.err.println("Request timed out!");
                return null;
            }
            return (T) response;
        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Lỗi khi gửi request hoặc chờ response: " + e.getMessage());
            isConnected = false;
            return null;
        }
    }

    public LoginResponse login(LoginRequest request) {
        return sendRequest(request);
    }

    public GetCaLamResponse getCaLam(GetCaLamRequest request) {
        return sendRequest(request);
    }

    public DangKyResponse dangKyCaLam(DangKyRequest request) {
        return sendRequest(request);
    }

    public GetDangKyResponse getDangKyByUser(GetDangKyRequest request) {
        return sendRequest(request);
    }

    public HuyDangKyResponse huyDangKy(HuyDangKyRequest request) {
        return sendRequest(request);
    }

    public CapNhatTrangThaiResponse capNhatTrangThai(CapNhatTrangThaiRequest request) {
        return sendRequest(request);
    }

    public ThongKeAdminResponse getThongKeAdmin(ThongKeAdminRequest request) {
        return sendRequest(request);
    }

    public DanhSachDangKyAdminResponse getDanhSachDangKyAdmin(DanhSachDangKyAdminRequest request) {
        return sendRequest(request);
    }

    public synchronized void disconnect() {
        isConnected = false;
        try {
            if (readerThread != null) {
                readerThread.interrupt();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("✅ Đã ngắt kết nối khỏi server.");
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }

    private synchronized boolean ensureConnection() {
        if (!isConnected()) {
            System.out.println("⚠️ Kết nối bị mất, đang thử kết nối lại...");
            return connect();
        }
        return true;
    }
}
