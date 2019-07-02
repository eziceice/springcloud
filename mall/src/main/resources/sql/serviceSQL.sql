create Table PRODUCT
(
    id int(12) not null auto_increment comment 'Product Id',
    product_name varchar(60) not null comment 'Product Name',
    stock int(10) not null comment 'Product Stock',
    price decimal(16,2) not null comment 'Product Price',
    version int(10) not null default 0 comment 'Product Version',
    note varchar(256) null comment 'Product Note',
    primary key(id)
);


create Table PURCHASE_RECORD
(
    id int(12) not null auto_increment comment 'Id',
    user_id int(12) not null comment 'User Id',
    product_id int(12) not null comment 'Product Id',
    price decimal(16,2) not null comment 'Price',
    quantity int(12) not null comment 'Quantity',
    sum decimal(16,2) not null comment 'Sum Price',
    purchase_date timestamp not null default now() comment 'Purchase Date',
    note varchar(512) null comment 'Note',
    primary key (id)
);

INSERT into PRODUCT (product_name, stock, price, note)
values ('Monitor', 1000, 100.00, 'Dell Monitor');