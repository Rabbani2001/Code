 Insert into stmaster.stclient_db_config (tenant_id,db_url,db_username,db_password,timestamp) values
  ("stclient_mahs",
  CONCAT(
  "jdbc:mysql://",
  COALESCE('${MYSQL_HOST}', 'localhost'),":",
  COALESCE('${MYSQL_PORT}', '3306'),"/stclient_mahs"),
  COALESCE('${MYSQL_USER}', 'root'),
  COALESCE('${MYSQL_PASSWORD}', 'root'),
  '2025-02-23 18:42:04'
  );


 Insert into stmaster.school_tenant (tenant_code,tenant_name,email,phone_no,alt_phone_no,tenant_id,is_active,timestamp) values
 ("mahs","M.A.H.S School","mahsschool@gmail.com","9897216322","NA",'stclient_mahs',1,'2025-02-23 18:42:04');


  Insert into stmaster.stclient_db_config (tenant_id,db_url,db_username,db_password,timestamp) values
   ("stclient_mahsp",
   CONCAT(
   "jdbc:mysql://",
   COALESCE('${MYSQL_HOST}', 'localhost'),":",
   COALESCE('${MYSQL_PORT}', '3306'),"/stclient_mahsp"),
   COALESCE('${MYSQL_USER}', 'root'),
   COALESCE('${MYSQL_PASSWORD}', 'root'),
   '2025-02-23 18:42:04'
   );


  Insert into stmaster.school_tenant (tenant_code,tenant_name,email,phone_no,alt_phone_no,tenant_id,is_active,timestamp) values
  ("mahsp","M.A.H.S Public School","mahspublicschool@gmail.com","9897216322","NA",'stclient_mahsp',1,'2025-02-23 18:42:04');