import random
from math import floor
from time import sleep

import paho.mqtt.client as mqtt
import json
import time

EVERY_ID = "<every>"
MY_ID = "bulb-1"
TYPE = "smart_led"
power = "true"

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

client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)


time.sleep(1)


client.publish(f"devices/{TYPE}/{MY_ID}/status", json.dumps({"online": True}))

# client.publish(f"devices/{TYPE}/{MY_ID}/state", json.dumps({"temperature": 80}))
client.subscribe(f"events/smart_led-{EVERY_ID}/off")
client.subscribe(f"events/smart_led-{EVERY_ID}/on")
client.subscribe(f"events/smart_led-{EVERY_ID}/brightness")

client.subscribe(f"events/{MY_ID}/off")
client.subscribe(f"events/{MY_ID}/on")
client.subscribe(f"events/{MY_ID}/brightness")


def on_disconnect(client, userdata, flags, reason_code, properties):
    print(f"Disconnected: {reason_code}")


def on_message(client, userdata, msg):
    global power
    print(msg.topic, msg.payload.decode())
    _, _, event = msg.topic.split("/")

    match event:
        case "off":
            power = "false"
        case "on":
            power = "true"


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
        "brightness": getRandom(1, 100),
        "power": power,
        "temperature": getRandom(2700, 4000),
    }), qos=1)

client.publish(f"devices/{TYPE}/{MY_ID}/state", json.dumps({
        "brightness": getRandom(1, 100),
        "power":  "true" if getRandom(0,2) else "false",
        "temperature": getRandom(2700, 4000)
    }))



client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
client.on_connect = on_connect
client.on_disconnect = on_disconnect
client.on_message = on_message

client.will_set(STATUS_TOPIC, json.dumps({"online": False}), qos=1, retain=True)
client.reconnect_delay_set(min_delay=1, max_delay=30)

client.connect_async("localhost", 1883, keepalive=30)
client.loop_forever()
# while True:
    # client.publish(f"devices/bulb/{MY_ID}/state", json.dumps({
    #     "brightness": getRandom(1, 100),
    #     "power":  "true" if getRandom(0,2) else "false",
    #     "temperature": getRandom(2700, 4000)
    # }))
    # sleep(1)
# time.sleep(10)
# client.loop_stop()
# client.disconnect()
