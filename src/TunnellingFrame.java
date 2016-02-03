

public class TunnellingFrame {
	public byte headerLength;
	public byte version;
	public byte serviceTypeH;
	public byte serviceTypeL;
	public byte totalLengthH;
	public byte totalLengthL;
	public byte structLength;
	public byte CID;
	public byte SC;
	public byte reserved;
	public CEMIMessage cemiMessage;
	public ServiceMessage serviceMessage;

}
