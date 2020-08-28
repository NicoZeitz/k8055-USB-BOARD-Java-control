package tarik.board.control;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

/**
 * Interface für das UsbBoard.
 * Um eine neue Instanz zu bekommen, führe die Methode USBBoard.getBoard() aus.
 * 
 * @author Tarik A., Nico Z.
 *
 */
public class TWUsb
{
	private static Thread eventThread = new Thread(TWUsb::eventLoop);
	private static AtomicBoolean runThread = new AtomicBoolean(false);
	private static __InternalUsbBoard __usbBoard;
	private static volatile IntConsumer __BtnPressed = null;
	private static volatile Runnable __Btn1Pressed = null;
	private static volatile Runnable __Btn2Pressed = null;
	private static volatile Runnable __Btn3Pressed = null;
	private static volatile Runnable __Btn4Pressed = null;
	private static volatile Runnable __Btn5Pressed = null;
	private static AtomicInteger __eventDebounceTime = new AtomicInteger(250);

	static
	{
		__usbBoard = Native.load("lib\\K8055D.dll", __InternalUsbBoard.class);
		if (__usbBoard == null)
		{
			throw new ExceptionInInitializerError("Load Libary failed");
		}
		try
		{
			setup( );
		}
		catch (TWUsbException | InterruptedException e)
		{
			throw new ExceptionInInitializerError("TWUsb initial setup failed");
		}
		eventThread.start( );
	}

	private TWUsb( )
	{
	}

	public static void setup( ) throws TWUsbException, InterruptedException
	{
		__usbBoard.OpenDevice(TWUsb.ADDRESSE_0);
		__usbBoard.ClearAllDigital( );
		Thread.sleep(100);
		__usbBoard.ClearAllAnalog( );
		Thread.sleep(100);
	}

	// ================================================================================================= EVENTS

	public static void onBtnPressed(IntConsumer action)
	{
		__BtnPressed = action;
	}

	public static void onBtn1Pressed(Runnable action)
	{
		__Btn1Pressed = action;
	}

	public static void onBtn2Pressed(Runnable action)
	{
		__Btn2Pressed = action;
	}

	public static void onBtn3Pressed(Runnable action)
	{
		__Btn3Pressed = action;
	}

	public static void onBtn4Pressed(Runnable action)
	{
		__Btn4Pressed = action;
	}

	public static void onBtn5Pressed(Runnable action)
	{
		__Btn5Pressed = action;
	}

	// ================================================================================================= EVENT LOOP

	public static void setEventDebounceTime(int millis)
	{
		__eventDebounceTime.set(millis);
	}

	private static void eventLoop( )
	{
		runThread.set(true);
		while (runThread.get( ))
		{
			try
			{
				int digital = TWUsb.ReadAllDigital( );
				if ((digital & 0b1) > 0 && __Btn1Pressed != null)
				{
					__Btn1Pressed.run( );
					Thread.sleep(__eventDebounceTime.get( ));
				}
				if ((digital & 0b10) > 0 && __Btn2Pressed != null)
				{
					__Btn2Pressed.run( );
					Thread.sleep(__eventDebounceTime.get( ));
				}
				if ((digital & 0b100) > 0 && __Btn3Pressed != null)
				{
					__Btn3Pressed.run( );
					Thread.sleep(__eventDebounceTime.get( ));
				}
				if ((digital & 0b1000) > 0 && __Btn4Pressed != null)
				{
					__Btn4Pressed.run( );
					Thread.sleep(__eventDebounceTime.get( ));
				}
				if ((digital & 0b10000) > 0 && __Btn5Pressed != null)
				{
					__Btn5Pressed.run( );
					Thread.sleep(__eventDebounceTime.get( ));
				}
				if (digital > 0 && __BtnPressed != null)
				{
					__BtnPressed.accept(digital);
					Thread.sleep(__eventDebounceTime.get( ));
				}
				Thread.sleep(1);
			}
			catch (TWUsbException e)
			{
				e.printStackTrace( );
			}
			catch (InterruptedException e)
			{
				return;
			}
		}
	}

