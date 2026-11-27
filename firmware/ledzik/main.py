import math
import random
import time
from math import floor, isnan

import json
from time import ticks_ms, ticks_diff, sleep_ms

from umqtt.simple import MQTTClient
import sys
import env

from lib.net import connect_to_wifi
from lib.fancyPrint import print_with_style, LOG_CHAR_CNT
from lib.idFile import get_my_id

client = "None"

# MY STATE
power = True
brightness = 80
temperature = 2700

# PWM STATE
freq = 5000
duty = 1024

# REAL STATE
pinout = 21
led = None

EVERY_ID = "<every>"
MY_ID = "none"

platform = "none"


def get_initial_state(mqtt_client, timeout_ms=1000):
    global brightness, power, temperature
    received = []

    print_with_style("INITIAL STATE")
    mqtt_client.set_callback(lambda topic, msg: received.append(msg))
    mqtt_client.connect()
    try:
        mqtt_client.connect()
        mqtt_client.subscribe(get_path_state())

        start = ticks_ms()
        while not received and ticks_diff(ticks_ms(), start) < timeout_ms:
            mqtt_client.check_msg()
            sleep_ms(50)
    except Exception as e:
        print("MQTT error:", e)
    finally:
        mqtt_client.disconnect()

    if not received:
        print("no retained state found")
        print('-' * LOG_CHAR_CNT)
        return
    try:
        data = json.loads(received[0].decode())
    except ValueError:
        print("invalid JSON in state")
        print('-' * LOG_CHAR_CNT)
        return

    print(received[0].decode())

    b = data.get('brightness')
    if isinstance(b, (int, float)) and not isnan(b):
        print(f"\tBRIGHTNESS: {b}")
        brightness = b

    p = data.get('power')
    if isinstance(p, bool):
        print(f"\tPOWER: {p}")
        power = p

    t = data.get('temperature')
    if isinstance(t, (int, float)) and not isnan(t):
        print(f"\tTEMPERATURE: {t}")
        temperature = t

    print('-' * LOG_CHAR_CNT)


def get_path_status():
    return f"devices/smart_led/{MY_ID}/status".encode()


def get_path_state():
    return f"devices/smart_led/{MY_ID}/state".encode()


def get_led_brightness():
    global brightness, duty
    u16 = math.pow(2,16)-1
    return int(brightness/100 * u16)


def process_state():
    global power
    global brightness
    global temperature
    print_with_style("CURRENT STATE", 40)
    print(f"power state: {'on' if power else 'off'}")
    print(f"brightness: {brightness}")
    print(f"color temperature: {temperature}")
    print(f"platform: {platform}")
    if platform == "esp32" or platform == "rp2":
        currDuty = power * get_led_brightness()
        print(f"powering led: {currDuty}")
        led.duty_u16(currDuty)
    print("-" * 40)


def on_message(topic, msg):
    print("Message incoming: ", topic.decode(), msg.decode())

    _, id, event = topic.decode().split("/")

    global power
    global brightness

    if event == "on":
        power = True
    elif event == "off":
        power = False
    elif event == "brightness":
        new_brightness = json.loads(msg.decode())['brightness']
        if not isnan(new_brightness):
            brightness = new_brightness

    if client:
        publish_state(client)
    process_state()


def get_subscriptions():
    return [
        f"events/{MY_ID}/on",
        f"events/{MY_ID}/off",
        f"events/{MY_ID}/brightness",
        f"events/smart_led-{EVERY_ID}/on",
        f"events/smart_led-{EVERY_ID}/off",
        f"events/smart_led-{EVERY_ID}/brightness",
    ]


def subscribe(client):
    print("SUBSCRIBING")
    for sub in get_subscriptions():
        print("\tto topic: ", sub)
        client.subscribe(sub.encode())


def getRandom(_min, _max):
    return _min + floor((random.random() * (_max - _min)))


def publish_state(publisher):
    global temperature
    temperature = 2700
    publisher.publish(
        get_path_state(),
        json.dumps({
            "brightness": brightness,
            "power": power,
            "temperature": temperature,
        }).encode(),
        retain=True,
        qos=1,
    )


def publish_status(publisher):
    publisher.publish(
        get_path_status(),
        json.dumps({
            "online": True
        }).encode(),
        retain=True,
        qos=1,
    )


def print_configuration():
    print("Starting Smart Ledzik firmware")
    print_with_style("CONFIGURATION DATA")
    print("MQTT BROKER and REST HOST: " + env.BROKER_HOST)
    print("MQTT BROKER PORT: " + str(env.BROKER_PORT))
    print("REST PORT: " + str(env.REST_POT))
    print("ID FILE PATH: " + env.ID_FILEPATH)
    print("STATIC MODE: " + "true" if env.STATIC else "false")
    print("-" * LOG_CHAR_CNT)


HEARTBEAT_S = 5


def main():
    global MY_ID
    global client
    print_configuration()

    MY_ID = get_my_id(sys, env.ID_FILEPATH)

    print("MY ID: " + MY_ID)
    print("STATUS PATH: " + get_path_status().decode())
    print("STATE PATH: " + get_path_state().decode())

    client = MQTTClient(
        f"smart-led-client-{MY_ID}",
        env.BROKER_HOST,
        env.BROKER_PORT,
        keepalive=30)

    get_initial_state(client)

    client.set_callback(on_message)
    client.set_last_will(
        get_path_status(),
        json.dumps({
            "online": False
        }).encode(),
        retain=True,
        qos=1
    )

    client.connect()

    subscribe(client)

    publish_status(client)

    publish_state(client)

    print("Now listening for messages")
    last = 0
    while True:
        if time.time() - last >= HEARTBEAT_S:
            client.ping()
            last = time.time()
        client.check_msg()
        time.sleep(0.1)


def realConfig():
    global led
    from machine import Pin, PWM
    led = PWM(Pin(pinout))
    led.freq(freq)
    led.duty_u16(duty)


if __name__ == "__main__":
    try:
        if sys.platform == "esp32" or sys.platform == "rp2":
            connect_to_wifi(env)
            platform = sys.platform
            realConfig()
        main()
    except Exception as e:
        print("Crashed:", e)
        time.sleep(2)
        machine.reset()