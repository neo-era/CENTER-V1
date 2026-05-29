
TIÊU CHUẨN VÀ YÊU CẦU KỸ THUẬT
PHẦN MỀM ĐIỀU KHIỂN TRUNG TÂM
Hệ thống chiếu sáng đô thị và tín hiệu giao thông
I. MỤC ĐÍCH VÀ PHẠM VI
1. Mục đích
Tài liệu này quy định các tiêu chuẩn, yêu cầu kỹ thuật bắt buộc đối với phần mềm điều khiển trung tâm phục vụ công tác giám sát, điều khiển, vận hành và quản lý hệ thống chiếu sáng đô thị, tín hiệu giao thông; bảo đảm tính mở, tính tương thích, tính bảo mật, độ tin cậy và khả năng mở rộng theo định hướng đô thị thông minh, công nghiệp 4.0 và Internet vạn vật (IoT).
2. Phạm vi áp dụng
Tài liệu áp dụng cho việc thiết kế, phát triển, triển khai, nghiệm thu và bàn giao phần mềm điều khiển trung tâm thuộc phạm vi cung cấp của Gói thầu, bao gồm các thành phần chính:
Lớp dịch vụ kết nối (Connectivity Layer) thu thập dữ liệu từ các tủ điều khiển chiếu sáng, tủ điều khiển tín hiệu giao thông và các thiết bị trường (field devices).
Lớp xử lý và lưu trữ dữ liệu (Data Processing & Storage Layer).
Lớp ứng dụng (Application Layer) cung cấp các nghiệp vụ giám sát, điều khiển, quản lý duy tu, báo cáo, cảnh báo.
Lớp trình diễn (Presentation Layer) cung cấp giao diện người dùng trên nền Web và ứng dụng di động.
Hệ thống cổng giao tiếp lập trình ứng dụng mở (Open API) phục vụ tích hợp với các hệ thống bên ngoài.
3. Đối tượng quản lý
Tủ điều khiển chiếu sáng kết nối về trung tâm; đèn LED thông minh kết nối từng đèn (node-level).
Tủ điều khiển tín hiệu giao thông; đèn tín hiệu giao thông và các thiết bị phụ trợ (camera, cảm biến mật độ phương tiện, biển báo điện tử nếu có).
Trụ đèn, cần đèn, cáp cấp nguồn, các thiết bị phụ trợ thuộc tài sản hạ tầng kỹ thuật.
Người dùng hệ thống được phân quyền theo nhóm, khu vực quản lý.
4. Tài liệu tham chiếu
IEC 62541: OPC Unified Architecture (toàn bộ các phần áp dụng được).
ISO/IEC 27001: Hệ thống quản lý an toàn thông tin.
ISO/IEC 25010: Mô hình chất lượng phần mềm.
QCVN 41:2019/BGTVT — Quy chuẩn kỹ thuật quốc gia về báo hiệu đường bộ.
Luật An toàn thông tin mạng số 86/2015/QH13 và các văn bản hướng dẫn thi hành.
Nghị định 85/2016/NĐ-CP về bảo đảm an toàn hệ thống thông tin theo cấp độ.
II. KIẾN TRÚC TỔNG THỂ HỆ THỐNG
1. Mô hình kiến trúc phân lớp
Phần mềm điều khiển trung tâm phải được thiết kế theo mô hình kiến trúc phân lớp, hướng dịch vụ (Service-Oriented Architecture – SOA) hoặc kiến trúc microservices, gồm bốn lớp chính:
Lớp thiết bị trường (Field Layer): các tủ điều khiển chiếu sáng, tủ tín hiệu giao thông, cảm biến, đèn LED node-level, đồng hồ đo điện đa năng, photocell, GPS, modem GPRS/3G/4G/5G.
Lớp dịch vụ kết nối (Connectivity Layer): máy chủ OPC-UA Server, MQTT Broker, REST/HTTPS Gateway thực hiện thu thập – đệm – chuẩn hóa dữ liệu từ các giao thức không đồng nhất về một định dạng thống nhất.
Lớp xử lý nghiệp vụ (Application Layer): các microservice xử lý logic điều khiển, lập lịch, cảnh báo, báo cáo, quản lý duy tu, GIS, phân quyền; cơ sở dữ liệu quan hệ (RDBMS), cơ sở dữ liệu chuỗi thời gian (Time-Series DB) và cơ sở dữ liệu không gian địa lý (Spatial DB).
Lớp trình diễn (Presentation Layer): ứng dụng Web đáp ứng (responsive), ứng dụng di động iOS/Android, dashboard điều hành, cổng tích hợp Open API.
2. Yêu cầu về tính mở và khả năng mở rộng
Toàn bộ phần mềm phải được xây dựng trên các nền tảng, công nghệ, giao thức mở (open standard, open protocol), không phụ thuộc vào bất kỳ một nhà sản xuất phần cứng cụ thể nào.
Cho phép tích hợp đồng thời thiết bị của nhiều hãng khác nhau thông qua chuẩn OPC-UA mà không phải sửa đổi mã nguồn lõi của phần mềm.
Kiến trúc có khả năng mở rộng theo chiều ngang (horizontal scaling) đáp ứng tăng trưởng số lượng tủ điều khiển và đèn LED tối thiểu gấp 10 lần quy mô triển khai ban đầu mà không thay đổi kiến trúc.
Hỗ trợ triển khai trên hạ tầng vật lý (on-premise), hạ tầng ảo hóa (VMware, Hyper-V, KVM) và hạ tầng đám mây (private cloud / public cloud) thông qua công nghệ container (Docker) và điều phối container (Kubernetes).
3. Yêu cầu về dự phòng và tính sẵn sàng cao
Hỗ trợ cấu hình cụm máy chủ (cluster) hoạt động theo mô hình Active-Active hoặc Active-Standby.
Cơ sở dữ liệu hỗ trợ replication thời gian thực và tự động chuyển đổi dự phòng (automatic failover) khi máy chủ chính gặp sự cố.
Lớp kết nối OPC-UA hỗ trợ Redundancy (Cold/Warm/Hot) và Fault Tolerance theo tiêu chuẩn IEC 62541-4.
Bảo đảm chỉ số tính sẵn sàng tối thiểu 99,9% (uptime ≥ 8.760 giờ/năm × 99,9%).
III. TIÊU CHUẨN CHUNG CỦA HỆ THỐNG
Để thuận lợi cho việc triển khai phần mềm, đấu nối các thiết bị phần cứng của nhiều nhà cung cấp khác nhau, tránh trường hợp độc quyền công nghệ, các thành phần trong hệ thống phải tuân theo các tiêu chuẩn chung như sau:
a. Tiêu chuẩn về kết nối giữa Tủ điều khiển với Lớp dịch vụ kết nối của Trung tâm điều khiển
Mục tiêu: thiết bị của các hãng, các công nghệ khác nhau có thể thiết lập kết nối dễ dàng, nhanh chóng, bền vững và bảo mật với Lớp dịch vụ kết nối của Trung tâm điều khiển.
Tiêu chuẩn đề nghị: sử dụng giao thức mở, cụ thể là chuẩn giao thức OPC-UA. Chuẩn này cho phép thiết bị của các hãng sản xuất khác nhau kết nối chung và an toàn; đồng thời đây là chuẩn được sử dụng rộng rãi trong các hệ thống giám sát và điều khiển công nghiệp, được phát triển và hỗ trợ bởi nhiều hãng công nghệ lớn hàng đầu thế giới.
Giao thức tiêu chuẩn quốc tế OPC-UA (Open Platform Communications Unified Architecture) đã được Ủy ban Kỹ thuật Điện Quốc tế (IEC) công nhận thành chuẩn quốc tế IEC 62541. OPC-UA được thiết kế là nền tảng chung kết nối nhiều hệ thống khác nhau (IoT, công nghiệp 4.0, M2M), đặc biệt thích hợp với các hệ thống điều khiển và giám sát tự động có số lượng thiết bị rất lớn. Đây là một trong những bước quan trọng tiếp cận nền tảng công nghệ 4.0, IoT và M2M hiện đại trong xây dựng đô thị thông minh.
OPC-UA tiêu chuẩn quốc tế có tính bảo mật cao, sử dụng nhiều lớp bảo mật, hỗ trợ Fault Tolerance và Redundancy:
X.509 Certificate: Client phải có Certificate do Server cung cấp thì mới truy cập được dữ liệu.
Chữ ký số (Digital Signature): mỗi thông điệp truyền đi đều được kèm theo chữ ký; Server đối chiếu với chữ ký đăng ký trước để chấp nhận hoặc từ chối thông điệp.
Mã hóa: mỗi thông điệp truyền đi đều được mã hóa.
User/Password: đăng nhập truyền thống để vào Server.
Quyền truy cập đến từng point (tag): mỗi biến trên Server đều có lựa chọn cho phép chỉ đọc, đọc/ghi hay không truy cập.
Hỗ trợ kỹ thuật Heartbeat hai chiều giữa Client và Server để kiểm tra tình trạng hoạt động.
Hỗ trợ chức năng Automatic Backfilling dữ liệu: khi kết nối giữa Client và Server bị gián đoạn, dữ liệu trong thời gian này được Buffer trên Server; ngay sau khi kết nối được khôi phục, dữ liệu Buffer này được gửi ngay cho Client.
Giá trị của OPC-UA trong cách mạng công nghiệp 4.0 ở chỗ đây là một nền tảng giao tiếp tiêu chuẩn quốc tế cho phép kết nối nhiều hệ thống khác nhau, tiết kiệm chi phí đầu tư và có nhiều tùy chọn hơn cho người dùng. Các nhà sản xuất phần cứng chỉ cần cung cấp một máy chủ OPC duy nhất để hệ thống thiết bị của họ kết nối chung với hệ thống thiết bị của các nhà sản xuất khác, giám sát và điều khiển trên cùng một giao diện phần mềm.
OPC-UA là tiêu chuẩn giao tiếp tương thích cho việc trao đổi dữ liệu an toàn và tin cậy trong hệ thống tự động hóa công nghiệp, các hệ thống giao tiếp và điều khiển thông minh; là nền tảng độc lập, không phụ thuộc vào việc phát triển phần cứng từ bất kỳ nhà sản xuất nào.
b. Tiêu chuẩn bảo mật người dùng
Mục tiêu: bảo đảm an toàn thông tin tài khoản người dùng và các thông tin nhạy cảm khác của hệ thống.
Tiêu chuẩn đề nghị: áp dụng các tiêu chuẩn OpenPGP, SSH v2.0, HTTPS, FTPS, SMTPS, POP3S, DNSSEC, VPN, IPsec, 3DES, AES-256, PKCS #1, SHA-2, RSA-KEM, SAML v2.0, XKMS v2.0, P3P v1.1, PKCS #7 v1.5, RFC 5280, OAuth 2.0, OpenID Connect và các tiêu chuẩn tương đương.
c. Tiêu chuẩn truyền nhận dữ liệu giữa Trung tâm điều khiển và Phần mềm ứng dụng
Mục tiêu: truyền nhận thông tin giữa Trung tâm điều khiển và Phần mềm ứng dụng một cách an toàn, thông suốt và bảo mật qua mạng.
Tiêu chuẩn đề nghị: OpenPGP, SSH v2.0, HTTPS, FTPS, SMTPS, POP3S, DNSSEC, VPN, IPsec, 3DES, AES-256, TLS 1.3, PKCS #1, SHA-2, RSA-KEM, SAML v2.0, XKMS v2.0, P3P v1.1, PKCS #7 v1.5, RFC 5280 và các tiêu chuẩn tương đương.
d. Tiêu chuẩn về dữ liệu
Mục tiêu: lưu trữ dữ liệu tuân theo một quy luật nhất định mà hầu hết các ngôn ngữ lập trình trên các nền tảng khác nhau đều có thể đọc và sử dụng được.
Tiêu chuẩn đề nghị: XML v1.1, ISO/TS 15000:2014, XML Schema v1.1, XSL, UML v2.5, GML v3.3, WMS v1.3.0, XMI v2.4.2, ISO/IEC 11179:2015, JSON RFC 7159, GeoJSON RFC 7946, YAML 1.2, Protocol Buffers và các tiêu chuẩn tương đương.
e. Tiêu chuẩn về Phần mềm ứng dụng
Mục tiêu: tạo ra phần mềm được chuẩn hóa, sử dụng được trên các nền tảng khác nhau.
Tiêu chuẩn đề nghị: HTML 5, XHTML v1.1, CSS3, XLS, WML v2.0, ASCII, TCVN 6909:2001 (Unicode UTF-8 cho tiếng Việt), ECMA 262 (JavaScript), RSS v2.0, JSR 286, WCAG 2.1 (về tiếp cận sử dụng) và các tiêu chuẩn tương đương.
IV. YÊU CẦU VỀ HẠ TẦNG VÀ NỀN TẢNG TRIỂN KHAI
1. Yêu cầu hạ tầng máy chủ tối thiểu
Phần mềm phải có khả năng chạy ổn định trên hạ tầng máy chủ với cấu hình tối thiểu như sau (cấu hình thực tế sẽ được tính toán theo quy mô triển khai và yêu cầu hiệu năng cụ thể):
Thành phần
Máy chủ ứng dụng
Máy chủ CSDL
Máy chủ kết nối OPC-UA
CPU
≥ 8 nhân, 16 luồng, 2.4 GHz trở lên
≥ 16 nhân, 32 luồng, 2.4 GHz trở lên
≥ 8 nhân, 16 luồng, 2.4 GHz trở lên
RAM
≥ 32 GB DDR4 ECC
≥ 64 GB DDR4 ECC
≥ 16 GB DDR4 ECC
Ổ cứng
SSD ≥ 500 GB, RAID 1
SSD NVMe ≥ 2 TB, RAID 10
SSD ≥ 500 GB, RAID 1
Mạng
2 cổng 1 Gbps trở lên
2 cổng 1 Gbps trở lên
2 cổng 1 Gbps trở lên
Nguồn
Nguồn dự phòng kép (Redundant PSU) — bắt buộc cho cả ba loại máy chủ

