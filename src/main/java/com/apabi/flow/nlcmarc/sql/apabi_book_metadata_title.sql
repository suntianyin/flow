# 阿帕比图书题名数据建表sql
CREATE TABLE APABI_BOOK_METADATA_TITLE(
    ID VARCHAR2(64) PRIMARY KEY,
    METAID VARCHAR2(64),
    NLCMARCIDENTIFIER VARCHAR2(64),
    TITLE VARCHAR2(512),
    SUBTITLE VARCHAR2(512),
    TITLEPINYIN VARCHAR2(512),
    SERIESTITLE VARCHAR2(256),
    PARALLELSERIESTITLE VARCHAR2(256),
    SERIESSUBTITLE VARCHAR2(512),
    VOLUMETITLE VARCHAR2(256),
    PARALLELTITLE VARCHAR2(256),
    COVERTITLE VARCHAR2(256),
    ADDEDPAGETITLE VARCHAR2(256),
    CAPTIONTITLE VARCHAR2(256),
    RUNNINGTITLE VARCHAR2(256),
    SPINETITLE VARCHAR2(256),
    OTHERVARIANTTITLE VARCHAR2(256),
    OPERATOR VARCHAR2(128),
    CREATETIME DATE,
    UPDATETIME DATE
);