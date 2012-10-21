# --- Users schema 
 

# --- !Downs
alter table Users drop column facebookid;

# --- !Ups
alter table Users add facebookid bigint;