2. Hệ điều hành và phần mềm nền
Hệ điều hành máy chủ: hỗ trợ các hệ điều hành Linux phổ biến (Ubuntu Server LTS, CentOS Stream, RHEL, Rocky Linux) và/hoặc Windows Server phiên bản còn được hãng hỗ trợ chính thức.
Cơ sở dữ liệu quan hệ: hỗ trợ ít nhất một trong các hệ quản trị phổ biến (PostgreSQL ≥ 14, MySQL ≥ 8.0, Oracle Database, Microsoft SQL Server) — ưu tiên CSDL mã nguồn mở để giảm chi phí bản quyền.
Cơ sở dữ liệu chuỗi thời gian (Time-Series Database) phục vụ lưu trữ dữ liệu giám sát có khối lượng lớn theo thời gian: TimescaleDB, InfluxDB, hoặc tương đương.
Cơ sở dữ liệu không gian địa lý (Spatial Database): PostGIS hoặc tương đương để phục vụ chức năng GIS.
Web Server / Reverse Proxy: Nginx, Apache HTTP Server hoặc tương đương.
Container & điều phối: Docker ≥ 24, Kubernetes ≥ 1.28 hoặc tương đương.
Hệ thống xếp hàng tin nhắn (Message Queue): Apache Kafka, RabbitMQ, hoặc tương đương — phục vụ giao tiếp bất đồng bộ giữa các microservice.
Hệ thống cache: Redis ≥ 7 hoặc tương đương.
3. Yêu cầu mạng và kết nối
Đường truyền tới Trung tâm điều khiển có băng thông tối thiểu 100 Mbps, đồng thời có đường truyền dự phòng từ nhà mạng khác (dual-homed).
Đường truyền không dây tới các tủ điều khiển hỗ trợ đồng thời các công nghệ GPRS/3G/4G/5G; cho phép cấu hình SIM dự phòng (multi-SIM) tự động chuyển mạng khi nhà mạng chính mất sóng.
Hỗ trợ APN riêng (Private APN) để cách ly luồng dữ liệu vận hành với mạng Internet công cộng.
Toàn bộ kết nối từ thiết bị trường về Trung tâm phải đi qua kênh VPN/IPsec mã hóa đầu cuối.
V. TIÊU CHUẨN VỀ PHẦN MỀM ĐIỀU KHIỂN HỆ THỐNG CHIẾU SÁNG ĐÔ THỊ
Phần mềm điều khiển tại trung tâm kết nối với hệ thống các tủ điều khiển chiếu sáng đô thị thông qua đường truyền không dây GPRS/3G/4G/5G trên nền tảng giao tiếp tiêu chuẩn OPC-UA. Nền tảng giao tiếp này là giao thức mở, cho phép hỗ trợ kết nối nhiều thiết bị điều khiển khác nhau, từ nhiều nhà sản xuất khác nhau. Phần mềm có sự khác biệt với các giải pháp khác trên thị trường: hiện nay, các phần mềm trên thị trường sử dụng giao thức kết nối "riêng" do đơn vị sản xuất phần cứng thiết bị tự phát triển, phục vụ cho việc giám sát và điều khiển duy nhất cho riêng đơn vị sản xuất phần cứng đó. Việc đó dẫn đến tình trạng độc quyền, lệ thuộc công nghệ vào một đơn vị duy nhất, làm tăng chi phí duy trì, bảo dưỡng do không có sự cạnh tranh giữa các nhà sản xuất.
Bảo mật đường truyền: đường truyền dữ liệu giữa trung tâm điều khiển và tủ được bảo mật bằng cách thiết lập mật mã đường truyền giữa trung tâm điều khiển và tủ. Tất cả gói tin từ trung tâm truyền đến tủ đều có chứa thông tin mật mã đường truyền. Mật mã được cài đặt khác nhau cho từng tủ điều khiển và do người có quyền hạn thiết lập. Gói tin truyền đến tủ đúng mật mã thì tủ mới nhận, giải mã gói tin và thực thi lệnh gửi từ trung tâm.
Bảo mật máy chủ (Server) và người dùng (Client): được bảo mật cao, tăng cường qua nhiều lớp.
Phần mềm được thực hiện trên nền Web, cài đặt và triển khai hoàn toàn trên máy chủ. Người dùng không phải cài đặt phần mềm, chỉ cần mở trình duyệt và truy cập vào Web là có thể giám sát và điều khiển hệ thống chiếu sáng công cộng. Toàn bộ mã nguồn nằm tại máy chủ giúp giảm thiểu khả năng bị rò rỉ, phòng tránh việc đột nhập trái phép vào hệ thống.
Toàn bộ hệ thống máy chủ được bảo vệ phía sau VPN (Virtual Private Network – Mạng riêng ảo). Tất cả các kết nối từ người dùng vào máy chủ phải được thực hiện thông qua VPN, ngăn chặn các truy cập bất hợp pháp. Dữ liệu khi truyền trên Internet được mã hóa bằng giao thức HTTPS giúp bảo vệ người dùng khỏi bị nghe lén cũng như can thiệp trái phép vào dữ liệu.
Hệ thống phân quyền truy cập rất chi tiết cho từng nhóm, từng khu quản lý và từng người dùng. Bổ sung định danh người dùng bằng ứng dụng bảo mật của Google (Google Authenticator) hoặc cơ chế xác thực hai yếu tố (2FA) tương đương.
Open API: Phần mềm được xây dựng theo hướng Giao tiếp lập trình ứng dụng mở (Open API) — giao tiếp lập trình ứng dụng công khai cung cấp cho các nhà phát triển các truy cập bằng chương trình tới phần mềm hoặc dịch vụ Web. Phần mềm quản lý hạ tầng trong lĩnh vực chiếu sáng đô thị đã xây dựng các tính năng cơ bản, cần thiết, các tag (nhãn) chức năng và dữ liệu chiếu sáng đều được định nghĩa rõ ràng, đầy đủ, công khai, đảm bảo nhiều nhà sản xuất với thiết bị theo công nghệ khác nhau đều có thể kết nối vào phần mềm thông qua cơ chế Open API.
VI. CÁC TÍNH NĂNG CHÍNH CỦA PHẦN MỀM ĐIỀU KHIỂN TRUNG TÂM
Bảng tổng hợp các tính năng nghiệp vụ bắt buộc của phần mềm điều khiển trung tâm:
BẢNG TỔNG HỢP TÍNH NĂNG CỦA PHẦN MỀM ĐIỀU KHIỂN TRUNG TÂM
A
ĐIỀU KHIỂN VÀ GIÁM SÁT TỦ CHIẾU SÁNG KẾT NỐI TRUNG TÂM
STT
Tính năng
Ghi chú
1
Hiển thị danh sách tủ cùng trạng thái trong phạm vi 3km tính từ trung tâm bản đồ theo thời gian thực

