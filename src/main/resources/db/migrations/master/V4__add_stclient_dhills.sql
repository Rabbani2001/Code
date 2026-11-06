 Insert into stmaster.stclient_db_config (tenant_id,db_url,db_username,db_password,timestamp) values
  ("stclient_dhills",
  CONCAT(
  "jdbc:mysql://",
  COALESCE('${MYSQL_HOST}', 'localhost'),":",
  COALESCE('${MYSQL_PORT}', '3306'),"/stclient_dhills"),
  COALESCE('${MYSQL_USER}', 'root'),
  COALESCE('${MYSQL_PASSWORD}', 'root'),
  '2025-02-23 18:42:04'
  );


 Insert into stmaster.school_tenant (tenant_code,tenant_name,email,phone_no,alt_phone_no,tenant_id,is_active,timestamp) values
 ("dhs","Dhauj Hills Modern School","dhaujhillsmodernschool@gmail.com","7982125534","NA",'stclient_dhills',1,'2025-02-23 18:42:04');