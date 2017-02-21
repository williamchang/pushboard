/**
@file
    SystemLog.java
@author
    William Chang
@version
    0.1
@date
    - Created: 2017-02-16
    - Modified: 2017-02-16
    .
@note
    References:
    - General:
        - https://logging.apache.org/log4net/release/config-examples.html
        .
    .
*/

package data.entities;

import java.util.Date;

public class SystemLog {
    private int id;
    private String applicationName;
    private Date dateCreated;
    private String thread;
    private String level;
    private String logger;
    private String message;
    private String exception;

    public SystemLog() {
        this.id = 0;
        this.applicationName = "";
        this.dateCreated = new Date();
        this.thread = "";
        this.level = "";
        this.logger = "";
        this.message = "";
        this.exception = "";
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getException() {
        return exception;
    }

    public int getId() {
        return id;
    }

    public String getLevel() {
        return level;
    }

    public String getLogger() {
        return logger;
    }

    public String getMessage() {
        return message;
    }

    public String getThread() {
        return thread;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }
}