2
Hiển thị danh sách tủ theo phân cấp Quận/Huyện → Phường/Xã → Đường

3
Thống kê số lượng tủ theo từng trạng thái

4
Liệt kê danh sách tủ theo từng trạng thái

5
Khai báo tủ kết nối trung tâm mới

6
Xem, chỉnh sửa thông tin chi tiết tủ kết nối trung tâm

7
Tìm kiếm nhanh tủ theo tên, mã, phường/xã, quận/huyện, đường

8
Xem nhanh trạng thái của tủ

9
Cập nhật thời gian tắt, mở, tiết giảm đèn trên từng tủ

10
Đồng bộ thời gian thực trên từng tủ

11
Cập nhật các thông số về ngưỡng cảnh báo lỗi

12
Theo dõi các chỉ số về điện: tần số, dòng rò, cos phi… trên từng tủ theo thời gian thực

13
Xem biểu đồ dòng điện theo thời gian thực

14
Xem biểu đồ điện áp theo thời gian thực

15
Xem biểu đồ tiêu thụ điện trong ngày, và trong 30 ngày gần nhất

16
Cập nhật nhóm cho đèn

17
Theo dõi trạng thái của đèn

18
Cập nhật thông số tiết giảm đèn theo 5 cấp độ

19
Đồng bộ thời gian thực cho toàn bộ tủ kết nối

20
Đồng bộ thời gian tắt, mở, tiết giảm đèn theo từng khu vực được định nghĩa trước

21
Theo dõi biểu đồ LUX để lấy giá trị tham khảo về giờ tắt, mở đèn

22
Quản lý người dùng theo các phân cấp và khu vực quản lý

23
Theo dõi nhật ký báo lỗi của thiết bị

24
Theo dõi nhật ký thao tác của người dùng

25
Theo dõi nhật ký đồng bộ thời gian tắt, mở, tiết giảm

26
Cảnh báo lỗi mất kết nối, tắt tủ qua Viber, Zalo, Email, SMS

27
Vẽ biểu đồ về công suất tiêu thụ điện của tủ theo thời gian được chọn

28
Thiết lập API mở để giao tiếp với các thiết bị khác nhau

B
ĐIỀU KHIỂN VÀ GIÁM SÁT ĐÈN LED KẾT NỐI VỀ TRUNG TÂM
29
Khai báo đèn kết nối về trung tâm mới

30
Thống kê số lượng đèn theo từng trạng thái

31
Liệt kê danh sách đèn theo từng trạng thái

32
Hiển thị đèn với trạng thái theo từng tủ trên bản đồ

33
Xem, chỉnh sửa thông tin chi tiết về đèn

C
QUẢN LÝ DUY TU CÁC THIẾT BỊ CHIẾU SÁNG CÔNG CỘNG
34
Báo cáo các thiết bị đến niên hạn trong tuần tới, tháng tới, quý tới…

