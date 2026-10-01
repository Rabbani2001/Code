 Insert into stmaster.stclient_db_config (tenant_id,db_url,db_username,db_password,timestamp) values
  ("stclient_jfpl",
  CONCAT(
  "jdbc:mysql://",
  COALESCE('${MYSQL_HOST}', 'localhost'),":",
  COALESCE('${MYSQL_PORT}', '3306'),"/stclient_jfpl"),
  COALESCE('${MYSQL_USER}', 'root'),
  COALESCE('${MYSQL_PASSWORD}', 'root'),
   NOW()
  );


 Insert into stmaster.school_tenant (tenant_code,tenant_name,email,phone_no,alt_phone_no,tenant_id,is_active,timestamp) values
 ("jfpl","Jasmine Fiore Private Limited","connecttojasmine1@gmail.com","6397148494","NA",'stclient_jfpl',1,NOW());