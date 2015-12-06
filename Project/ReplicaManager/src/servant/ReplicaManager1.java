package servant;

public class ReplicaManager1 {

    public static void main(String[] args) {
        dlmsImpl _bank1Obj = new dlmsImpl(6003, 7002, 4000);
        _bank1Obj.rest_port[0] = 6004;
        _bank1Obj.rest_port[1] = 6005;
        dlmsImpl _bank2Obj = new dlmsImpl(6004, 7002, 4000);
        _bank2Obj.rest_port[0] = 6003;
        _bank2Obj.rest_port[1] = 6005;
        dlmsImpl _bank3Obj = new dlmsImpl(6005, 7002, 4000);
        _bank3Obj.rest_port[0] = 6003;
        _bank3Obj.rest_port[1] = 6004;
    }

}