35
Lập lịch duy tu

36
Tìm kiếm, thêm, xóa, sửa thông tin tủ điều khiển

37
Xem lịch sử toàn bộ quá trình duy tu của tủ điều khiển

38
Xem, thêm, xóa, sửa danh sách thiết bị và lịch sử toàn bộ quá trình duy tu các thiết bị của tủ điều khiển

39
Duy tu (thay thế, sửa chữa) thiết bị tủ điều khiển đến niên hạn hoặc bị hư

40
Tìm kiếm, thêm, xóa, sửa thông tin đèn

41
Xem lịch sử toàn bộ quá trình duy tu của đèn

42
Xem, thêm, xóa, sửa danh sách thiết bị và lịch sử toàn bộ quá trình duy tu các thiết bị của đèn

43
Duy tu (thay thế, sửa chữa) thiết bị đèn đến niên hạn hoặc bị hư

44
Tìm kiếm, thêm, xóa, sửa thông tin trụ đèn

45
Xem lịch sử toàn bộ quá trình duy tu của trụ đèn

46
Xem, thêm, xóa, sửa danh sách thiết bị và lịch sử toàn bộ quá trình duy tu các thiết bị của trụ đèn

47
Duy tu (thay thế, sửa chữa) thiết bị trụ đèn đến niên hạn hoặc bị hư

48
Tìm kiếm, thêm, xóa, sửa thông tin cần đèn

49
Xem lịch sử toàn bộ quá trình duy tu của cần đèn

50
Xem, thêm, xóa, sửa danh sách thiết bị và lịch sử toàn bộ quá trình duy tu các thiết bị của cần đèn

51
Duy tu (thay thế, sửa chữa) thiết bị cần đèn đến niên hạn hoặc bị hư

52
Tìm kiếm, thêm, xóa, sửa thông tin cáp

53
Xem lịch sử toàn bộ quá trình duy tu của cáp

54
Xem, thêm, xóa, sửa danh sách thiết bị và lịch sử toàn bộ quá trình duy tu các thiết bị của cáp

55
Duy tu (thay thế, sửa chữa) thiết bị cáp đến niên hạn hoặc bị hư

56
Tìm kiếm, thêm, xóa, sửa mẫu tủ điều khiển

57
Tìm kiếm, thêm, xóa, sửa mẫu đèn

58
Tìm kiếm, thêm, xóa, sửa mẫu trụ đèn

59
Tìm kiếm, thêm, xóa, sửa mẫu cần đèn

60
Quản lý các dữ liệu danh mục (tìm kiếm, thêm, xóa, sửa) như: loại tủ, loại đèn, nguồn gốc, nhà sản xuất…

D
SỐ HÓA DỮ LIỆU TRÊN NỀN BẢN ĐỒ GIS
61
Thu thập và tích hợp dữ liệu của tất cả đối tượng tủ điều khiển, đèn, trụ, cần, cáp

62
Xử lý, phân tích và tổ chức lưu trữ thành cơ sở dữ liệu chuyên dụng

63
Phân phối và cung cấp cho các hệ thống giám sát điều khiển

64
Xuất thông tin ở nhiều dạng khác nhau: Excel, XML, JSON… để phục vụ công tác kiểm tra kiểm soát của các phòng ban khi có nhu cầu


VII. YÊU CẦU TÍCH HỢP MODULE ĐIỀU KHIỂN TÍN HIỆU GIAO THÔNG
Phần mềm điều khiển trung tâm phải có module mở rộng phục vụ giám sát, điều khiển hệ thống đèn tín hiệu giao thông trong phạm vi gói thầu, hoạt động trên cùng một nền tảng kiến trúc và cùng một tài khoản người dùng (Single Sign-On).
1. Giám sát thời gian thực
Hiển thị toàn bộ tủ tín hiệu giao thông trên bản đồ GIS với các trạng thái: hoạt động bình thường, đèn vàng nhấp nháy, mất kết nối, mất nguồn, lỗi cảm biến, lỗi đèn cháy.
Theo dõi trạng thái từng pha đèn (đỏ, vàng, xanh, mũi tên) cho từng hướng tại nút giao theo thời gian thực với độ trễ ≤ 2 giây.
Theo dõi mật độ phương tiện qua nút giao (nếu có cảm biến vòng từ, camera AI hoặc radar).
2. Điều khiển và lập lịch
Cập nhật, đồng bộ pha đèn (chu kỳ, thời lượng từng pha, thời gian xanh-vàng-đỏ) cho từng tủ tín hiệu hoặc theo nhóm tủ thuộc cùng một trục đường (làn sóng xanh).
Thiết lập lịch hoạt động khác nhau theo khung giờ trong ngày, theo ngày trong tuần, theo ngày lễ — hỗ trợ kế hoạch đặc biệt cho các sự kiện.
Chế độ điều khiển bằng tay từ trung tâm: chuyển sang đèn vàng nhấp nháy, tắt đèn, chuyển pha thủ công khi có yêu cầu của lực lượng cảnh sát giao thông.
Hỗ trợ chế độ điều khiển thích ứng (adaptive control) dựa trên dữ liệu mật độ phương tiện thực tế khi có cảm biến.
3. Cảnh báo và bảo trì
Cảnh báo tự động khi có sự cố: mất kết nối, mất nguồn, đèn cháy, xung đột pha đèn (đỏ-đỏ hoặc xanh-xanh trên các hướng xung đột), lỗi bộ đếm thời gian.
Quản lý duy tu, bảo dưỡng định kỳ tủ điều khiển tín hiệu giao thông, đèn tín hiệu, cột tín hiệu, cảm biến, camera tương tự như module chiếu sáng.
Lưu trữ nhật ký vận hành tối thiểu 12 tháng phục vụ công tác hậu kiểm khi xảy ra tai nạn giao thông tại nút.
4. An toàn vận hành
Kiểm tra logic xung đột pha trước khi áp dụng cấu hình mới — không cho phép cấu hình gây xung đột pha đèn.
Khi mất kết nối với trung tâm, tủ điều khiển hiện trường tự động chạy theo lịch đã được nạp gần nhất; khi không có lịch hợp lệ, tự động chuyển về chế độ đèn vàng nhấp nháy.
Mọi thao tác điều khiển từ xa đều được ghi nhật ký kèm theo người thực hiện, thời điểm và lý do.
VIII. YÊU CẦU HIỆU NĂNG VÀ ĐỘ TIN CẬY
Tiêu chí
Yêu cầu
Phương pháp kiểm tra
Thời gian phản hồi giao diện Web (P95)
≤ 2 giây
Đo bằng công cụ kiểm thử tải (JMeter, k6)
Độ trễ truyền dữ liệu giám sát từ tủ về Trung tâm
≤ 5 giây trong điều kiện sóng 4G ổn định
So sánh dấu thời gian (timestamp) tại tủ và tại Server
Độ trễ thực thi lệnh điều khiển từ Trung tâm xuống tủ
≤ 3 giây
Kiểm thử end-to-end
Số lượng tủ kết nối đồng thời
≥ 5.000 tủ trên cùng một cluster
Kiểm thử mô phỏng OPC-UA Client
Số lượng người dùng đồng thời
≥ 200 người dùng đồng thời
Kiểm thử tải bằng JMeter/k6
Tính sẵn sàng của hệ thống
≥ 99,9% (uptime)
Đo qua hệ thống monitoring trong 12 tháng
Mục tiêu thời gian phục hồi (RTO)
≤ 30 phút
Kiểm thử kịch bản DR
Mục tiêu mất mát dữ liệu (RPO)
≤ 5 phút
Kiểm thử kịch bản DR
Khả năng lưu trữ dữ liệu lịch sử
≥ 5 năm dữ liệu vận hành, ≥ 12 tháng dữ liệu sự kiện thời gian thực
Kiểm tra dung lượng và chính sách lưu trữ

