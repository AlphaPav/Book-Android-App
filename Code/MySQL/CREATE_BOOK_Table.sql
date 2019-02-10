alter database book_mysql character set 'gbk';
use book_mysql;
Create TABLE Table_Book  (
BookID int primary key auto_increment not null,
Username char(28) not null,
Title varchar(100) not null default '',
ISBN varchar(20) not null default '',
Author varchar(50) not null default '',
OriPrice varchar(50) not null default '',
Publisher varchar(100) not null default '',
OriImg varchar(100) not null default '',
Summary varchar(10000)  not null default '',
Catalog varchar(10000)  not null default '',
CurrentImg varchar(100) not null default '',
BookType varchar(100) not null default '',
SellPrice varchar(50) not null default '',
Description varchar(10000) not null default ""
);
