package com.example.doanltm.Service;

import com.example.doanltm.DAO.CaLamDAO;
import com.example.doanltm.DAO.DangKyDAO;
import com.example.doanltm.DAO.UserDAO;
import com.example.doanltm.Model.*;
import com.example.doanltm.Request.*;
import com.example.doanltm.Response.DangKyResponse;
import com.example.doanltm.Response.GetCaLamResponse;
import com.example.doanltm.Response.GetDangKyResponse;
import com.example.doanltm.Response.HuyDangKyResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private static final int PORT = 8888;
    private static final int MAX_CLIENTS = 50;
    
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean isRunning = false;
    private int clientCount = 0;  // Đếm số client đang kết nối

    public TCPServer() {
        // Sử dụng CachedThreadPool thay vì FixedThreadPool cho hiệu quả tốt hơn
        threadPool = Executors.newCachedThreadPool();
        System.out.println("🛠️ Khởi tạo Thread Pool - MAX_CLIENTS: " + MAX_CLIENTS);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            System.out.println("🚀 TCP Server đang chạy trên port " + PORT);
            System.out.println("⏳ Đang chờ client kết nối...\n");

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                
                synchronized (this) {
                    clientCount++;
                    System.out.println("✅ Client mới kết nối: " + clientSocket.getInetAddress() + 
                                     " [Tổng: " + clientCount + "/" + MAX_CLIENTS + "]");
                }
                
                // Kiểm tra giới hạn client
                if (clientCount > MAX_CLIENTS) {
                    System.out.println("⚠️ Vượt quá giới hạn client! Từ chối kết nối.");
                    clientSocket.close();
                    synchronized (this) {
                        clientCount--;
                    }
                    continue;
                }

                // Xử lý client trong thread pool
                threadPool.execute(new ClientHandler(clientSocket, this));
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
                System.out.println("📊 Đang chờ tất cả client ngắt kết nối...");
                try {
                    if (!threadPool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                        threadPool.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    threadPool.shutdownNow();
                }
            }
            System.out.println("🛑 Server đã dừng hoàn toàn!");
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi dừng server: " + e.getMessage());
        }
    }
    
    /**
     * Giảm số client count khi client ngắt kết nối
     */
    public synchronized void decrementClientCount() {
        clientCount--;
        System.out.println("📊 Client ngắt kết nối. Còn lại: " + clientCount + "/" + MAX_CLIENTS);
    }

    /**
     * Inner class xử lý từng client
     */
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private TCPServer server;  // Tham chiếu tới server
        private String clientId;   // ID client cho dễ debug

        // DAOs
        private UserDAO userDAO;
        private CaLamDAO caLamDAO;
        private DangKyDAO dangKyDAO;

        public ClientHandler(Socket socket, TCPServer server) {
            this.clientSocket = socket;
            this.server = server;
            this.clientId = socket.getInetAddress() + ":" + socket.getPort();
            this.userDAO = new UserDAO();
            this.caLamDAO = new CaLamDAO();
            this.dangKyDAO = new DangKyDAO();
            
            System.out.println("🔍 ClientHandler tạo cho: " + clientId);
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
                    } else if (request instanceof GetCaLamRequest) {
                        handleGetCaLamRequest((GetCaLamRequest) request);
                    } else if (request instanceof DangKyRequest) {
                        handleDangKyRequest((DangKyRequest) request);
                    } else if (request instanceof GetDangKyRequest) {
                        handleGetDangKyRequest((GetDangKyRequest) request);
                    } else if (request instanceof HuyDangKyRequest) {
                        handleHuyDangKyRequest((HuyDangKyRequest) request);
                    } else {
                        System.out.println("⚠️ Request không xác định: " + request.getClass().getName());
                    }
                }

            } catch (EOFException e) {
                System.out.println("📤 Client đã ngắt kết nối bình thường: " + clientId);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("❌ Lỗi xử lý client " + clientId + ": " + e.getMessage());
            } finally {
                closeConnection();
            }
        }

        /**
         * Xử lý login request
         */
        private void handleLoginRequest(LoginRequest request) {
            System.out.println("📥 Nhận login request: " + request);

            try {
                User user = userDAO.authenticate(request.getEmail(), request.getMatKhau());

                LoginResponse response;
                if (user != null) {
                    user.setMatKhau(null); // Không gửi mật khẩu về client
                    response = new LoginResponse(true, "Đăng nhập thành công!", user);
                    System.out.println("✅ Login thành công cho user: " + user.getEmail());
                } else {
                    response = new LoginResponse(false, "Email hoặc mật khẩu không đúng!");
                    System.out.println("❌ Login thất bại cho email: " + request.getEmail());
                }

                out.writeObject(response);
                out.flush();
                System.out.println("📤 Đã gửi LoginResponse về client\n");

            } catch (IOException e) {
                System.err.println("❌ Lỗi khi gửi response: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Xử lý get ca lam request
         */
        private void handleGetCaLamRequest(GetCaLamRequest request) {
            System.out.println("📥 Nhận get ca lam request: " + request);

            try {
                List<CaLam> caLamList = caLamDAO.getCaLamWithRegistrationCount(request.getNgayLam());

                GetCaLamResponse response;
                if (caLamList != null && !caLamList.isEmpty()) {
                    response = new GetCaLamResponse(true, "Lấy danh sách ca làm thành công!", caLamList);
                    System.out.println("✅ Tìm thấy " + caLamList.size() + " ca làm");
                } else {
                    response = new GetCaLamResponse(true, "Không có ca làm nào!", caLamList);
                    System.out.println("⚠️ Không có ca làm nào");
                }

                out.writeObject(response);
                out.flush();
                System.out.println("📤 Đã gửi GetCaLamResponse về client\n");

            } catch (IOException e) {
                System.err.println("❌ Lỗi khi gửi response: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Xử lý dang ky request
         */
        private void handleDangKyRequest(DangKyRequest request) {
            System.out.println("📥 Nhận dang ky request: " + request);
            
            try {
                DangKy dangKy = request.getDangKy();
                System.out.println("🔍 Debug - DangKy object: " + dangKy);
                System.out.println("🔍 Debug - isCaGay(): " + dangKy.isCaGay());
                System.out.println("🔍 Debug - MaCalam: " + dangKy.getMaCalam());
                System.out.println("🔍 Debug - NgayLam: " + dangKy.getNgayLam());
                
                DangKyResponse response;

                // Kiểm tra logic trước khi đăng ký
                if (dangKy.isCaGay()) {
                    // Ca gãy - kiểm tra trùng giờ
                    boolean trungGio = dangKyDAO.isTrungGio(
                            dangKy.getMaNguoidung(),
                            dangKy.getNgayLam(),
                            dangKy.getGbdCagay(),
                            dangKy.getGktCagay()
                    );

                    if (trungGio) {
                        response = new DangKyResponse(false, "Bạn đã có ca trùng giờ trong ngày này!");
                        System.out.println("❌ Trùng giờ ca gãy");
                    } else {
                        boolean success = dangKyDAO.dangKyCaLam(dangKy);
                        if (success) {
                            response = new DangKyResponse(true, "Đăng ký ca gãy thành công!", dangKy);
                            System.out.println("✅ Đăng ký ca gãy thành công");
                        } else {
                            response = new DangKyResponse(false, "Đăng ký thất bại!");
                            System.out.println("❌ Đăng ký ca gãy thất bại");
                        }
                    }
                } else {
                    // Ca bình thường - kiểm tra đã đăng ký chưa
                    boolean daDangKy = dangKyDAO.isDaDangKyCaBinhThuong(
                            dangKy.getMaNguoidung(),
                            dangKy.getMaCalam(),
                            dangKy.getNgayLam()
                    );

                    if (daDangKy) {
                        response = new DangKyResponse(false, "Bạn đã đăng ký ca này rồi!");
                        System.out.println("❌ Đã đăng ký ca này");
                    } else {
                        // Kiểm tra slot còn trống
                        CaLam caLam = caLamDAO.getCaLamById(dangKy.getMaCalam());
                        int soLuongDaDangKy = caLamDAO.getSoLuongDaDangKy(dangKy.getMaCalam(), dangKy.getNgayLam());

                        if (caLam != null && soLuongDaDangKy >= caLam.getSoLuongToiDa()) {
                            response = new DangKyResponse(false, "Ca này đã đủ số lượng!");
                            System.out.println("❌ Ca đã đủ người");
                        } else {
                            boolean success = dangKyDAO.dangKyCaLam(dangKy);
                            if (success) {
                                response = new DangKyResponse(true, "Đăng ký ca làm thành công!", dangKy);
                                System.out.println("✅ Đăng ký ca bình thường thành công");
                            } else {
                                response = new DangKyResponse(false, "Đăng ký thất bại!");
                                System.out.println("❌ Đăng ký ca bình thường thất bại");
                            }
                        }
                    }
                }

                out.writeObject(response);
                out.flush();
                System.out.println("📤 Đã gửi DangKyResponse về client\n");

            } catch (IOException e) {
                System.err.println("❌ Lỗi khi gửi response: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Xử lý get dang ky request
         */
        private void handleGetDangKyRequest(GetDangKyRequest request) {
            System.out.println("📥 Nhận get dang ky request: " + request);

            try {
                List<DangKy> dangKyList = dangKyDAO.getDangKyByUser(request.getMaNguoidung());

                GetDangKyResponse response;
                if (dangKyList != null) {
                    response = new GetDangKyResponse(true, "Lấy danh sách đăng ký thành công!", dangKyList);
                    System.out.println("✅ Tìm thấy " + dangKyList.size() + " đăng ký");
                } else {
                    response = new GetDangKyResponse(false, "Lỗi khi lấy danh sách đăng ký!");
                    System.out.println("❌ Lỗi khi lấy danh sách");
                }

                out.writeObject(response);
                out.flush();
                System.out.println("📤 Đã gửi GetDangKyResponse về client\n");

            } catch (IOException e) {
                System.err.println("❌ Lỗi khi gửi response: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Xử lý huy dang ky request
         */
        private void handleHuyDangKyRequest(HuyDangKyRequest request) {
            System.out.println("📥 Nhận huy dang ky request: " + request);

            try {
                boolean success = dangKyDAO.huyDangKy(request.getMaDangky(), request.getMaNguoidung());

                HuyDangKyResponse response;
                if (success) {
                    response = new HuyDangKyResponse(true, "Hủy đăng ký thành công!");
                    System.out.println("✅ Hủy đăng ký thành công");
                } else {
                    response = new HuyDangKyResponse(false, "Không thể hủy đăng ký này!");
                    System.out.println("❌ Hủy đăng ký thất bại");
                }

                out.writeObject(response);
                out.flush();
                System.out.println("📤 Đã gửi HuyDangKyResponse về client\n");

            } catch (IOException e) {
                System.err.println("❌ Lỗi khi gửi response: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void closeConnection() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
                
                // Giảm client count
                if (server != null) {
                    server.decrementClientCount();
                }
                
                System.out.println("🔌 Đã đóng kết nối client: " + clientId + "\n");
                
            } catch (IOException e) {
                System.err.println("❌ Lỗi khi đóng kết nối " + clientId + ": " + e.getMessage());
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