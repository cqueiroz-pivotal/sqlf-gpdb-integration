CREATE TABLE "APP"."ERD_DATA"
(
  ERD_1 timestamp NOT NULL,
  ERD_2 varchar(100) NOT NULL,
  ERD_3 varchar(20) NOT NULL,
  ERD_4 varchar(20) NOT NULL,
  ERD_5 varchar(20) NOT NULL,
  ERD_6 varchar(20) NOT NULL,
  ERD_7 varchar(20) NOT NULL,
  ERD_8 int NOT NULL,
  ERD_9 int NOT NULL,
  ERD_10 int NOT NULL,
  ERD_11 int NOT NULL,
  ERD_12 int NOT NULL,
  PRIMARY KEY (ERD_2)
) PARTITION BY COLUMN (ERD_2)
  REDUNDANCY 0
  SERVER GROUPS (POC);



call SYS.ADD_LISTENER('ERD_LISTENER',
                      'APP','ERD_DATA',
                      'com.gopivotal.poc.gfxd_gpdb.DataDispatcher',
                      'connectionURL=jdbc:gemfirexd:|numproxies=2|proxyTablePrefix=dataProxy|username=app|password=app|minConn=32|maxConn=128',
                      null);



CREATE ASYNCEVENTLISTENER dataProxy_1
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=2|maxConn=128'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 6000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_1 );

create table DataProxy_1
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_1 ) SERVER GROUPS ( DataProxy_1 ) ;

insert into dataProxy_1 values (1,'hello');


CREATE ASYNCEVENTLISTENER dataProxy_2
(
  LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
   INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
  MANUALSTART false
  ENABLEBATCHCONFLATION false
  BATCHSIZE 100000
  BATCHTIMEINTERVAL 6000
  ENABLEPERSISTENCE false
  MAXQUEUEMEMORY 100
)
  SERVER GROUPS ( dataProxy_2 );


create table dataProxy_2
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_2 ) SERVER GROUPS ( dataProxy_2 ) ;

insert into dataProxy_2 values (1,'hello');



CREATE ASYNCEVENTLISTENER dataProxy_3
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 6000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 100
)
SERVER GROUPS ( poc );


create table dataProxy_3
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_3 ) SERVER GROUPS ( poc ) ;

insert into dataProxy_3 values (1,'hello');


CREATE ASYNCEVENTLISTENER dataProxy_4
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 6000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 100
)
SERVER GROUPS ( poc );


create table dataProxy_4
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_4 ) SERVER GROUPS ( poc ) ;

insert into dataProxy_4 values (1,'hello');



----------------------------------------------------------------------------------------------------
insert into dataProxy_1 values (1,'hello');
insert into dataProxy_2 values (1,'hello');