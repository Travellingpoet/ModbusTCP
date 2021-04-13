package net.wimpi.modbustcp.bean;

/**
 * Time:2021/4/8
 * <p>
 * Author:VanDine
 * <p>
 * Description:
 */
public class TcpBean {
    //轿车运行状态
    private final String runningState = "";
    //当前楼层
    private final String rtFloor = "";
    //轿门状态
    private final String doorOpenOrClose = "";
    //轿厢是否有人
    private Boolean humanInOrNon;
    //警铃
    private final String faultCode = "";
    //紧急呼叫按下
    private Boolean emdgencyKeyWasPress;
    //累计运行时间
    private final String runAllTmr = "";
    //累计运行次数
    private final String runAllCnt = "";
    //停层门打开，长时间未关闭，默认时常120s
    private int liftStopDoorOpenOverTime = 120;
    //停层门关人,默认时常40s
    private int doorNoOpenCloseHuman = 40;
    //上行开门走车
    private final String liftuRunDoorNoClose = "";
    //下行开门走车
    private final String liftdRunDoorNoClose = "";
    //非平层停车关人
    private final String nonPceStopCloseHuman = "";
    //设备外部电源断开
    private final String triggerStat = "";

}