	/*
	 * =================================================================================================
	 * TWUSB-BOARD METHODS
	 * 
	 * missing methods
	 * 
	 * public static void ShowDeviceVersionDialog() throws de.wenzlaff.twusb.schnittstelle.exception.TWUsbException;
	 * public static boolean isDllVersionValid(); {
	 * return getVersion().equals("24");
	 * }
	 * public static String getInfo()
	 * public static String getVersion();
	 */

	public static final java.lang.String VERSION_TWUSB = "1.0";
	public static final java.lang.String TWUSB_DLL_NAME = "TWUsb";
	public static final int ADDRESSE_0 = 0;
	public static final int ADDRESSE_1 = 1;
	public static final int ADDRESSE_2 = 2;
	public static final int ADDRESSE_3 = 3;
	public static final int ADDRESSE_AUTO_SEARCH = 99;
	public static final int DIGITALER_AUSGABE_KANAL_1 = 1;
	public static final int DIGITALER_AUSGABE_KANAL_2 = 2;
	public static final int DIGITALER_AUSGABE_KANAL_3 = 3;
	public static final int DIGITALER_AUSGABE_KANAL_4 = 4;
	public static final int DIGITALER_AUSGABE_KANAL_5 = 5;
	public static final int DIGITALER_AUSGABE_KANAL_7 = 7;
	public static final int DIGITALER_AUSGABE_KANAL_8 = 8;
	public static final int DIGITALER_EINGABE_KANAL_1 = 1;
	public static final int DIGITALER_EINGABE_KANAL_2 = 2;
	public static final int DIGITALER_EINGABE_KANAL_3 = 3;
	public static final int DIGITALER_EINGABE_KANAL_4 = 4;
	public static final int DIGITALER_EINGABE_KANAL_5 = 5;
	public static final boolean DIGITALER_EINGABE_KANAL_AN = true;
	public static final boolean DIGITALER_EINGABE_KANAL_AUS = false;
	public static final boolean DIGITALER_EINGABE_KANAL_TASTE_GEDRUECKT = true;
	public static final boolean DIGITALER_EINGABE_KANAL_TASTE_NICHT_GEDRUECKT = false;
	public static final int ANALOGER_EINGABE_KANAL_1 = 1;
	public static final int ANALOGER_EINGABE_KANAL_2 = 2;
	public static final int ANALOGER_AUSGABE_KANAL_1 = 1;
	public static final int ANALOGER_AUSGABE_KANAL_2 = 2;
	public static final boolean DEVICE_NOT_CONNECT = false;
	public static final int DEVICE_NOT_FOUND = -1;
	public static final int DATA_FEHLER = -1;
	public static final long ABFRAGE_INTERVAL_100_MILLISEKUNDEN = 100L;
	public static final long ABFRAGE_INTERVAL_500_MILLISEKUNDEN = 500L;
	public static final long ABFRAGE_INTERVAL_1_SEKUNDEN = 1000L;
	public static final long ABFRAGE_INTERVAL_5_SEKUNDEN = 5000L;
	public static final long ABFRAGE_INTERVAL_10_SEKUNDEN = 10000L;
	public static final long START_IN_1_SEKUNDE = 1000L;
	public static final long START_IN_10_SEKUNDEN = 10000L;
	public static final long START_IN_1_MINUTE = 60000L;
	public static final long START_IN_10_MINUTEN = 600000L;

	public static int OpenDevice(int CardAddress) throws TWUsbException
	{
		return __usbBoard.OpenDevice(CardAddress);
	}

	// modified to exit the event Loop
	public static void CloseDevice( )
	{
		runThread.set(false);
		__usbBoard.CloseDevice( );
	}

	public static int SearchDevices( ) throws TWUsbException
	{
		return __usbBoard.SearchDevices( );
	}

	public static int SetCurrentDevice(int lngCardAddress) throws TWUsbException
	{
		return __usbBoard.SetCurrentDevice(lngCardAddress);
	}

	public static int Version( )
	{
		return __usbBoard.Version( );
	}

	public static int ReadAnalogChannel(int Channel) throws TWUsbException
	{
		return __usbBoard.ReadAnalogChannel(Channel);
	}

