create table xinhuashudian_metadata(
    item_id varchar2(64) primary key,
    title varchar2(128),
    author varchar2(128),
    isbn varchar2(128),
    publisher varchar2(128),
    issued_date varchar2(64),
    binding varchar2(64),
    pages varchar2(16),
    language varchar2(32),
    format varchar2(32),
    print_time varchar2(32),
    meta_id varchar2(32),
    cip varchar2(64),
    cover_img_url varchar2(256),
    create_time date,
    update_time date
);