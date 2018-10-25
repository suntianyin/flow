create table book.apabi_book_metadata_pub_record(
    ID VARCHAR2(64) PRIMARY KEY,
    OPERATEDATATYPE VARCHAR2(32),
    METAID VARCHAR2(128),
    OPERATOR VARCHAR2(128),
    OPERATETIME DATE,
    OPERATERESULT varchar2(128),
    PRECONTENT CLOB,
    POSTCONTENT CLOB,
    DATASOURCE VARCHAR2(64)
    CREATETIME DATE,
    UPDATETIME DATE,
    HASSYNC NUMBER(1,0)
);