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
SERVER GROUPS (POC)
EVICTION BY LRUCOUNT 50000000 EVICTACTION DESTROY;



call SYS.ADD_LISTENER('ERD_LISTENER',
     'APP','ERD_DATA',
     'demo.vmware.sqlfire.greenplum.MultiHubProxyDispatcher',
     'connectionURL=jdbc:sqlfire:|numproxies=2|proxyTablePrefix=dataProxy|username=app|password=app',
      null);



CREATE ASYNCEVENTLISTENER Proxy_0
(
LISTENERCLASS 'demo.vmware.sqlfire.greenplum.MicroBatchListener'
INITPARAMS 'pipeFileLocation=/tmp/data.pipe|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin'
MANUALSTART false
ENABLEBATCHCONFLATION false
BATCHSIZE 100000
BATCHTIMEINTERVAL 60000
ENABLEPERSISTENCE false
MAXQUEUEMEMORY 100
)
SERVER GROUPS ( dataProxy_1 );

CREATE ASYNCEVENTLISTENER Proxy_1
(
  LISTENERCLASS 'demo.vmware.sqlfire.greenplum.MicroBatchListener'
   INITPARAMS 'pipeFileLocation=/tmp/data.pipe|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin'
  MANUALSTART false
  ENABLEBATCHCONFLATION false
  BATCHSIZE 100000
  BATCHTIMEINTERVAL 60000
  ENABLEPERSISTENCE false
  MAXQUEUEMEMORY 100
)
  SERVER GROUPS ( dataProxy_1 );


create table Proxy_1
  ( k integer, value varchar(500))
 ASYNCEVENTLISTENER ( Proxy_1 ) SERVER GROUPS ( dataProxy_1 ) ;

insert into Proxy_1 values (1,'hello');


create table Proxy_0
( k integer, value varchar(500))
  ASYNCEVENTLISTENER ( Proxy_1 ) SERVER GROUPS ( dataProxy_1 ) ;

----------------------------------------------------------------------------------------------------

CREATE ASYNCEVENTLISTENER Proxy_2
(
  LISTENERCLASS 'demo.vmware.sqlfire.greenplum.MicroBatchListener'
  INITPARAMS 'pipeFileLocation=/tmp/data.pipe|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin'
  MANUALSTART false
  ENABLEBATCHCONFLATION false
  BATCHSIZE 100000
  BATCHTIMEINTERVAL 60000
  ENABLEPERSISTENCE false
  MAXQUEUEMEMORY 100
)
  SERVER GROUPS ( dataProxy_2 );


create table Proxy_2
  ( k integer, value varchar(500))
 ASYNCEVENTLISTENER ( Proxy_2 ) SERVER GROUPS ( dataProxy_2 ) ;

insert into Proxy_2 values (1,'hello');
----------------------------------------------------------------------------------------------------

CREATE ASYNCEVENTLISTENER dataProxy_3
(
  LISTENERCLASS 'demo.vmware.sqlfire.greenplum.MicroBatchListener'
  INITPARAMS 'pipeFileLocation=/tmp/data.pipe|extTableName=app.ext_data|destTableName=app.data|connectionURL=jdbc:postgresql://mdw:5432/fdc|username=gpadmin|password=gpadmin'
  MANUALSTART false
  ENABLEBATCHCONFLATION false
  BATCHSIZE 100000
  BATCHTIMEINTERVAL 60000
  ENABLEPERSISTENCE false
  MAXQUEUEMEMORY 100
)
  SERVER GROUPS ( dataProxy_3 );


create table dataProxy_3
  ( k integer, value varchar(500))
 ASYNCEVENTLISTENER ( flightsProxy_3 ) SERVER GROUPS ( dataProxy_3) ;

insert into dataProxy_3 values (1,'hello');