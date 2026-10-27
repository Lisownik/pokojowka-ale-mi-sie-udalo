import random
from math import floor, isnan
from time import sleep

import paho.mqtt.client as mqtt
import json
import time

EVERY_ID = "<every>"
MY_ID = "bulb-1"
TYPE = "smart_led"
power = "true"
brightness = 80

STATUS_TOPIC = f"devices/{TYPE}/{MY_ID}/status"
STATE_TOPIC = f"devices/{TYPE}/{MY_ID}/state"

SUBSCRIPTIONS = [
    (f"events/smart_led-{EVERY_ID}/off", 1),
    (f"events/smart_led-{EVERY_ID}/on", 1),
    (f"events/smart_led-{EVERY_ID}/brightness", 1),
    (f"events/{MY_ID}/off", 1),
    (f"events/{MY_ID}/on", 1),
    (f"events/{MY_ID}/brightness", 1),
]

def on_disconnect(client, userdata, flags, reason_code, properties):
    print(f"Disconnected: {reason_code}")


def on_message(client, userdata, msg):
    global power
    global brightness
    print(msg.topic, msg.payload.decode())
    _, _, event = msg.topic.split("/")

    match event:
        case "off":
            power = "false"
        case "on":
            power = "true"
        case "brightness":
            new =  json.loads(msg.payload.decode())["brightness"]
            if not isnan(new):
                brightness = new

    publish_state(client)



def on_connect(client, userdata, flags, reason_code, properties):
    if reason_code.is_failure:
        print(f"Connect failed: {reason_code}")
        return

    print("Connected")
    client.subscribe(SUBSCRIPTIONS)
    client.publish(STATUS_TOPIC, json.dumps({"online": True}), qos=1, retain=True)
    publish_state(client)


def getRandom(min, max):
    return min + floor((random.random() * (max - min)))

def publish_state(client):
    client.publish(STATE_TOPIC, json.dumps({
        "brightness": brightness,
        "power": power,
        "temperature": getRandom(2700, 4000),
    }), qos=1, retain=True)


client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
client.on_connect = on_connect
client.on_disconnect = on_disconnect
client.on_message = on_message

client.will_set(STATUS_TOPIC, json.dumps({"online": False}), qos=1, retain=True)
client.reconnect_delay_set(min_delay=1, max_delay=30)

client.connect_async("localhost", 1883, keepalive=5)
client.loop_forever()

publish_state(client)
# client.loop_stop()
# while True:
#     publish_state(client)
#     sleep(10)
# time.sleep(10)
# client.disconnect()