IX. YÊU CẦU AN TOÀN THÔNG TIN
1. Định danh và phân quyền
Áp dụng cơ chế phân quyền theo vai trò (Role-Based Access Control – RBAC), hỗ trợ phân quyền theo chức năng, theo khu vực địa lý quản lý, theo nhóm thiết bị.
Bắt buộc xác thực đa yếu tố (Multi-Factor Authentication – MFA/2FA) đối với tài khoản quản trị; hỗ trợ Google Authenticator, Microsoft Authenticator, OTP qua SMS/Email.
Hỗ trợ tích hợp với hệ thống xác thực tập trung của đơn vị chủ quản qua giao thức LDAP, Active Directory, SAML 2.0, OpenID Connect (nếu có).
Chính sách mật khẩu: tối thiểu 8 ký tự, có chữ hoa, chữ thường, chữ số, ký tự đặc biệt; bắt buộc thay đổi định kỳ; chống tái sử dụng N mật khẩu gần nhất.
2. Mã hóa dữ liệu
Dữ liệu trên đường truyền (data in transit): mã hóa bằng TLS 1.2 trở lên (ưu tiên TLS 1.3), bộ mật mã (cipher suite) tuân theo khuyến nghị mới nhất của NIST.
Dữ liệu lưu trữ (data at rest): mã hóa cơ sở dữ liệu, file backup, file log nhạy cảm bằng AES-256.
Khóa mã hóa được quản lý tập trung qua Key Management Service (HashiCorp Vault hoặc tương đương), không lưu hard-coded trong mã nguồn hoặc tệp cấu hình.
3. Nhật ký kiểm toán (Audit Log)
Ghi nhật ký toàn bộ thao tác đăng nhập, đăng xuất, thay đổi cấu hình, lệnh điều khiển, tạo/sửa/xóa người dùng — kèm theo địa chỉ IP, trình duyệt, thời điểm.
Nhật ký không cho phép sửa, xóa; được lưu trữ tối thiểu 12 tháng và sao lưu định kỳ.
Cung cấp công cụ tra cứu, xuất nhật ký theo nhiều tiêu chí phục vụ công tác kiểm tra, thanh tra.
4. Phòng chống tấn công
Chống tấn công SQL Injection, XSS, CSRF, Path Traversal, Command Injection theo khuyến nghị OWASP Top 10.
Áp dụng Rate Limiting và CAPTCHA tại các điểm xác thực để chống tấn công vét cạn (brute-force) và DDoS lớp ứng dụng.
Tích hợp Web Application Firewall (WAF) trước lớp ứng dụng Web.
Quét lỗ hổng bảo mật mã nguồn (SAST), thư viện phụ thuộc (SCA), môi trường vận hành (DAST) định kỳ ít nhất 6 tháng/lần.
5. Tuân thủ pháp luật
Tuân thủ Luật An toàn thông tin mạng số 86/2015/QH13, Nghị định 85/2016/NĐ-CP về bảo đảm an toàn hệ thống thông tin theo cấp độ.
Hệ thống được phân loại tối thiểu cấp độ 3 theo Nghị định 85/2016/NĐ-CP và áp dụng các biện pháp tương ứng.
Tuân thủ các quy định về bảo vệ dữ liệu cá nhân theo Nghị định 13/2023/NĐ-CP.
X. YÊU CẦU SAO LƯU VÀ KHÔI PHỤC THẢM HỌA
Sao lưu cơ sở dữ liệu: sao lưu đầy đủ (full backup) hằng ngày vào khung giờ thấp tải; sao lưu gia tăng (incremental backup) mỗi 1–4 giờ; lưu trữ tối thiểu 30 ngày dữ liệu sao lưu trực tuyến và 12 tháng sao lưu nguội (offline).
Sao lưu cấu hình hệ thống và mã nguồn ảnh container vào kho lưu trữ tập trung.
Lưu trữ bản sao tại địa điểm vật lý khác (off-site backup) hoặc đám mây để phòng tránh thảm họa cục bộ.
Mã hóa toàn bộ dữ liệu sao lưu bằng AES-256.
Có quy trình kiểm thử khôi phục (Disaster Recovery Drill) định kỳ ít nhất 6 tháng/lần với báo cáo kết quả.
Khả năng khôi phục từng phần: cho phép khôi phục riêng một bảng, một tủ, hoặc một thời điểm cụ thể (Point-in-Time Recovery).
XI. YÊU CẦU GIAO DIỆN NGƯỜI DÙNG
1. Giao diện Web
Thiết kế đáp ứng (Responsive Design): hoạt động tốt trên các kích thước màn hình từ 1024×768 trở lên; tương thích các trình duyệt phổ biến phiên bản hiện hành: Google Chrome, Microsoft Edge, Mozilla Firefox, Safari.
Hỗ trợ song song tiếng Việt và tiếng Anh; chuyển đổi ngôn ngữ trực tiếp trên giao diện không cần đăng nhập lại.
Tuân thủ tiêu chuẩn tiếp cận sử dụng WCAG 2.1 mức AA (hỗ trợ người khuyết tật, hiển thị tương phản tốt, hỗ trợ phím tắt).
Bảng điều khiển (Dashboard) tổng quan có thể tùy biến theo người dùng: kéo-thả widget, thêm/bớt biểu đồ, lưu trạng thái cá nhân.
2. Bản đồ GIS tích hợp
Hiển thị toàn bộ tài sản (tủ điều khiển, đèn, trụ, cần, cáp, tủ tín hiệu giao thông) trên nền bản đồ GIS.
Hỗ trợ nhiều lớp bản đồ nền: bản đồ vệ tinh, bản đồ đường phố, bản đồ địa hình; cho phép cấu hình nguồn bản đồ (Google Maps, OpenStreetMap, bản đồ nội bộ).
Hỗ trợ tìm kiếm địa chỉ, chuyển vùng nhanh, đo khoảng cách, đo diện tích, lọc theo khu vực hành chính.
Cho phép phóng to, thu nhỏ tới mức tối đa nhìn rõ từng đèn riêng lẻ; gom cụm (clustering) tự động khi mức zoom xa.
Cho phép xuất bản đồ thành ảnh, PDF cho công tác báo cáo.
3. Biểu đồ và báo cáo
Cung cấp các biểu đồ thời gian thực: dòng điện, điện áp, công suất, mật độ phương tiện…
Cho phép kéo dài/co lại trục thời gian, so sánh nhiều thiết bị trên cùng một biểu đồ, xuất biểu đồ ra ảnh PNG/SVG.
Trình tạo báo cáo có sẵn các mẫu báo cáo phổ biến (báo cáo ngày, tuần, tháng, quý, năm); cho phép tùy biến cột, lọc, tổng hợp.
Xuất báo cáo định dạng PDF, Excel, CSV, JSON, XML.
XII. YÊU CẦU ỨNG DỤNG DI ĐỘNG
Ứng dụng di động hỗ trợ song song hai nền tảng iOS (≥ iOS 14) và Android (≥ Android 9.0).
Sử dụng cùng tài khoản với phần mềm Web; hỗ trợ đăng nhập sinh trắc học (Face ID, Touch ID, vân tay) sau lần đăng nhập đầu tiên.
Tính năng tối thiểu: giám sát trạng thái tủ và đèn theo thời gian thực, nhận cảnh báo đẩy (push notification) khi có sự cố, thực hiện các lệnh điều khiển cơ bản (bật/tắt/tiết giảm theo phân quyền).
Tính năng dành cho cán bộ duy tu hiện trường: quét mã QR/Barcode trên tủ/đèn để truy xuất nhanh thông tin và lịch sử bảo dưỡng; chụp ảnh hiện trường gắn vào hồ sơ duy tu; ghi nhận tọa độ GPS thiết bị.
Hỗ trợ chế độ ngoại tuyến (offline mode) đối với công tác duy tu hiện trường: cho phép cập nhật dữ liệu khi không có sóng và đồng bộ tự động khi có kết nối trở lại.
XIII. YÊU CẦU TÍCH HỢP VÀ OPEN API
1. Cổng tích hợp Open API
Cung cấp bộ API RESTful trên nền HTTPS với định dạng dữ liệu JSON theo chuẩn RFC 7159; ngoài ra hỗ trợ định dạng XML khi có yêu cầu.
Áp dụng cơ chế xác thực OAuth 2.0 / API Key kèm hạn ngạch (rate limit) và phân quyền theo phạm vi (scope) cho từng ứng dụng tích hợp.
Cung cấp tài liệu API trực tuyến tự động sinh ra theo chuẩn OpenAPI 3.0 (Swagger) — cho phép thử nghiệm gọi API ngay trên giao diện tài liệu.
Hỗ trợ Webhook để chủ động đẩy sự kiện (cảnh báo lỗi, thay đổi trạng thái) sang hệ thống bên thứ ba theo thời gian thực.
2. Tích hợp với các hệ thống đô thị thông minh
Cho phép tích hợp với Trung tâm điều hành thông minh (IOC) của thành phố qua chuẩn dữ liệu mở.
Cho phép kết nối, trao đổi dữ liệu với hệ thống GIS của Sở/Ngành thông qua các chuẩn WMS, WFS, WMTS, GeoJSON.
Sẵn sàng tích hợp với hệ thống quản lý giao thông thông minh (ITS), camera giám sát, hệ thống phát hiện vi phạm tự động khi có yêu cầu.
3. Xuất – nhập dữ liệu
Cho phép nhập dữ liệu hàng loạt từ tệp Excel, CSV với mẫu chuẩn được cung cấp sẵn; có cơ chế kiểm tra hợp lệ trước khi ghi vào hệ thống.
Cho phép xuất dữ liệu hàng loạt ra Excel, CSV, JSON, XML, GeoJSON.
Hỗ trợ xuất dữ liệu định kỳ tự động (scheduled export) tới các thư mục FTP/SFTP, kho lưu trữ S3 hoặc gửi qua email.
XIV. BẢNG API KẾT NỐI TỦ ĐIỀU KHIỂN CHIẾU SÁNG ĐÔ THỊ
Bảng dưới đây mô tả danh mục Tag (biến) bắt buộc trên Server OPC-UA của tủ điều khiển chiếu sáng đô thị. Đây là giao tiếp công khai để các nhà sản xuất phần cứng tủ điều khiển có thể tự triển khai và kết nối vào phần mềm Trung tâm điều khiển.
Tên Tag
Kiểu dữ liệu
Mức truy cập
Mô tả
1_00_SEC_SP
Word
R/W
Thông số cài đặt giá trị giây
1_01_MIN_SP
Word
R/W
Thông số cài đặt giá trị phút
1_02_HOUR_SP
Word
R/W
Thông số cài đặt giá trị giờ
1_03_DOW_SP
Word
R/W
Thông số cài đặt giá trị thứ: 0: Chủ nhật, 1: Thứ hai, 2: Thứ ba, 3: Thứ tư, 4: Thứ năm, 5: Thứ sáu, 6: Thứ bảy
1_04_DAY_SP
Word
R/W
Thông số cài đặt giá trị ngày
1_05_MONT_SP
Word
R/W
Thông số cài đặt giá trị tháng
1_06_YEAR_SP
Word
R/W
Thông số cài đặt giá trị năm
1_07_SETTIME
Word
R/W
= 1: cài đặt các giá trị thời gian vào bộ điều khiển
1_08_HOUR_C1_ON
Word
R/W
Giờ mở đèn nhánh C1
1_09_MIN_C1_ON
Word
R/W
Phút mở đèn nhánh C1
1_10_HOUR_C1_OFF
Word
R/W
Giờ tắt đèn nhánh C1
1_11_MIN_C1_OFF
Word
R/W
Phút tắt đèn nhánh C1
1_12_HOUR_C2_ON
Word
R/W
Giờ mở đèn nhánh C2
1_13_MIN_C2_ON
Word
R/W
Phút mở đèn nhánh C2
1_14_HOUR_C2_OFF
Word
R/W
Giờ tắt đèn nhánh C2
1_15_MIN_C2_OFF
Word
R/W
Phút tắt đèn nhánh C2
1_16_HOUR_DIM_ON
Word
R/W
Giờ bật tiết giảm C3
1_17_MIN_DIM_ON
Word
R/W
Phút bật tiết giảm C3
1_18_HOUR_DIM_OFF
Word
R/W
Giờ tắt tiết giảm C3
1_19_MIN_DIM_OFF
Word
R/W
Phút tắt tiết giảm C3
1_20_SETTIME_LAMP
Word
R/W
= 1: cài đặt các thông số thời gian tắt/mở/tiết giảm đèn vào bộ điều khiển
1_21_SETUP
Word
R/W
= 11: chuyển sang chế độ cài đặt
1_22_SEC_PV
Word
RO
Thời gian thực – giây của bộ điều khiển
1_23_MIN_PV
Word
RO
Thời gian thực – phút của bộ điều khiển
1_24_HOUR_PV
Word
RO
Thời gian thực – giờ của bộ điều khiển
1_25_DOW_PV
Word
RO
Thời gian thực – thứ của bộ điều khiển
1_26_DAY_PV
Word
RO
Thời gian thực – ngày của bộ điều khiển
1_27_MONT_PV
Word
RO
Thời gian thực – tháng của bộ điều khiển
1_28_YEAR_PV
Word
RO
Thời gian thực – năm của bộ điều khiển
1_29_ERR_CODE
Word
RO
Mã lỗi bộ điều khiển
1_291_LUX_LEVEL1
Word
R/W
Ngưỡng độ lux thời điểm chuyển trạng thái từ chiều sang tối
1_292_LUX_LEVEL2
Word
R/W
Ngưỡng độ lux thời điểm chuyển trạng thái từ đêm sang sáng
1_293_LUX_VALUE
Word
RO
Giá trị độ lux
1_30_CONTACTOR_C1
Boolean
RO
Ngõ ra điều khiển C1 của bộ điều khiển (1: ON, 0: OFF)
1_31_CONTACTOR_C2
Boolean
RO
Ngõ ra điều khiển C2 của bộ điều khiển (1: ON, 0: OFF)
1_32_CONTACTOR_C3
Boolean
RO
Ngõ ra điều khiển tiết giảm C3 của bộ điều khiển (1: ON, 0: OFF)
1_33_MAN_CTR
Boolean
R/W
Tín hiệu điều khiển bật đèn từ trung tâm (1: ON, 0: OFF)
1_34_ERR_VOLT
Boolean
RO
Lỗi điện áp (1: có lỗi, 0: không lỗi)
1_35_SW_HAND_STATUS
Boolean
RO
Trạng thái tín hiệu công tắc điều khiển tay tại tủ (1: ON, 0: OFF)
1_36_PHOTOCELL_1
Boolean
RO
Tín hiệu photocell 1 (chiều sang tối)
1_37_PHOTOCELL_2
Boolean
RO
Tín hiệu photocell 2 (đêm sang sáng)
1_38_ERR_TIME
Boolean
R/W
Lỗi thời gian thực bộ điều khiển (1: có lỗi, 0: không lỗi)
1_39_MODE1
Boolean
R/W
Chế độ 1: chế độ điều khiển không tách nhánh C1, C2
1_40_MODE2
Boolean
R/W
Chế độ 2: chế độ điều khiển riêng từng nhánh C1, C2
1_41_EN_C1
Boolean
R/W
Cho phép nhánh C1 hoạt động (chỉ có tác dụng ở chế độ 2)
1_42_EN_C2
Boolean
R/W
Cho phép nhánh C2 hoạt động (chỉ có tác dụng ở chế độ 2)
1_43_ERR_CONNECT_ZEN
Boolean
RO
Lỗi mất kết nối bộ điều khiển (1: có lỗi, 0: không lỗi)
1_44_PHOTOCEL_SIGNAL
Boolean
RO
Tín hiệu báo trời tối (ON khi độ lux nhỏ hơn 1_291_LUX_LEVEL1, OFF khi độ lux lớn hơn 1_292_LUX_LEVEL2)
1_45_EN_DO
Boolean
R/W
Cho phép giám sát tín hiệu cửa tủ (0: không cho phép, 1: cho phép)
1_46_ST_DO
Boolean
RO
Trạng thái cửa tủ (0: đóng, 1: mở)
1_47_ERR_PW_OFF_PANEL
Boolean
RO
Lỗi mất điện lưới tủ (1: có lỗi, 0: không lỗi)
1_48_ERR_PW_OFF_CTT
Boolean
RO
Lỗi mất điện áp đầu ra contactor (1: có lỗi, 0: không lỗi)
1_49_INFO_SIM
Word
RO
Thông tin đơn vị cung cấp đường truyền (1: Viettel, 2: Mobifone, 3: Vinaphone)
1_50_INFO_ST_SIM
Word
RO
Dịch vụ đường truyền đang sử dụng của tủ điều khiển (2: GPRS, 3: 3G, 4: 4G, 5: 5G)
1_51_INFO_WAVE
Word
RO
Cường độ sóng đường truyền tại vị trí tủ điều khiển (giá trị từ 0 đến 31)
1_52_PERCENT_BATTERY
Word
RO
Phần trăm ắc quy/pin còn lại (Option)
1_53_GPS
String
RO
Tọa độ GPS của tủ điều khiển
1_54_SERI_SIM
String
RO
Số seri SIM sử dụng tại tủ điều khiển
2_00_V1N
Float
RO
Điện áp pha 1 (V)
2_01_V2N
Float
RO
Điện áp pha 2 (V)
2_02_V3N
Float
RO
Điện áp pha 3 (V)
2_03_VLN
Float
RO
Điện áp pha trung bình (V)
2_04_V12
Float
RO
Điện áp dây 1-2 (V)
2_05_V23
Float
RO
Điện áp dây 2-3 (V)
2_06_V31
Float
RO
Điện áp dây 3-1 (V)
2_07_VLL
Float
RO
Điện áp dây trung bình (V)
2_08_I1
Float
RO
Dòng điện pha 1 (A)
2_09_I2
Float
RO
Dòng điện pha 2 (A)
2_10_I3
Float
RO
Dòng điện pha 3 (A)
2_11_I
Float
RO
Dòng điện pha trung bình (A)
2_12_P1
Float
RO
Công suất tiêu thụ pha 1 (kW)
2_13_P2
Float
RO
Công suất tiêu thụ pha 2 (kW)
2_14_P3
Float
RO
Công suất tiêu thụ pha 3 (kW)
2_15_S1
Float
RO
Công suất biểu kiến pha 1 (kVA)
2_16_S2
Float
RO
Công suất biểu kiến pha 2 (kVA)
2_17_S3
Float
RO
Công suất biểu kiến pha 3 (kVA)
2_18_Q1
Float
RO
Công suất phản kháng pha 1 (kVAr)
2_19_Q2
Float
RO
Công suất phản kháng pha 2 (kVAr)
2_20_Q3
Float
RO
Công suất phản kháng pha 3 (kVAr)
2_21_TOTAL_P
Float
RO
Tổng công suất tiêu thụ (kW)
2_22_TOTAL_S
Float
RO
Tổng công suất biểu kiến (kVA)
2_23_TOTAL_Q
Float
RO
Tổng công suất phản kháng (kVAr)
2_24_PF1
Float
RO
Cosφ pha 1
2_25_PF2
Float
RO
Cosφ pha 2
2_26_PF3
Float
RO
Cosφ pha 3
2_27_PF
Float
RO
Cosφ trung bình
2_28_F
Float
RO
Tần số điện lưới (Hz)
2_29_KWH
Float
RO
Điện năng tiêu thụ (kWh)
2_30_KVAH
Float
RO
kVAh
2_31_KVARH
Float
RO
kVArh
2_32_HI_V
Float
R/W
Ngưỡng cao điện áp pha (V)
2_33_LO_V
Float
R/W
Ngưỡng thấp điện áp pha (V)
2_34_HYS_V
Float
R/W
Độ trễ điện áp pha (V)
2_35_HI_I1
Float
R/W
Ngưỡng cao dòng điện pha 1 (A)
2_36_LO_I1
Float
R/W
Ngưỡng thấp dòng điện pha 1 (A)
2_37_HYS_I1
Float
R/W
Độ trễ dòng điện pha 1 (A)
2_38_HI_I2
Float
R/W
Ngưỡng cao dòng điện pha 2 (A)
2_39_LO_I2
Float
R/W
Ngưỡng thấp dòng điện pha 2 (A)
2_40_HYS_I2
Float
R/W
Độ trễ dòng điện pha 2 (A)
2_41_HI_I3
Float
R/W
Ngưỡng cao dòng điện pha 3 (A)
2_42_LO_I3
Float
R/W
Ngưỡng thấp dòng điện pha 3 (A)
2_43_HYS_I3
Float
R/W
Độ trễ dòng điện pha 3 (A)
2_44_NO_LOAD_I1
Float
R/W
Ngưỡng không tải pha 1 (A)
2_45_NO_LOAD_I2
Float
R/W
Ngưỡng không tải pha 2 (A)
2_46_NO_LOAD_I3
Float
R/W
Ngưỡng không tải pha 3 (A)
2_47_ERR_CODE_PM
Word
RO
Mã lỗi các thông số điện
2_471_HI_I_LEAK
Word
R/W
Ngưỡng cao dòng điện rò (mA)
2_472_I_LEAK
Word
RO
Dòng điện rò của tủ (mA)
2_48_EN_HI_V
Boolean
R/W
Cho phép kiểm tra lỗi điện áp cao
2_49_EN_LO_V
Boolean
R/W
Cho phép kiểm tra lỗi điện áp thấp
2_50_EN_HI_I1
Boolean
R/W
Cho phép kiểm tra dòng điện pha 1 cao
2_51_EN_LO_I1
Boolean
R/W
Cho phép kiểm tra dòng điện pha 1 thấp
2_52_EN_HI_I2
Boolean
R/W
Cho phép kiểm tra dòng điện pha 2 cao
2_53_EN_LO_I2
Boolean
R/W
Cho phép kiểm tra dòng điện pha 2 thấp
2_54_EN_HI_I3
Boolean
R/W
Cho phép kiểm tra dòng điện pha 3 cao
2_55_EN_LO_I3
Boolean
R/W
Cho phép kiểm tra dòng điện pha 3 thấp
2_56_ERR_HI_V1
Boolean
RO
Lỗi quá áp pha 1 (Mã lỗi = 1)
2_57_ERR_LO_V1
Boolean
RO
Lỗi thấp áp pha 1 (Mã lỗi = 2)
2_58_ERR_HI_V2
Boolean
RO
Lỗi quá áp pha 2 (Mã lỗi = 3)
2_59_ERR_LO_V2
Boolean
RO
Lỗi thấp áp pha 2 (Mã lỗi = 4)
2_60_ERR_HI_V3
Boolean
RO
Lỗi quá áp pha 3 (Mã lỗi = 5)
2_61_ERR_LO_V3
Boolean
RO
Lỗi thấp áp pha 3 (Mã lỗi = 6)
2_62_ERR_HI_I1
Boolean
RO
Lỗi quá dòng pha 1 (Mã lỗi = 7)
2_63_ERR_LO_I1
Boolean
RO
Lỗi thấp dòng pha 1 (Mã lỗi = 8)
2_64_ERR_HI_I2
Boolean
RO
Lỗi quá dòng pha 2 (Mã lỗi = 9)
2_65_ERR_LO_I2
Boolean
RO
Lỗi thấp dòng pha 2 (Mã lỗi = 10)
2_66_ERR_HI_I3
Boolean
RO
Lỗi quá dòng pha 3 (Mã lỗi = 11)
2_67_ERR_LO_I3
Boolean
RO
Lỗi thấp dòng pha 3 (Mã lỗi = 12)
2_68_ERR_CONNECT_PM
Boolean
RO
Lỗi mất kết nối đồng hồ đo điện (Mã lỗi = 13)
2_69_SETUP_PM
Boolean
R/W
= 1: cài đặt các thông số báo lỗi thông số điện
2_70_ERR_NO_LOAD_I1
Boolean
RO
Lỗi không tải pha 1 (Mã lỗi = 14)
2_71_ERR_NO_LOAD_I2
Boolean
RO
Lỗi không tải pha 2 (Mã lỗi = 15)
2_72_ERR_NO_LOAD_I3
Boolean
RO
Lỗi không tải pha 3 (Mã lỗi = 16)
2_73_ERR_I_LEAK
Boolean
RO
Lỗi dòng rò cao (Mã lỗi = 17)
NOTCONNECT
Boolean
RO
Tủ điều khiển mất kết nối (1: mất kết nối, 0: có kết nối)
SETTIME
Boolean
R/W
= 1: đồng bộ thời gian thực tủ điều khiển với trung tâm

