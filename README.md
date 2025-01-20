数据库系统实验  
自行设想一个场景，编写一个简单的交互式命令行程序，该程序接收用户输入并访问数据库以存取数据。在实现与数据库的交互时，先使用字符串操作来构造SQL语句。  
以攻击者的视角，尝试针对程序中用到的SQL语句实行SQL注入，展示注入结果。  
修改代码，将构造SQL的方式从字符串操作更改为预编译语句，重新尝试SQL注入，查看是否成功。


1.配置sql sever manager启用tcp/ip访问，设置端口1433  
 ![image](https://github.com/user-attachments/assets/13b979bb-13b6-405c-ac63-4af636176572)  
2.运行largeRelationsInsertFile.sql脚本创建university数据库  
 ![image](https://github.com/user-attachments/assets/0f9242b0-a7e4-4d13-9154-4372eff46fff)  
3.不安全的操作方法
1),连接数据库，尝试向student插入学生，但是输入了非法的名字wujc','Comp. Sci.',100);--
使得查询语句变成INSERT INTO student (ID, name, dept_name, tot_cred) VALUES ('990', ' wujc','Comp. Sci.',100);--', 'Math', 60)。后面输入的系名Math和分数60被注释，攻击者可以根据这点添加错误的数据
可以看到插入成功  
 ![image](https://github.com/user-attachments/assets/7b90d76e-267b-4fcc-badb-c610bd0c6730)
![image](https://github.com/user-attachments/assets/1614449f-e853-4fda-9398-9dafb6cd0174)  
2),查询学生时输入' OR '1'='1，
那么sql查询变成SELECT * FROM Students WHERE name = ' ' OR '1'='1'
返回了所有学生的结果  
 ![image](https://github.com/user-attachments/assets/6f105fb2-9af9-4731-b18c-05bc19bb793c)  
 ![image](https://github.com/user-attachments/assets/22904789-7080-4828-941b-852667a17c22)  
3),修改学生信息，在输入名字时输入wujc', dept_name = 'Math', tot_cred = 100; DELETE FROM student; -- 。使得输入变成UPDATE student SET name = 'wujc', dept_name = 'Math', tot_cred = 100; DELETE FROM student; -- ', dept_name = 'Math', tot_cred = 60  WHERE ID = 999;攻击者可以借助修改学生的权限删除所有学生信息
可以看到学生全部被删除  
 ![image](https://github.com/user-attachments/assets/d6a04dad-cf91-4c21-8ab6-c80c000d190c)
![image](https://github.com/user-attachments/assets/c41db929-d998-418d-9f78-f4a5987cc14a)  
4),重新运行largeRelationsInsertFile.sql脚本创建university数据库后
尝试删除操作，输入999; DELETE FROM student; -- 使得删除了所有学生  
![image](https://github.com/user-attachments/assets/7f3bd89d-133c-4136-a240-81261e64eb35)
![image](https://github.com/user-attachments/assets/db5a1991-c2a2-43ae-85db-36d39d92cc52)  
5.使用预编译输入，防止sql注入  
添加了判断输入是否合法的方法，检测输入的字符串有没有出现(‘,”,--)  
 ![image](https://github.com/user-attachments/assets/368b8400-b53e-42a4-be65-a65efeff0f19)  
1),连接数据库，尝试向student插入学生，但是输入了非法的名字wujc','Comp. Sci.',100);--  
程序判断输入非法，要求输入正确的学生名字  
 ![image](https://github.com/user-attachments/assets/f3c324d2-e6c3-45f4-bce8-536fd2edf002)
![image](https://github.com/user-attachments/assets/fe127779-6b33-4910-b214-6a6955645750)
![image](https://github.com/user-attachments/assets/c95fb008-7d8b-437a-9cbc-1b389c452a62)  
2),查询学生时输入' OR '1'='1, 程序判断输入非法  
 ![image](https://github.com/user-attachments/assets/410c327b-c642-44af-ac3b-3deda05adbd5)
![image](https://github.com/user-attachments/assets/70440e62-bcdc-4c43-bb69-fce340e30f18)
![image](https://github.com/user-attachments/assets/3e72b4b5-0937-45ff-b815-13b133830ec4)  
3),修改学生信息，在输入名字时输入wujc', dept_name = 'Math', tot_cred = 100; DELETE FROM student; --  
程序判断输入非法  
![image](https://github.com/user-attachments/assets/f475a297-4b19-4052-ab48-d45e88aa0828)
 ![image](https://github.com/user-attachments/assets/bbba39ba-00f9-497b-973d-d2c6f48704a6)
![image](https://github.com/user-attachments/assets/26c11538-70ab-4e96-aa15-4c1c19461b82)  
4),尝试删除操作，输入999; DELETE FROM student; --  
判断输入id不是int类型的整数  
 ![image](https://github.com/user-attachments/assets/75a02e72-c5c1-4dd9-b31b-f2af364c200a)
![image](https://github.com/user-attachments/assets/382c8fd7-fc20-4f13-b0a4-03dab27db0cf)  
  

  完整源代码在src/UniversityManager.java
 
