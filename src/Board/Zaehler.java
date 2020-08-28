package Board;

import java.util.concurrent.atomic.AtomicInteger;

import tarik.board.control.TWUsb;
import tarik.board.control.TWUsbException;

public class Zaehler
{
	private static AtomicInteger count = new AtomicInteger(0);

	public static void main(String[] args)
	{
		System.out.println("Programm gestartet, warten auf Tastendruck");
		try
		{
			TWUsb.onBtnPressed(Zaehler::btnPressed);
			TWUsb.onBtn5Pressed(Zaehler::btn5Pressed);

			for (; true; count.incrementAndGet( ))
			{
				if (count.get( ) == 256)
					count.set(0);

				TWUsb.WriteAllDigital(count.get( ));
				Thread.sleep(400);
			}
		}
		catch (TWUsbException | InterruptedException e)
		{
			e.printStackTrace( );
		}
	}

	public static void btnPressed(int i)
	{
		System.out.println("BTN PRESSED: " + i);
	}

	public static void btn5Pressed( )
	{
		System.out.println("BTN 5 PRESSED - RESET");
		count.set(0);
	}
}
