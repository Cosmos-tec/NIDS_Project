public class UserEntry {
    // Will contain source Ip
    private String attacker = "";
    private String suspiciousUser = "";

    // Will contain info on malicious user information
    private String srcPort = "";
    private String dstPort = "";
    private String protocol = "";
    private String attackType = "";
    private String attackDescription = "";

    //Packet info
    private String no = "";
    private String srcIP = "";
    private String dstIP = "";
    private String pCount = "";

    public void setpCount(String pCount) {
        this.pCount = pCount;
    }

    public String getpCount() {
        return pCount;
    }

    public void setpCount(int pCount) {

        this.pCount = Integer.toString(pCount);
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public String getDstIP() {
        return dstIP;
    }

    public void setDstIP(String dstIP) {
        this.dstIP = dstIP;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    private String size = "";

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    private int dataSize;

    public String getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(String threatLevel) {
        this.threatLevel = threatLevel;
    }

    private String threatLevel = "";

    public String getAttacker() {
        return attacker;
    }

    public void setAttacker(String attacker) {
        this.attacker = attacker;
    }

    public String getSuspiciousUser() {
        return suspiciousUser;
    }

    public void setSuspiciousUser(String suspiciousUser) {
        this.suspiciousUser = suspiciousUser;
    }

    public String getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }

    public String getDstPort() {
        return dstPort;
    }

    public void setDstPort(String dstPort) {
        this.dstPort = dstPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAttackType() {
        return attackType;
    }

    public void setAttackType(String attackType) {
        this.attackType = attackType;
    }

    public String getAttackDescription() {
        return attackDescription;
    }

    public void setAttackDescription(String attackDescription) {
        this.attackDescription = attackDescription;
    }

    public String all() {
        return getpCount() + " " + getNo() + " " + getSrcIP() +
                " " + getDstIP() + " " + getProtocol() + " " +
                getSize();
    }
}
