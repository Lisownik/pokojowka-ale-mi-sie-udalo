import random
from math import floor
from time import sleep

import paho.mqtt.client as mqtt
import json
import time

MY_ID = "room-1"

client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
client.will_set(f"devices/room/{MY_ID}/status", json.dumps({"online": False}), qos=1, retain=True)

client.connect("localhost", 1883, 60)

time.sleep(1)


client.publish(f"devices/room/{MY_ID}/status", json.dumps({"online": True}))

client.publish(f"devices/room/{MY_ID}/state", json.dumps({"temperature": 80}))

client.subscribe(f"events/window-1/close")
client.subscribe(f"events/window-1/open")

def onMessage(client, _, msg):
    print("GOT THE WINDOW-1 CLOSE EVENT! YUPI")
    print(msg.topic, msg.payload.decode())

def getRandom(min, max):
    return min + floor((random.random() * (max - min)))

client.on_message =  onMessage
client.loop_start()
while True:
    client.publish(f"devices/room/{MY_ID}/state", json.dumps({"temperature": getRandom(0,80)}))
    sleep(5)
# time.sleep(10)
# client.loop_stop()
# client.disconnect()
