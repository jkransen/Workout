# --- Users schema 
 

# --- !Downs

DROP TABLE if exists Performances;
DROP TABLE if exists Exercises;
DROP TABLE if exists Machines;
DROP TABLE if exists Gyms;
DROP TABLE if exists Users;


# --- !Ups
 
CREATE TABLE Users (
    id BIGSERIAL,
    email varchar(255) NOT NULL,
    fullname varchar(255),
    facebookusername varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE Gyms (
    id BIGSERIAL,
    name varchar(255) NOT NULL,
    adminid bigint NOT NULL references Users,
    PRIMARY KEY (id)
);

CREATE TABLE Machines (
    id BIGSERIAL,
    name  varchar(255),
    location  varchar(255),
    gymid bigint references Gyms,
    PRIMARY KEY (id)
);

CREATE TABLE Exercises (
    id BIGSERIAL,
    variation varchar(255),
    musclezone varchar(255),
    machineid bigint references Machines,
    PRIMARY KEY (id)
);

CREATE TABLE Performances (
    id BIGSERIAL,
    userid bigint NOT NULL references Users,
    exerciseid bigint NOT NULL references Exercises,
    weight numeric(4,1) NOT NULL,
    notes varchar(255),
    tstamp timestamp NOT NULL,
    PRIMARY KEY (id)
);


