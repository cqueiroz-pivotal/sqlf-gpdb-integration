CREATE TABLE "APP"."ERD_DATA"
(
  ERD_1 timestamp NOT NULL,
  ERD_2 VARCHAR(100) NOT NULL,
  ERD_3 varchar(20) NOT NULL,
  ERD_4 varchar(20) NOT NULL,
  ERD_5 varchar(20) NOT NULL,
  ERD_6 varchar(20) NOT NULL,
  ERD_7 varchar(20) NOT NULL,
  ERD_8 int NOT NULL,
  ERD_9 int NOT NULL,
  ERD_10 int NOT NULL,
  ERD_11 int NOT NULL,
  ERD_12 int NOT NULL
) PARTITION BY COLUMN (ERD_2)
  SERVER GROUPS (POC) REDUNDANCY 0;



call SYS.ADD_LISTENER('ERD_LISTENER',
                      'APP','ERD_DATA',
                      'com.gopivotal.poc.gfxd_gpdb.DataDispatcher',
                      'connectionURL=jdbc:gemfirexd:|numproxies=4|proxyTablePrefix=dataProxy|username=app|password=app|minConn=32|maxConn=128',
                      null);


CREATE ASYNCEVENTLISTENER dataProxy_1
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=2|maxConn=128'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( DataProxy_1 );

CREATE ASYNCEVENTLISTENER dataProxy_11
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=2|maxConn=128'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( DataProxy_1 );

CREATE ASYNCEVENTLISTENER dataProxy_111
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=2|maxConn=128'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( DataProxy_1 );

CREATE ASYNCEVENTLISTENER dataProxy_1111
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=2|maxConn=128'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( DataProxy_1 );


create table DataProxy_1
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_1 ) SERVER GROUPS ( DataProxy_1 ) ;

create table DataProxy_11
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_11 ) SERVER GROUPS ( DataProxy_1 ) ;

create table DataProxy_111
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_111 ) SERVER GROUPS ( DataProxy_1 ) ;


create table DataProxy_1111
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_1111 ) SERVER GROUPS ( DataProxy_1 ) ;






CREATE ASYNCEVENTLISTENER dataProxy_2
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_2 );

CREATE ASYNCEVENTLISTENER dataProxy_22
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_2 );


CREATE ASYNCEVENTLISTENER dataProxy_222
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_2 );

CREATE ASYNCEVENTLISTENER dataProxy_2222
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_2 );



create table DataProxy_2
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_2 ) SERVER GROUPS ( DataProxy_2 ) ;

create table DataProxy_22
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_22 ) SERVER GROUPS ( DataProxy_2 ) ;

create table DataProxy_222
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_222 ) SERVER GROUPS ( DataProxy_2 ) ;

create table DataProxy_2222
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_2222 ) SERVER GROUPS ( DataProxy_2 ) ;



CREATE ASYNCEVENTLISTENER dataProxy_3
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_3 );

CREATE ASYNCEVENTLISTENER dataProxy_33
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_3 );

CREATE ASYNCEVENTLISTENER dataProxy_333
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_3 );

CREATE ASYNCEVENTLISTENER dataProxy_3333
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_3 );


create table DataProxy_3
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_3 ) SERVER GROUPS ( DataProxy_3 ) ;

create table DataProxy_33
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_33 ) SERVER GROUPS ( DataProxy_3 ) ;

create table DataProxy_333
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_333 ) SERVER GROUPS ( DataProxy_3 ) ;

create table DataProxy_3333
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_3333 ) SERVER GROUPS ( DataProxy_3 ) ;



CREATE ASYNCEVENTLISTENER dataProxy_4
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_4 );

CREATE ASYNCEVENTLISTENER dataProxy_44
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_4 );

CREATE ASYNCEVENTLISTENER dataProxy_444
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_4 );

CREATE ASYNCEVENTLISTENER dataProxy_4444
(
LISTENERCLASS 'com.gopivotal.poc.gfxd_gpdb.DataBatchListener'
INITPARAMS 'pipeFileLocation=/dev/null|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin|gfxdConnectionURL=jdbc:sqlfire:|gfxdUserName=app|gfxdPassword=app|delPattern=delete from app.erd_data where ERD_2=''{1}''|whereClausePostions=1|minConn=32|maxConn=64'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 5000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 500
)
SERVER GROUPS ( dataProxy_4 );

create table DataProxy_4
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_4 ) SERVER GROUPS ( DataProxy_4 ) ;

create table DataProxy_44
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_44 ) SERVER GROUPS ( DataProxy_4 ) ;

create table DataProxy_444
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_444) SERVER GROUPS ( DataProxy_4 ) ;


create table DataProxy_4444
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( dataProxy_4444 ) SERVER GROUPS ( DataProxy_4 ) ;



insert into dataProxy_1 values (1,'hello');
insert into dataProxy_11 values (1,'hello');
insert into dataProxy_111 values (1,'hello');
insert into dataProxy_1111 values (1,'hello');

insert into dataProxy_2 values (1,'hello');
insert into dataProxy_22 values (1,'hello');
insert into dataProxy_222 values (1,'hello');
insert into dataProxy_2222 values (1,'hello');

insert into dataProxy_3 values (1,'hello');
insert into dataProxy_33 values (1,'hello');
insert into dataProxy_333 values (1,'hello');
insert into dataProxy_3333 values (1,'hello');

insert into dataProxy_4 values (1,'hello');
insert into dataProxy_44 values (1,'hello');
insert into dataProxy_444 values (1,'hello');
insert into dataProxy_4444 values (1,'hello');