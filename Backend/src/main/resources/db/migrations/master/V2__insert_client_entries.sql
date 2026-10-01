

Insert into stmaster.stclient_db_config (tenant_id,db_url,db_username,db_password,timestamp) values
("stmaster",
CONCAT(
"jdbc:mysql://",
COALESCE('${MYSQL_HOST}', 'localhost'),":",
COALESCE('${MYSQL_PORT}', '3306'),"/",
COALESCE('${MYSQL_DB}', 'stmaster')),
COALESCE('${MYSQL_USER}', 'root'),
COALESCE('${MYSQL_PASSWORD}', 'root'),
'2025-02-23 18:42:04'
);

Insert into stmaster.stclient_db_config (tenant_id,db_url,db_username,db_password,timestamp) values
("stclient_default",
CONCAT(
"jdbc:mysql://",
COALESCE('${MYSQL_HOST}', 'localhost'),":",
COALESCE('${MYSQL_PORT}', '3306'),"/stclient_default"),
COALESCE('${MYSQL_USER}', 'root'),
COALESCE('${MYSQL_PASSWORD}', 'root'),
'2025-02-23 18:42:04'
);


Insert into stmaster.stclient_db_config (tenant_id,db_url,db_username,db_password,timestamp) values
("stclient_iqraa",
CONCAT(
"jdbc:mysql://",
COALESCE('${MYSQL_HOST}', 'localhost'),":",
COALESCE('${MYSQL_PORT}', '3306'),"/stclient_iqraa"),
COALESCE('${MYSQL_USER}', 'root'),
COALESCE('${MYSQL_PASSWORD}', 'root'),
'2025-02-23 18:42:04'
);


 Insert into stmaster.stclient_db_config (tenant_id,db_url,db_username,db_password,timestamp) values
  ("stclient_kanchan",
  CONCAT(
  "jdbc:mysql://",
  COALESCE('${MYSQL_HOST}', 'localhost'),":",
  COALESCE('${MYSQL_PORT}', '3306'),"/stclient_kanchan"),
  COALESCE('${MYSQL_USER}', 'root'),
  COALESCE('${MYSQL_PASSWORD}', 'root'),
  '2025-02-23 18:42:04'
  );


 Insert into stmaster.school_tenant (tenant_code,tenant_name,email,phone_no,alt_phone_no,tenant_id,is_active,timestamp) values
 ("test","Test Public School","default@gmail.com","9999999997","NA",'stclient_default',1,'2025-02-23 18:42:04');

 Insert into stmaster.school_tenant (tenant_code,tenant_name,email,phone_no,alt_phone_no,tenant_id,is_active,timestamp) values
 ("ips","Iqraa Public School","iqraa@gmail.com","9999999999","NA",'stclient_iqraa',1,'2025-02-23 18:42:04');

Insert into stmaster.school_tenant (tenant_code,tenant_name,email,phone_no,alt_phone_no,tenant_id,is_active,timestamp) values
 ("kps","Kanchan Public School","kanchan@gmail.com","9999999998","NA",'stclient_kanchan',1,'2025-02-23 18:42:04');

