import java.sql.*;
import java.util.Scanner;

public class UniversityManager {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;" +
            "databaseName=university;" +
            "integratedSecurity=true;" +   // 启用 Windows 身份验证
            "encrypt=false;";              // 关闭加密连接
    private Connection connection;
    private Scanner scanner;

    public UniversityManager() {
        try {
            // 通过 Windows 身份验证连接数据库
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("数据库连接成功！");
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
        }
        scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("请选择操作：");
            System.out.println("1. 添加学生（不安全）");
            System.out.println("2. 查找学生（不安全）");
            System.out.println("3. 修改学生信息（不安全）");
            System.out.println("4. 删除学生（不安全）");
            System.out.println("5. 添加学生（安全）");
            System.out.println("6. 查找学生（安全）");
            System.out.println("7. 修改学生信息（安全）");
            System.out.println("8. 删除学生（安全）");
            System.out.println("9. 退出");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addStudentVulnerable();
                    break;
                case "2":
                    searchStudentVulnerable();
                    break;
                case "3":
                    updateStudentVulnerable();
                    break;
                case "4":
                    deleteStudentVulnerable();
                    break;
                case "5":
                    addStudentSecure();
                    break;
                case "6":
                    searchStudentSecure();
                    break;
                case "7":
                    updateStudentSecure();
                    break;
                case "8":
                    deleteStudentSecure();
                    break;
                case "9":
                    close();
                    return;
                default:
                    System.out.println("无效选择，请重新选择。");
            }
        }
    }

    // 添加学生（不安全）
    private void addStudentVulnerable() {
        System.out.println("请输入学生ID：");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("请输入学生姓名：");
        String name = scanner.nextLine();
        System.out.println("请输入专业：");
        String major = scanner.nextLine();
        System.out.println("请输入学分：");
        int credits = Integer.parseInt(scanner.nextLine());

        String sql = "INSERT INTO student (ID, name, dept_name, tot_cred) VALUES (" + id + ", '" + name + "', '" + major + "', " + credits + ")";

        try (Statement stmt = connection.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println(rowsAffected + " 位学生已添加！");
        } catch (SQLException e) {
            System.err.println("添加学生失败: " + e.getMessage());
        }
    }

    // 添加学生（安全）
    private void addStudentSecure() {
        System.out.println("请输入学生ID：");
        int id = Integer.parseInt(scanner.nextLine());

        String name;
        while (true) {
            System.out.println("请输入学生姓名：");
            name = scanner.nextLine();
            if (isValidInput(name)) {
                break;  // 输入合法，跳出循环
            } else {
                System.out.println("输入非法，请重新输入。");
            }
        }

        String major;
        while (true) {
            System.out.println("请输入专业：");
            major = scanner.nextLine();
            if (isValidInput(major)) {
                break;  // 输入合法，跳出循环
            } else {
                System.out.println("输入非法，请重新输入。");
            }
        }

        System.out.println("请输入学分：");
        int credits = Integer.parseInt(scanner.nextLine());

        String sql = "INSERT INTO student (ID, name, dept_name, tot_cred) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, major);
            pstmt.setInt(4, credits);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " 位学生已添加！");
        } catch (SQLException e) {
            System.err.println("添加学生失败: " + e.getMessage());
        }
    }

    // 输入合法性检查方法
    private boolean isValidInput(String input) {
        // 这里检查输入是否包含不合法字符
        String illegalCharacters = "';--";
        for (char c : illegalCharacters.toCharArray()) {
            if (input.indexOf(c) >= 0) {
                return false;  // 如果输入包含非法字符，返回false
            }
        }
        return true;  // 输入合法
    }

    // 查找学生（不安全）
    private void searchStudentVulnerable() {
        System.out.println("请输入学生姓名：");
        String name = scanner.nextLine();

        String sql = "SELECT * FROM student WHERE name = '" + name + "'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("ID"));
                System.out.println("姓名: " + rs.getString("name"));
                System.out.println("专业: " + rs.getString("dept_name"));
                System.out.println("学分: " + rs.getInt("tot_cred"));
                System.out.println("--------------");
            }
        } catch (SQLException e) {
            System.err.println("查询学生失败: " + e.getMessage());
        }
    }

    // 查找学生（安全）
    private void searchStudentSecure() {
        String name;
        while (true) {
            System.out.println("请输入学生姓名：");
            name = scanner.nextLine();
            if (isValidInput(name)) {
                break;  // 输入合法，跳出循环
            } else {
                System.out.println("输入非法，请重新输入。");
            }
        }

        String sql = "SELECT * FROM student WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println("ID: " + rs.getInt("ID"));
                    System.out.println("姓名: " + rs.getString("name"));
                    System.out.println("专业: " + rs.getString("dept_name"));
                    System.out.println("学分: " + rs.getInt("tot_cred"));
                    System.out.println("--------------");
                }
                if (!found) {
                    System.out.println("没有找到该学生。");
                }
            }
        } catch (SQLException e) {
            System.err.println("查询学生失败: " + e.getMessage());
        }
    }


    // 修改学生信息（不安全）
    private void updateStudentVulnerable() {
        System.out.println("请输入要修改的学生ID：");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("请输入新的姓名：");
        String name = scanner.nextLine();
        System.out.println("请输入新的专业：");
        String major = scanner.nextLine();
        System.out.println("请输入新的学分：");
        int credits = Integer.parseInt(scanner.nextLine());

        String sql = "UPDATE student SET name = '" + name + "', dept_name = '" + major + "', tot_cred = " + credits + " WHERE ID = " + id;

        try (Statement stmt = connection.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("学生信息已更新！");
        } catch (SQLException e) {
            System.err.println("更新学生信息失败: " + e.getMessage());
        }
    }

    // 修改学生信息（安全）
    private void updateStudentSecure() {
        System.out.println("请输入要修改的学生ID：");
        int id = Integer.parseInt(scanner.nextLine());
        String name;
        while (true) {
            System.out.println("请输入新的姓名：");
            name = scanner.nextLine();
            if (isValidInput(name)) {
                break;  // 如果输入合法，跳出循环
            } else {
                System.out.println("输入非法，请重新输入。");
            }
        }
        String major;
        while (true) {
            System.out.println("请输入新的专业：");
            major = scanner.nextLine();
            if (isValidInput(major)) {
                break;  // 如果输入合法，跳出循环
            } else {
                System.out.println("输入非法，请重新输入。");
            }
        }
        int credits;
        while (true) {
            System.out.println("请输入新的学分：");
            try {
                credits = Integer.parseInt(scanner.nextLine());
                break;  // 如果输入是整数，跳出循环
            } catch (NumberFormatException e) {
                System.out.println("学分输入无效，请重新输入整数值。");
            }
        }

        // SQL更新操作
        String sql = "UPDATE student SET name = ?, dept_name = ?, tot_cred = ? WHERE ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, major);
            pstmt.setInt(3, credits);
            pstmt.setInt(4, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("学生信息已更新！");
        } catch (SQLException e) {
            System.err.println("更新学生信息失败: " + e.getMessage());
        }
    }


    // 删除学生（不安全）
    private void deleteStudentVulnerable() {
        System.out.println("请输入要删除的学生ID：");
        String input = scanner.nextLine(); // 获取用户输入

        // 直接拼接用户输入到 SQL 语句中
        String sql = "DELETE FROM student WHERE ID = " + input;

        try (Statement stmt = connection.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("学生已删除！");
        } catch (SQLException e) {
            System.err.println("删除学生失败: " + e.getMessage());
        }
    }

    // 删除学生（安全）
    private void deleteStudentSecure() {
        int id;
        // 验证学生ID的输入是否合法
        while (true) {
            System.out.println("请输入要删除的学生ID：");
            try {
                id = Integer.parseInt(scanner.nextLine());  // 尝试将输入转换为整数
                break;  // 如果输入是合法整数，跳出循环
            } catch (NumberFormatException e) {
                System.out.println("无效的ID输入，请输入有效的整数值。");
            }
        }

        String sql = "DELETE FROM student WHERE ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("学生已删除！");
            } else {
                System.out.println("未找到指定ID的学生，删除失败。");
            }
        } catch (SQLException e) {
            System.err.println("删除学生失败: " + e.getMessage());
        }
    }


    private void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("数据库连接已关闭。");
            }
        } catch (SQLException e) {
            System.err.println("关闭连接失败: " + e.getMessage());
        }
        scanner.close();
    }

    public static void main(String[] args) {
        UniversityManager manager = new UniversityManager();
        manager.start();
    }
}
