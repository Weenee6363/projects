package converter;

public class ByteTest
{

	public static void main(String[] args)
	{
		byte unit_state = (byte) -124;
		System.out.println(Integer.toBinaryString(unit_state));
		byte chain_state = (byte)((unit_state >> 6) & 0x2);
		System.out.println(Integer.toBinaryString(chain_state));
		System.out.println(String.valueOf(chain_state));
	}

}
