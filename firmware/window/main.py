import math
import time

import json
from time import ticks_ms, ticks_diff, sleep_ms
from umqtt.simple import MQTTClient
import sys
import env
from ..lib.net import connect_to_wifi


# CONST VALS
LOG_CHAR_CNT = 60
client = "None"

# MY STATE

opened = False

# ID DEF
EVERY_ID = "<every>"
MY_ID = "none"


def get_arg_id(argv):
    for a in argv[1:]:
        if a.startswith("--id="):
            return a[5:]
    return None

def print_with_style(text, char_cnt = LOG_CHAR_CNT):
    text = text.strip()
    char = '-'
    chars = math.floor((char_cnt - len(text) - 2)/2)
    print(char * chars + " " + text + " " + char * chars)


def read_id_file():
    try:
        with open(env.ID_FILEPATH, 'r') as file:
            text = file.read()
            if text == "":
                raise ValueError("Id not stored in id.txt")
            return text
    except OSError or ValueError:
        content = ''
        reg_id = "tried-to-register-id-no-implementation"
        if not reg_id:
            raise Exception("No server no id file or id in file")
        with open(env.ID_FILEPATH, 'w') as file:
            file.write(reg_id)
        return reg_id

def get_my_id():
    arg_id = get_arg_id(sys.argv)
    if arg_id:
        return arg_id
    return read_id_file()

def get_path_status():
    return f"devices/window/{MY_ID}/status".encode()

def get_path_state():
    return f"devices/window/{MY_ID}/state".encode()


def get_initial_state(mqtt_client, timeout_ms=1000):
    global opened
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

    _open = data.get('open')
    if isinstance(_open, bool):
        print(f"\tOpened: {_open}")
        opened = _open

    print('-' * LOG_CHAR_CNT)

def process_state():
    global opened
    print_with_style("CURRENT STATE", 40)
    print(f"Opened state: {opened}")
    print("-" * 40)

def on_message(topic, msg):
    global opened
    print("Message incoming: ", topic.decode(), msg.decode())

    _, pr_id, event = topic.decode().split("/")


    opened = event == "open"

    process_state()
    if client:
        publish_state(client)

def publish_state(publisher):
    publisher.publish(
        get_path_state(),
        json.dumps({
            "open": opened
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

def get_subscriptions():
    return [
        f"events/{MY_ID}/open",
        f"events/{MY_ID}/close",
        f"events/window-{EVERY_ID}/open",
        f"events/window-{EVERY_ID}/close",
    ]

def subscribe(client):
    print("SUBSCRIBING")
    for sub in get_subscriptions():
        print("\tto topic: ", sub)
        client.subscribe(sub.encode())

def print_configuration():
    print("Starting Smart Ledzik firmware")
    print_with_style("CONFIGURATION DATA")
    print("MQTT BROKER and REST HOST: " + env.BROKER_HOST)
    print("MQTT BROKER PORT: " + str(env.BROKER_PORT))
    print("REST PORT: " + str(env.REST_POT))
    print("ID FILE PATH: " +  env.ID_FILEPATH)
    print("STATIC MODE: " + "true" if env.STATIC else "false")
    print("-" * LOG_CHAR_CNT)

def main():
    global MY_ID
    global client
    print_configuration()

    MY_ID = get_my_id()

    print("MY ID: " + MY_ID)
    print("STATUS PATH: " + get_path_status().decode())
    print("STATE PATH: " + get_path_state().decode())

    client = MQTTClient(f"window-client-{MY_ID}", env.BROKER_HOST, env.BROKER_PORT)

    get_initial_state(client)

    client.set_callback(on_message)
    client.set_last_will(
        get_path_status(),
        json.dumps({
            "online": False
        }).encode(),
        retain = True,
        qos = 1
    )

    client.connect()

    subscribe(client)

    publish_status(client)

    publish_state(client)

    print("Now listening for messages")
    while True:
        client.wait_msg()


if sys.platform == "esp32":
    connect_to_wifi(env)

if __name__ == "__main__":
    main()