	// not in x86-jar?
	public static void ReadAllAnalog(IntByReference Data1, IntByReference Data2)
			throws TWUsbException
	{
		__usbBoard.ReadAllAnalog(Data1, Data2);
	}

	public static void OutputAnalogChannel(int Channel, int Data) throws TWUsbException
	{
		__usbBoard.OutputAnalogChannel(Channel, Data);
	}

	public static void OutputAllAnalog(int Data1, int Data2) throws TWUsbException
	{
		__usbBoard.OutputAllAnalog(Data1, Data2);
	}

	public static void ClearAnalogChannel(int Channel) throws TWUsbException
	{
		__usbBoard.ClearAnalogChannel(Channel);
	}

	public static void ClearAllAnalog( ) throws TWUsbException
	{
		__usbBoard.ClearAllAnalog( );
	}

	public static void SetAnalogChannel(int Channel) throws TWUsbException
	{
		__usbBoard.SetAnalogChannel(Channel);
	}

	public static void SetAllAnalog( ) throws TWUsbException
	{
		__usbBoard.SetAllAnalog( );
	}

	public static void WriteAllDigital(int Data) throws TWUsbException
	{
		__usbBoard.WriteAllDigital(Data);
	}

	public static void ClearDigitalChannel(int Channel) throws TWUsbException
	{
		__usbBoard.ClearDigitalChannel(Channel);
	}

	public static void ClearAllDigital( ) throws TWUsbException
	{
		__usbBoard.ClearAllDigital( );
	}

	public static void SetDigitalChannel(int Channel) throws TWUsbException
	{
		__usbBoard.SetDigitalChannel(Channel);
	}

	public static void SetAllDigital( ) throws TWUsbException
	{
		__usbBoard.SetAllDigital( );
	}

	public static boolean ReadDigitalChannel(int Channel) throws TWUsbException
	{
		return __usbBoard.ReadDigitalChannel(Channel);
	}

	public static int ReadAllDigital( ) throws TWUsbException
	{
		return __usbBoard.ReadAllDigital( );
	}

	public static void ResetCounter(int CounterNr) throws TWUsbException
	{
		__usbBoard.ResetCounter(CounterNr);
	}

	public static int ReadCounter(int CounterNr) throws TWUsbException
	{
		return __usbBoard.ReadCounter(CounterNr);
	}

	public static void SetCounterDebounceTime(int CounterNr, int DebounceTime) throws TWUsbException
	{
		__usbBoard.SetCounterDebounceTime(CounterNr, DebounceTime);
	}

	// not in x86-jar?
	public static int ReadBackDigitalOut( ) throws TWUsbException
	{
		return __usbBoard.ReadBackDigitalOut( );
	}

	// not in x86-jar?
	public static void ReadBackAnalogOut(IntByReference Buffer) throws TWUsbException
	{
		__usbBoard.ReadBackAnalogOut(Buffer);
	}

	private static interface __InternalUsbBoard extends Library
	{
		int OpenDevice(int CardAddress);

		void CloseDevice( );

		int SearchDevices( );

		int SetCurrentDevice(int lngCardAddress);

		int Version( );

		int ReadAnalogChannel(int Channel);

		void ReadAllAnalog(IntByReference Data1, IntByReference Data2);

		void OutputAnalogChannel(int Channel, int Data);

		void OutputAllAnalog(int Data1, int Data2);

		void ClearAnalogChannel(int Channel);

		void ClearAllAnalog( );

		void SetAnalogChannel(int Channel);

		void SetAllAnalog( );

		void WriteAllDigital(int Data);

		void ClearDigitalChannel(int Channel);

		void ClearAllDigital( );

		void SetDigitalChannel(int Channel);

		void SetAllDigital( );

		boolean ReadDigitalChannel(int Channel);

		int ReadAllDigital( );

		void ResetCounter(int CounterNr);

		int ReadCounter(int CounterNr);

		void SetCounterDebounceTime(int CounterNr, int DebounceTime);

		int ReadBackDigitalOut( );

		void ReadBackAnalogOut(IntByReference Buffer);
	}
}