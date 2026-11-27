from fancyPrint import print_with_style

def connect_to_wifi(env):
    import network
    import time
    print_with_style("WIFI CONFIG")
    print("SSID: ", env.WIFI_SSID, ", PASSWORD: ", env.WIFI_PASSWORD)
    wlan = network.WLAN(network.STA_IF)
    wlan.active(True)

    print("Scan:")
    for ap in wlan.scan():
        print(ap[0], "ch", ap[2], "rssi", ap[3], "sec", ap[5])

    wlan.connect(env.WIFI_SSID, env.WIFI_PASSWORD)
    for i in range(30):
        s = wlan.status()
        print("status:", s)
        if s == 3 or s < 0:
            break
        time.sleep(1)
    if wlan.status() != 3:
        raise RuntimeError("WiFi failed, status %d" % wlan.status())

    print("Connected:", wlan.ifconfig())
