/*
//------------------------------------------------------------------------------
/// File       : create.sqlite.sql
/// Version    : 0.1
/// Created    : 2017-02-19
/// Modified   : 2017-02-19
///
/// Author     : William Chang
/// Email      : william@babybluebox.com
/// Website    : http://williamchang.org
///
/// Compatible : SQLite 3
//------------------------------------------------------------------------------
/// References:
/// https://www.tutorialspoint.com/sqlite/sqlite_data_types.htm
/// http://techreadme.blogspot.com/2012/11/sqlite-read-write-datetime-values-using.html
//------------------------------------------------------------------------------
*/

/* Code Snippets
//----------------------------------------------------------------------------*/
/*

Storage Classes
null
integer
real
text
blob

Data Types By Popularity
datetime
boolean

Get Current Date and Time (YYYY-MM-DD HH:MM:SS) for System.DateTime compatibility
datetime('now')

Get Current Date and Time (YYYY-MM-DD HH:MM:SS.SSS) for System.DateTime compatibility
strftime('%Y-%m-%d %H:%M:%f', 'now')
strftime('%Y-%m-%d %H:%M:%f', 'now', 'utc')
strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime')

Get Current Unix Timestamp aka Unix Epoch
strftime('%s', 'now')

Use Data Type for System.Guid (separated by hyphens, lowercase, no enclosed curly braces)
text

*/

/* Create Tables (columns are default "null")
//----------------------------------------------------------------------------*/
CREATE TABLE SystemLog (
    Id integer not null,
    ApplicationName text not null,
    DateCreated datetime not null,
    Thread text not null,
    Level text not null,
    Logger text not null,
    Message text not null,
    Exception text not null,
    PRIMARY KEY (Id)
);
INSERT INTO SystemLog (ApplicationName, DateCreated, Thread, Level, Logger, Message, Exception) VALUES ('Unknown', strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime'), '0', 'Debug', 'DefaultLogger', 'YourMessage1', 'YourException1');
INSERT INTO SystemLog (ApplicationName, DateCreated, Thread, Level, Logger, Message, Exception) VALUES ('Unknown', strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime'), '0', 'Debug', 'DefaultLogger', 'YourMessage2', 'YourException2');
INSERT INTO SystemLog (ApplicationName, DateCreated, Thread, Level, Logger, Message, Exception) VALUES ('Unknown', strftime('%Y-%m-%d %H:%M:%f', 'now', 'localtime'), '0', 'Debug', 'DefaultLogger', 'YourMessage3', 'YourException3');

CREATE TABLE User (
    Id text not null,
    Email text not null,
    Role text not null,
    Alias text not null,
    GameId text not null,
    DateCreated datetime not null,
    PRIMARY KEY (Id),
    FOREIGN KEY (GameId) REFERENCES Game (Id) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE Game (
    Id text not null,
    User1Id text not null,
    User1Score integer not null,
    User2Id text not null,
    User2Score integer not null,
    User3Id text not null,
    User3Score integer not null,
    Board text not null,
    Timer integer not null,
    State integer not null,
    UserWinnerId text not null,
    DateCreated datetime not null,
    PRIMARY KEY (Id),
    FOREIGN KEY (User1Id) REFERENCES User (Id) ON DELETE CASCADE ON UPDATE NO ACTION,
    FOREIGN KEY (User2Id) REFERENCES User (Id) ON DELETE CASCADE ON UPDATE NO ACTION,
    FOREIGN KEY (User3Id) REFERENCES User (Id) ON DELETE CASCADE ON UPDATE NO ACTION,
    FOREIGN KEY (UserWinnerId) REFERENCES User (Id) ON DELETE CASCADE ON UPDATE NO ACTION
);

/* Create Views
//----------------------------------------------------------------------------*/
