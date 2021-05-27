-- vid1

DROP DATABASE IF EXISTS jemstone;
CREATE DATABASE jemstone;
USE jemstone;

SHOW TABLES;

CREATE TABLE User (
    username CHAR(5) PRIMARY KEY,
    password VARCHAR(16) NOT NULL,
    role CHAR(1) CHECK (role IN ('R', 'D', 'P')) NOT NULL 
);
CREATE TABLE Doctor (
    did INTEGER AUTO_INCREMENT PRIMARY KEY,
    initial CHAR(1) NOT NULL,
    surname VARCHAR(10) NOT NULL,
    roomNo CHAR(2) NOT NULL UNIQUE,
    phoneNo VARCHAR(11) NOT NULL UNIQUE,
    background TEXT,
    username CHAR(5) NOT NULL,
    FOREIGN KEY (username) REFERENCES User (username)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE TABLE Patient (
    pid INTEGER AUTO_INCREMENT PRIMARY KEY,
    forename VARCHAR(10) NOT NULL,
    surname VARCHAR(10) NOT NULL,
    gender CHAR(1) CHECK (gender IN ('M', 'F', 'O')) NOT NULL,
    dob DATE NOT NULL,
    phoneNo VARCHAR(11) NOT NULL UNIQUE,
    did INTEGER,
    username CHAR(5) NOT NULL,
    FOREIGN KEY (username) REFERENCES User (username)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (did) REFERENCES Doctor (did)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);
CREATE TABLE Message (
    msgid INTEGER AUTO_INCREMENT PRIMARY KEY,
    time TIME NOT NULL,
    date DATE NOT NULL,
    msgHead TEXT NOT NULL,
    msgBody TEXT,
    username CHAR(5) NOT NULL,
    FOREIGN KEY (username) REFERENCES User (username)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE TABLE Booking (
  did INTEGER NOT NULL,
  pid INTEGER NOT NULL,
  date DATE NOT NULL,
  time TIME NOT NULL,
  PRIMARY KEY (did, date, time),
  FOREIGN KEY (did) REFERENCES Doctor (did) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (pid) REFERENCES Patient (pid) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE TABLE Activity (
  actid INTEGER AUTO_INCREMENT PRIMARY KEY,
  username CHAR(5) NOT NULL,
  actCode INTEGER NOT NULL,
  details TEXT,
  date DATE NOT NULL,
  time TIME NOT NULL,
  did INTEGER,
  pid INTEGER,
  FOREIGN KEY (username) REFERENCES User (username) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (did) REFERENCES Doctor (did) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (pid) REFERENCES Patient (pid) ON UPDATE CASCADE ON DELETE CASCADE
);

SHOW TABLES;

-- vid2
SELECT * FROM User;
-- gui: try login
INSERT INTO User VALUES ('eh443', 'QWERTY', 'P');
INSERT INTO Message (time, date, msgHead, msgBody, username) VALUES ('10:20', '2021/02/22', 'This is your message subject', 'And this is your message body.', 'eh443');
-- gui: try login
SELECT * FROM User;

-- vid3
SELECT * FROM Doctor;
-- gui: add a doctor
--INSERT INTO Doctor (initial, surname, roomNo, phoneNo, background, username) VALUES ('G', 'Costanza', '2B', '07823458234', 'long descripion...', 'glc');
-- glc, costanza, G, Costanza, 2B, 07823458234, long desc
SELECT * FROM Doctor;

-- vid4
SELECT * FROM Patient;
SELECT * FROM Message;
-- gui: add a patient
--INSERT INTO Patient (forename, surname, gender, dob, phoneNo, did, username) VALUES ('Jesus', 'Christ', 'M', '1900/1/1', '07243761666', 1, 'jc');
--INSERT INTO User VALUES ('jc', 'saviour', 'P');
-- jc, saviour, Jesus, Christ, M, 1900/1/1, 07243761666, 1
SELECT * FROM Patient;
SELECT * FROM Message;

-- vid5
INSERT INTO User VALUES ('jas', 'seinfeld', 'D');
INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username, background) VALUES ('J', 'Seinfeld', '5A', '07395924835', 'jas', 'According to all known laws of aviation, there is no way a bee should be able to fly. Its wings are too small to get its fat little body off the ground. The bee, of course, flies anyway because bees dont care what humans think is impossible.');
SELECT * FROM Patient;
SELECT * FROM Message;
-- gui: reassign jesus from costanza (1) to seinfeld (2)
SELECT * FROM Patient;
SELECT * FROM Message;

-- vid6
SELECT * FROM Booking;
INSERT INTO User VALUES ('darth', 'sand', 'P');
INSERT INTO User VALUES ('kenbe', 'highground', 'P');
INSERT INTO Patient (forename, surname, gender, dob, phoneNo, did, username) VALUES ('ObiWan', 'Kenobi', 'M', '1990-02-01', '07234567891', 1, 'kenbe');
INSERT INTO Patient (forename, surname, gender, dob, phoneNo, did, username) VALUES ('Anakin', 'Skywalker', 'M', '1969-04-20', '07234567890', 1, 'darth');
INSERT IGNORE INTO Booking VALUES (1, (SELECT pid FROM Patient WHERE forename='ObiWan'), '2021-03-10', '10:00:00');
INSERT IGNORE INTO Booking VALUES (1, (SELECT pid FROM Patient WHERE forename='Anakin'), '2021-03-11', '09:00:00');
INSERT IGNORE INTO Booking VALUES (4, (SELECT pid FROM Patient WHERE forename='Jesus'), '2021-03-12', '11:00:00');
INSERT IGNORE INTO Booking VALUES (4, (SELECT pid FROM Patient WHERE forename='Jesus'), '2021-04-12', '11:30:00');
SELECT * FROM Booking;
-- gui: p obiwan filter-> d seinfeld filter -> m 2/21 filter
-- this is wrong because filters are individual: gui: d costanza p obi-wan -> d costanza p anakin -> d seinfeld p jesus -> m 04/2021

-- vid6 clean up (if mistake while filming)
--DELETE FROM User WHERE username='darth';
--DELETE FROM User WHERE username='kenbe';
--DELETE FROM Patient WHERE surname='Anakin';
--DELETE FROM Patient WHERE surname='ObiWan';
--DELETE FROM Booking;

-- vid7
SELECT * FROM Booking;
-- gui: add new booking for anakin on 9:30  03/11
SELECT * FROM Booking;
-- gui: rm the booking just added
SELECT * FROM Booking;
-- gui: reschedule 9:00 booking to 9:30
SELECT * FROM Booking;

-- vid8
SELECT * FROM Activity;
-- gui: log in as Anakin
-- gui: access vm -> md -> mp -> mb tabs
-- gui: reschedule 9:30 booking to 9:00
-- gui: access al tab
-- gui: logout
SELECT * FROM Activity;
