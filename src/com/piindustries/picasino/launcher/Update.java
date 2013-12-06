
package com.piindustries.picasino.launcher;

public class Update {
    private String serverVersion;
    private String clientVersion;
    private String updateURL;
    private boolean updateNeeded;

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public boolean isUpdateNeeded() {
        return updateNeeded;
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    public String getUpdateURL() {
        return updateURL;
    }

    public void setUpdateURL(String updateURL) {
        this.updateURL = updateURL;
    }
}