XV. YÊU CẦU TÀI LIỆU, ĐÀO TẠO, BẢO TRÌ
1. Tài liệu kèm theo phần mềm
Tài liệu thiết kế tổng thể, thiết kế chi tiết hệ thống, thiết kế cơ sở dữ liệu, mô tả kiến trúc kỹ thuật.
Tài liệu cài đặt, triển khai (deployment guide) trên môi trường vận hành, môi trường dự phòng và môi trường kiểm thử.
Tài liệu hướng dẫn vận hành quản trị (system administrator manual): cấu hình, sao lưu, khôi phục, xử lý sự cố.
Tài liệu hướng dẫn sử dụng cho người dùng cuối (user manual) — phân theo từng vai trò người dùng.
Tài liệu mô tả Open API kèm các ví dụ minh họa (theo chuẩn OpenAPI 3.0).
Toàn bộ tài liệu được cung cấp bằng tiếng Việt và lưu hành dưới dạng tệp PDF/DOCX có chữ ký số của đơn vị cung cấp.
2. Đào tạo chuyển giao
Đào tạo cho đội ngũ quản trị hệ thống: cài đặt, vận hành, khắc phục sự cố, sao lưu/phục hồi, bảo trì hệ thống.
Đào tạo cho người dùng cuối phân theo nhóm vai trò: cán bộ giám sát, cán bộ điều khiển, cán bộ duy tu, lãnh đạo.
Đào tạo cho đội ngũ phát triển phần mềm của Chủ đầu tư về Open API và quy trình tích hợp.
Cung cấp video hướng dẫn, môi trường thực hành (sandbox) và bài tập tình huống trong suốt quá trình đào tạo.
3. Bảo hành, bảo trì, hỗ trợ kỹ thuật
Thời gian bảo hành phần mềm tối thiểu 24 tháng kể từ ngày nghiệm thu bàn giao đưa vào sử dụng.
Trong thời gian bảo hành, đơn vị cung cấp chịu trách nhiệm khắc phục mọi lỗi phần mềm không phát sinh chi phí.
Cam kết thời gian phản hồi và khắc phục sự cố theo cấp độ:
Sự cố nghiêm trọng (toàn hệ thống không hoạt động): phản hồi ≤ 30 phút, khắc phục ≤ 4 giờ.
Sự cố lớn (mất một chức năng quan trọng): phản hồi ≤ 1 giờ, khắc phục ≤ 12 giờ.
Sự cố nhỏ (lỗi giao diện, không ảnh hưởng vận hành): phản hồi ≤ 4 giờ, khắc phục ≤ 5 ngày làm việc.
Cung cấp các kênh hỗ trợ: hotline 24/7, email, hệ thống ticket online, hỗ trợ tại chỗ khi cần.
Cung cấp các bản nâng cấp, vá lỗi bảo mật miễn phí trong suốt thời gian bảo hành.
4. Bàn giao mã nguồn
Bàn giao toàn bộ mã nguồn, kịch bản cài đặt (deployment script), tệp cấu hình, ảnh container, tài liệu thiết kế chi tiết cho Chủ đầu tư khi nghiệm thu.
Bảo đảm Chủ đầu tư có quyền sử dụng, sửa đổi, phát triển tiếp mã nguồn cho mục đích nội bộ và mở rộng hệ thống.
XVI. TIÊU CHUẨN ÁP DỤNG
1. Tiêu chuẩn quốc tế
IEC 62541 — OPC Unified Architecture.
ISO/IEC 27001 — Hệ thống quản lý an toàn thông tin.
ISO/IEC 25010 — Mô hình chất lượng phần mềm (Software Quality Model).
ISO/IEC 12207 — Quy trình vòng đời phần mềm.
ISO/IEC 20000 — Quản lý dịch vụ công nghệ thông tin.
ITU-T X.509 — Chứng thực số.
OWASP Application Security Verification Standard (ASVS) phiên bản 4.0 trở lên.
OpenAPI 3.0, GeoJSON RFC 7946, JSON RFC 7159.
2. Tiêu chuẩn Việt Nam
Luật An toàn thông tin mạng số 86/2015/QH13.
Luật Giao dịch điện tử số 20/2023/QH15.
Nghị định 85/2016/NĐ-CP về bảo đảm an toàn hệ thống thông tin theo cấp độ.
Nghị định 13/2023/NĐ-CP về bảo vệ dữ liệu cá nhân.
Thông tư 39/2017/TT-BTTTT về danh mục tiêu chuẩn kỹ thuật ứng dụng công nghệ thông tin trong cơ quan nhà nước.
TCVN 6909:2001 — Bảng mã ký tự tiếng Việt 16-bit (Unicode UTF-8).
TCVN 7114-1:2008, TCVN 259:2001, TCVN 5828:1994 — chiếu sáng nhân tạo bên trong và bên ngoài công trình.
QCVN 41:2019/BGTVT — Quy chuẩn kỹ thuật quốc gia về báo hiệu đường bộ.
Các văn bản chỉ đạo, hướng dẫn về xây dựng đô thị thông minh và chuyển đổi số do Bộ Thông tin và Truyền thông, Bộ Xây dựng ban hành.
XVII. TIÊU CHÍ NGHIỆM THU
1. Nghiệm thu chức năng
Toàn bộ tính năng theo Bảng tổng hợp tính năng (Mục VI) và Module tín hiệu giao thông (Mục VII) hoạt động đúng theo mô tả, không có lỗi nghiêm trọng (severity 1, 2).
Có biên bản kiểm thử chấp nhận người dùng (User Acceptance Test – UAT) được Chủ đầu tư hoặc đại diện được ủy quyền ký xác nhận.
2. Nghiệm thu hiệu năng
Đáp ứng đầy đủ các chỉ tiêu hiệu năng nêu tại Mục VIII; có báo cáo kiểm thử tải và kiểm thử bền (load test, stress test, endurance test).
3. Nghiệm thu an toàn thông tin
Có báo cáo đánh giá an toàn thông tin của tổ chức đánh giá độc lập có chứng nhận hợp lệ; không tồn tại lỗ hổng cấp độ Cao (High) và Nghiêm trọng (Critical).
Có hồ sơ đề nghị phê duyệt cấp độ an toàn hệ thống thông tin theo Nghị định 85/2016/NĐ-CP.
4. Nghiệm thu tích hợp
Đã hoàn tất kết nối, đồng bộ dữ liệu với tối thiểu 02 dòng tủ điều khiển của 02 nhà sản xuất khác nhau qua chuẩn OPC-UA.
Có biên bản nghiệm thu Open API: tài liệu API đầy đủ, chạy thực tế thành công các kịch bản tích hợp mẫu.

