package gg.uhc.keyboardcolours.api

class SdkInterface(file: String = "SDK.dll", device: Device.Value) {

  import gg.uhc.keyboardcolours.jna.SdkJnaInterface

  val sdk = SdkJnaInterface(file)
  sdk.SetControlDevice(device.id)

  def isDevicePluggedIn = sdk.IsDevicePlug
  def getDeviceLayout: Option[KeyboardLayout.Value] = KeyboardLayout.values.find(_.id == sdk.GetDeviceLayout)
  def setControllingLeds(enabled: Boolean): Boolean = sdk.EnableLedControl(enabled)
  def useEffect(effect: LedEffect.Value): Boolean = sdk.SwitchLedEffect(effect.id)
  def setAllKeys(ledColour: LedColour): Boolean = sdk.SetFullLedColor(ledColour.red, ledColour.blue, ledColour.green)
  def setLedColor(location: Location, ledColour: LedColour): Boolean = sdk.SetLedColor(location.y, location.x, ledColour.red, ledColour.blue, ledColour.green)
  def flushBuffer = sdk.RefreshLed(false)
  def enableAutoRefreshLeds = sdk.RefreshLed(true)

  def setKeyInterrupt(enable: Boolean): Boolean = sdk.EnableKeyInterrupt(enable)
  def getNowVolumePeekValue = sdk.GetNowVolumePeekValue
  def getRamUsage = sdk.GetRamUsage
  def getNowCPUUsage = sdk.GetNowCPUUsage

  // TODO void SetKeyCallBack(KEY_CALLBACK callback);
  // TODO bool SetAllLedColor(COLOR_MATRIX colorMatrix);
  // TODO TCHAR * GetNowTime();
}
