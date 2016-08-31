# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table cart (
  id                        integer auto_increment not null,
  items                     varchar(255),
  num_items                 integer,
  constraint pk_cart primary key (id))
;

create table item (
  id                        integer auto_increment not null,
  item_name                 varchar(255),
  description               varchar(255),
  price                     double,
  sale                      varchar(255),
  img_path                  varchar(255),
  constraint pk_item primary key (id))
;

create table sale (
  id                        integer auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  profit                    double,
  admin                     varchar(255),
  bookkeeper                varchar(255),
  constraint pk_sale primary key (id))
;

create table user (
  id                        integer auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  email                     varchar(255),
  description               varchar(255),
  attempts                  integer,
  guest                     integer,
  constraint pk_user primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table cart;

drop table item;

drop table sale;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

