USE projectdatabase;

DROP TABLE if EXISTS attackInfo;
DROP TABLE if EXISTS protocol;
DROP TABLE if EXISTS attackType;
DROP TABLE if EXISTS packetInfo;

Create table attackInfo (
  	Aid INT(100) AUTO_INCREMENT, 
  	userID varchar (16) NOT NULL,
  	protocol varchar (30) NOT NULL,
  	ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	attackType varchar (30) NOT NULL,
  	CONSTRAINT pk_attackInfo PRIMARY KEY (Aid)
);

Create table protocol (
	Pid INT(100) AUTO_INCREMENT,
	Aid INT(100) NOT NULL,
  	ptype VARCHAR (10) NOT NULL,
  	pport INT NOT NULL,
  	CONSTRAINT pk_protocol PRIMARY KEY (Pid),
  	CONSTRAINT fk_protocol FOREIGN KEY (Aid)
  		REFERENCES attackInfo (Aid)
);

CREATE TABLE attackType (
	ATid INT(100) AUTO_INCREMENT,
	Aid INT(100) NOT NULL,
	Pid INT(100) NOT NULL,
	attackName VARCHAR (100) NOT NULL,
	attackDescription VARCHAR (255),
	CONSTRAINT pk_attackType PRIMARY KEY (ATid),
	CONSTRAINT fk_attackType FOREIGN KEY (Aid)
		REFERENCES attackInfo (Aid),
	CONSTRAINT fk_attackType0 FOREIGN KEY (Pid)
		REFERENCES protocol (Pid)
);

Create table packetInfo (
  	packetID INT(100) AUTO_INCREMENT, 
  	srcIP VARCHAR (16) NOT NULL,
  	size INT (255) NOT NULL,
  	srcPort VARCHAR (20) NOT NULL,
  	dstPort VARCHAR (20) NOT NULL,
  	protocol VARCHAR (10) NOT NULL,
  	CONSTRAINT pk_packetInfo PRIMARY KEY (packetID)
);

