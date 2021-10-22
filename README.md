# db2-sql-error
This application connects to **DB2** and executes this simple **SQL** statement:

```sql
SELECT *
FROM ( VALUES (10,'A'),(20,'B'),(30,'C'),(40,'D') ) AS T(COL1,COL2)
WHERE T.COL2 = :PARAM OR :PARAM = 'ALL'
```

The above statement should return the first row if `'A'`  is assigned to `PARAM` parameter, second row if `'B'`, etc...

Otherwise, if `'ALL'` is assigned to `PARAM` then all rows should be returned.

Surprisingly, it does not work.

#### Steps to reproduce:

1. Clone this repository

   ```bash
   git clone https://github.com/noureldin-eg/db2-sql-error.git
   ```

2. *Optionally:* Download the pre-built docker image from **dockerhub**

   ```bash
   docker pull docker.io/noureldin/db2-sql-error
   ```

   *if you skipped step 2, the image will be built from source in the next step*.

3. Start **DB2** and execute the application using **Docker Compose**

   ```bash
   docker-compose up -d
   ```

   *This command will wait for DB2 to be ready and complete its setup before starting the application.*

4. View application output

   ```bash
   docker-compose logs app
   ```

   ```
   Attaching to db2-sql-error_app_1
   Database Product Name: DB2/LINUXX8664
   Database Product Version: SQL110551
   Database Version: 11.5
   Driver Name: IBM Data Server Driver for JDBC and SQLJ
   Driver Version: 4.29.24
   JDBC Version: 4.1
   Number of statement parameters: 2
   SQL type of parameter 1 is VARCHAR(1)
   SQL type of parameter 2 is VARCHAR(3)
   SQLException information:
   Error msg: DB2 SQL Error: SQLCODE=-302, SQLSTATE=22001, SQLERRMC=null, DRIVER=4.29.24
   SQLSTATE: 22001
   Error code: -302
   com.ibm.db2.jcc.am.SqlDataException: DB2 SQL Error: SQLCODE=-302, SQLSTATE=22001, SQLERRMC=null, DRIVER=4.29.24
   	at com.ibm.db2.jcc.am.b7.a(b7.java:802)
   	at com.ibm.db2.jcc.am.b7.a(b7.java:66)
   	at com.ibm.db2.jcc.am.b7.a(b7.java:140)
   	at com.ibm.db2.jcc.am.k9.c(k9.java:2844)
   	at com.ibm.db2.jcc.am.k9.a(k9.java:2281)
   	at com.ibm.db2.jcc.t4.ab.r(ab.java:1670)
   	at com.ibm.db2.jcc.t4.ab.l(ab.java:754)
   	at com.ibm.db2.jcc.t4.ab.d(ab.java:110)
   	at com.ibm.db2.jcc.t4.p.c(p.java:44)
   	at com.ibm.db2.jcc.t4.av.j(av.java:162)
   	at com.ibm.db2.jcc.am.k9.an(k9.java:2276)
   	at com.ibm.db2.jcc.am.k_.a(k_.java:4699)
   	at com.ibm.db2.jcc.am.k_.b(k_.java:4215)
   	at com.ibm.db2.jcc.am.k_.a(k_.java:4860)
   	at com.ibm.db2.jcc.am.k_.b(k_.java:4215)
   	at com.ibm.db2.jcc.am.k_.bd(k_.java:785)
   	at com.ibm.db2.jcc.am.k_.executeQuery(k_.java:750)
   	at com.ibm.db2.jcc.am.d0.executeQuery(d0.java:297)
   	at com.example.App.main(App.java:40)
   ```

5. One workaround to fix this error is to **un**comment [line #30](https://github.com/noureldin-eg/db2-sql-error/blob/4eb6ed8cf15c165d9b076ac55ad303c78d34e923/src/main/java/com/example/App.java#L30)

   ```java
   SQL = SQL.replaceAll(":PARAM", "'" + PARAM + "'"); // Uncomment me
   ```

   and comment [line #37](https://github.com/noureldin-eg/db2-sql-error/blob/4eb6ed8cf15c165d9b076ac55ad303c78d34e923/src/main/java/com/example/App.java#L37)

   ```java
   statement.setJccStringAtName("PARAM", PARAM); // Comment me
   ```

6. Rebuild and restart the application

   ```bash
   docker-compose up -d --build
   ```

7. All rows should be returned this time

   ```bash
   docker-compose logs app
   ```

   ```
   Attaching to db2-sql-error_app_1
   Database Product Name: DB2/LINUXX8664
   Database Product Version: SQL110551
   Database Version: 11.5
   Driver Name: IBM Data Server Driver for JDBC and SQLJ
   Driver Version: 4.29.24
   JDBC Version: 4.1
   Results:
   COL1  COL2
   ----  ----
     10  A   
     20  B   
     30  C   
     40  D   
   ```

---

#### Build from source:

You can build an executable jar directly if have **Maven** installed or you can build an the application image using **Docker**. 
Just `cd` into the project root directory and execute the following commands to build and run the application.

- **Maven:**

  ```bash
  mvn clean package
  java -jar target/db2-sql-error-*-SNAPSHOT-jar-with-dependencies.jar
  ```

- **Docker:**

  ```bash
  docker build -t docker.io/noureldin/db2-sql-error .
  docker run docker.io/noureldin/db2-sql-error
  ```

---

I have also executed the query on other **DB2** versions and got the same results

1. ```
	Database Product Name: DB2/LINUXX8664
	Database Product Version: SQL110560
	Database Version: 11.5
	```

2. ```
	Database Product Name: DB2/AIX64
	Database Product Version: SQL110146
	Database Version: 11.1
	```
