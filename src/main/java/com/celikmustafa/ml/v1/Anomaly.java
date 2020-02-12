package com.celikmustafa.ml.v1;

import java.util.Comparator;

public class Anomaly {
    ServerLog currentServerLog;
    ServerLog comparedServerLog;
    ServerLog serverLogDifference;

    public Anomaly(ServerLog currentServerLog, ServerLog comparedServerLog) {
        this.currentServerLog = currentServerLog;
        this.comparedServerLog = comparedServerLog;
        calculateServerLogDifference();
    }

    public ServerLog calculateServerLogDifference(){
        this.serverLogDifference = currentServerLog.getDifference(comparedServerLog);
        return this.serverLogDifference;
    }

    public ServerLog getServerLogDifference() {
        return serverLogDifference;
    }

    public void setServerLogDifference(ServerLog serverLogDifference) {
        this.serverLogDifference = serverLogDifference;
    }

    public ServerLog getCurrentServerLog() {
        return currentServerLog;
    }

    public void setCurrentServerLog(ServerLog currentServerLog) {
        this.currentServerLog = currentServerLog;
    }

    public ServerLog getComparedServerLog() {
        return comparedServerLog;
    }

    public void setComparedServerLog(ServerLog comparedServerLog) {
        this.comparedServerLog = comparedServerLog;
    }


    public static Comparator<Anomaly> serverLogRamComparator = new Comparator<Anomaly>() {

        public int compare(Anomaly a1, Anomaly a2) {
            Double s1_ = a1.getServerLogDifference().getRam();
            Double s2_ = a2.getServerLogDifference().getRam();
            //ascending order
            //return s1_.compareTo(s2_);

            //descending order
            return s2_.compareTo(s1_);
        }};

    @Override
    public String toString() {
        return "com.celikmustafa.ml.v1.Anomaly{" +
                "currentServerLog=" + currentServerLog +
                ", comparedServerLog=" + comparedServerLog +
                ", serverLogDifference=" + serverLogDifference +
                '}';
    }
}
