DROP DATABASE IF EXISTS jemstone;
CREATE DATABASE jemstone;
USE jemstone;
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
  FOREIGN KEY (username) REFERENCES User (username) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE TABLE Patient (
  pid INTEGER AUTO_INCREMENT PRIMARY KEY,
  forename VARCHAR(10) NOT NULL,
  surname VARCHAR(10) NOT NULL,
  gender CHAR(1) CHECK (gender IN ('M', 'F', 'O')) NOT NULL,
  dob DATE NOT NULL,
  phoneNo INTEGER(11),
  did INTEGER,
  username CHAR(5) NOT NULL,
  FOREIGN KEY (username) REFERENCES User (username) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (did) REFERENCES Doctor (did) ON UPDATE CASCADE ON DELETE SET NULL
);
CREATE TABLE Message (
  msgid INTEGER AUTO_INCREMENT PRIMARY KEY,
  date DATE NOT NULL,
  time TIME NOT NULL,
  msgHead TEXT NOT NULL,
  msgBody TEXT,
  username CHAR(5) NOT NULL,
  FOREIGN KEY (username) REFERENCES User (username) ON UPDATE CASCADE ON DELETE CASCADE
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
INSERT INTO User VALUES ('jjrf2', 'tired', 'R');
INSERT INTO User VALUES ('jas', 'seinfeld', 'D');
INSERT INTO User VALUES ('glc', 'costanza', 'D');
INSERT INTO User VALUES ('ck', 'kramer', 'D');
INSERT INTO User VALUES ('eb', 'benes', 'D');
INSERT INTO User VALUES ('prrrr', 'the', 'P');
INSERT INTO User VALUES ('darth', 'sand', 'P');
INSERT INTO User VALUES ('kenbe', 'highground', 'P');

INSERT INTO Message (time, date, msgHead, msgBody, username) VALUES ('23:26', '2021/02/24', 'Notice of new booking', 'Your patient Donkey Kong has scheduled a new meeting.', 'jjrf2');
INSERT INTO Message (time, date, msgHead, msgBody, username) VALUES ('00:38', '2021/02/25', 'Notice of cancelled booking', 'Your patient Donkey Kong has cancelled a meeting.', 'jjrf2');
INSERT INTO Message (time, date, msgHead, msgBody, username) VALUES ('11:30', '2021/02/25', 'What\'s the deal...', 'You like jazz?', 'jas');
INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username, background) VALUES ('J', 'Seinfeld', '5A', '07395924835', 'jas', 'According to all known laws of aviation, there is no way a bee should be able to fly. Its wings are too small to get its fat little body off the ground. The bee, of course, flies anyway because bees don\'t care what humans think is impossible.');
INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('G', 'Costanza', '2B', '07823458234', 'glc');
INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('C', 'Kramer', '5B', '05923995721', 'ck');
INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('E', 'Benes', '4C', '02843859628', 'eb');

INSERT INTO Patient (forename, surname, gender, dob, phoneNo, did, username) VALUES ('Perry', 'Platypus', 'M', '2010-04-05', '0782719365', 1, 'prrrr');
INSERT INTO Patient (forename, surname, gender, dob, phoneNo, did, username) VALUES ('ObiWan', 'Kenobi', 'M', '1990-02-01', '07234567891', 4, 'kenbe');
INSERT INTO Patient (forename, surname, gender, dob, phoneNo, did, username) VALUES ('Anakin', 'Skywalker', 'M', '1969-04-20', '07234567890', 3, 'darth');

INSERT IGNORE INTO Booking VALUES (1, (SELECT pid FROM Patient WHERE forename='Perry'), '2021-03-10', '10:00:00');
INSERT IGNORE INTO Booking VALUES (3, (SELECT pid FROM Patient WHERE forename='Anakin'), '2021-03-11', '09:00:00');
INSERT IGNORE INTO Booking VALUES (4, (SELECT pid FROM Patient WHERE forename='Obi-Wan'), '2021-03-12', '11:00:00');
INSERT IGNORE INTO Booking VALUES (4, (SELECT pid FROM Patient WHERE forename='Obi-Wan'), '2021-03-15', '12:00:00');
INSERT IGNORE INTO Booking VALUES (1, (SELECT pid FROM Patient WHERE forename='Perry'), '2021-02-11', '09:00:00');
INSERT IGNORE INTO Booking VALUES (1, (SELECT pid FROM Patient WHERE forename='Perry'), '2021-02-12', '10:30:00');
INSERT IGNORE INTO Booking VALUES (3, (SELECT pid FROM Patient WHERE forename='Anakin'), '2021-02-15', '11:00:00');

DROP DATABASE IF EXISTS testDouble;
CREATE DATABASE testDouble;
