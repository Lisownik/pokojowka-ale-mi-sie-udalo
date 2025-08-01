import asyncio
import datetime
import random
import websockets
import json
from dotenv import load_dotenv, dotenv_values
import os
# import board
# import busio
# import adafruit_bme680



class WebSocketClient:
    def __init__(self, uri="ws://localhost:8000"):
        self.uri = uri
        self.ws = None
        # Don't call connect here - instead use the async context below
        # self.bme680 = None

    async def connect(self):
        name = read_file_safe('./name.txt')
        self.ws = await websockets.connect(self.uri)
        print(f"✅ Connected to {self.uri}")

        # self.bme680 = init()
        # if self.bme680 is None:
        #     print("<UNK> BME680 not found on I2C bus.")
        #     return

        asyncio.create_task(start_with_name(self.ws, name))

        asyncio.create_task(self.receive_loop())
        asyncio.create_task(self.send_data())
        await asyncio.sleep(2)


    async def send_data(self):
        try:
            while True:
                temperature = round(random.uniform(15.0, 35.0), 1)  # 15-35°C
                humidity = random.randint(30, 90)  # 30-90%
                pressure = round(random.uniform(1000.0, 1040.0), 1)  # 980-1040 hPa
                quality = random.randint(1000, 50000)  # 1000-50000 ohms

                msg = {
                    "action": "response",
                    "params": {
                        "method": "room_data",
                        "type": "room",
                        "value": {
                            "date": datetime.datetime.now().isoformat(),
                            "temperature": temperature,
                            "humidity": humidity,
                            "pressure": pressure,
                            "quality": quality,
                        }
                    }
                }

                await self.ws.send(json.dumps(msg))
                print(f"> Sent: {msg}")
                await asyncio.sleep(5)
        except websockets.exceptions.ConnectionClosed:
            print("❌ WebSocket connection closed")

    # async def init(self):
    #     print("🔄 Sending init...")
    #     await self.send("init", {"msg": "hello"}, response_var="init_response")


    async def send(self, method, params=None, response_var=None):
        if not self.ws:
            print("❌ Not connected")
            return
        msg = {
            "action": "request",
            "params": {
                "method": method,
                "params": params or {}
            }
        }
        if response_var:
            msg["params"]["response_var"] = response_var
        await self.ws.send(json.dumps(msg))
        print(f"> Sent: {msg}")

    async def receive_loop(self):
        try:
            async for message in self.ws:
                print(f"< Received: {message}")
                handleMessage(json.loads(message))

        except websockets.exceptions.ConnectionClosed:
            print("❌ WebSocket connection closed")

def handleMessage(message):
    match message.get('action'):
        case 'request':
            match message.get('method'):
                case 'name.change':
                    print("CHANGE NAME")
                    print(message['params']['name'])
                    overwrite_file('./name.txt', message['params']['name'])
                    # asyncio.run(start_with_name(self.ws, name))

# def interpret_air_quality(gas_resistance):
#     if gas_resistance > 50000:
#         return "Excellent"
#     elif gas_resistance > 20000:
#         return "Good"
#     elif gas_resistance > 10000:
#         return "Moderate"
#     elif gas_resistance > 5000:
#         return "Poor"
#     else:
#         return "Unhealthy"

# def init():
#     i2c = busio.I2C(board.SCL, board.SDA)
#
#     for addr in [0x77, 0x76]:
#         try:
#             bme680 = adafruit_bme680.Adafruit_BME680_I2C(i2c, address=addr)
#             print(f"BME680 detected at 0x{addr:02X}")
#             bme680.sea_level_pressure = 1013.25
#             return bme680
#         except Exception:
#             continue
#     else:
#         print("BME680 not found on I2C bus.")
#         return None
#     return None

def overwrite_file(filename, content):
    try:
        with open(filename, 'w', encoding='utf-8') as file:
            file.write(content)
        print(f"Successfully wrote to '{filename}'")
    except Exception as e:
        print(f"An error occurred: {e}")


def read_file_safe(filename):
    try:
        with open(filename, 'r', encoding='utf-8') as file:
            content = file.read()
            return content if content else ""
    except FileNotFoundError:
        return ""
    except Exception as e:
        print(f"Error reading file: {e}")
        return ""

async def main():
    client = WebSocketClient(os.getenv("API_URL"))
    await client.connect()
    while True:
        await asyncio.sleep(1)

async def start_with_name(ws, name):
    msg = {
        "action": "request",
        "params": {
            "method": "room.name-start",
            "params": {
                "name": name,
            }
        }
    }

    await ws.send(json.dumps(msg))

if __name__ == "__main__":
    load_dotenv()
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print("Client stopped by user")