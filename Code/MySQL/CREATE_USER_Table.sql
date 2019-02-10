use book_mysql;
Create TABLE Table_User (
Username char(28) primary key not null,
Password varchar(50),
Email varchar(50),
QQ varchar(16),
WX varchar(21),
Phone char(11)
);