package gg.uhc.keyboardcolours.jna

import com.sun.jna.Library

trait SdkJnaInterface extends Library {
  def IsDevicePlug: Boolean
  def GetDeviceLayout: Int
  def EnableLedControl(enabled: Boolean): Boolean
  def SwitchLedEffect(index: Int): Boolean
  def SetFullLedColor(r: Int, g: Int, b: Int): Boolean
  def SetLedColor(row: Int, column: Int, r: Int, g: Int, b: Int): Boolean
  def SetControlDevice(index: Int)
  def RefreshLed(auto: Boolean): Boolean
  def EnableKeyInterrupt(enable: Boolean): Boolean
  def GetNowVolumePeekValue: Float
  def GetRamUsage: Int
  def GetNowCPUUsage: Long

  // TODO void SetKeyCallBack(KEY_CALLBACK callback);
  // TODO bool SetAllLedColor(COLOR_MATRIX colorMatrix);
  // TODO TCHAR * GetNowTime();
}

object SdkJnaInterface {
  import com.sun.jna.Native

  def apply(file: String): SdkJnaInterface = Native.loadLibrary(
    file,
    classOf[SdkJnaInterface]
  ).asInstanceOf[SdkJnaInterface]
}