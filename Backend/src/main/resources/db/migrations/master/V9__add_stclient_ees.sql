 Insert into stmaster.stclient_db_config (tenant_id,db_url,db_username,db_password,timestamp) values
  ("stclient_ees",
  CONCAT(
  "jdbc:mysql://",
  COALESCE('${MYSQL_HOST}', 'localhost'),":",
  COALESCE('${MYSQL_PORT}', '3306'),"/stclient_ees"),
  COALESCE('${MYSQL_USER}', 'root'),
  COALESCE('${MYSQL_PASSWORD}', 'root'),
   NOW()
  );


 Insert into stmaster.school_tenant (tenant_code,tenant_name,email,phone_no,alt_phone_no,tenant_id,is_active,timestamp) values
 ("ees","English Express School","ees@gmail.com","98973 63201","NA",'stclient_ees',1,NOW());