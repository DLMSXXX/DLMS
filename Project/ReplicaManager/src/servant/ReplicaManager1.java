package servant;

public class ReplicaManager1 {

	public static void main(String[] args) {
		dlmsImpl _bank1Obj = new dlmsImpl(2061, 7002);
		_bank1Obj.rest_port[0] = 2062;
		_bank1Obj.rest_port[1] = 2063;
		dlmsImpl _bank2Obj = new dlmsImpl(2062, 7002);
		_bank2Obj.rest_port[0] = 2061;
		_bank2Obj.rest_port[1] = 2063;
		dlmsImpl _bank3Obj = new dlmsImpl(2063, 7002);
		_bank3Obj.rest_port[0] = 2061;
		_bank3Obj.rest_port[1] = 2062;
	}

}
