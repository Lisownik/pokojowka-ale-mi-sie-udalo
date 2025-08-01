import asyncio
import datetime

import websockets
import json
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
        self.ws = await websockets.connect(self.uri)
        print(f"✅ Connected to {self.uri}")

        # self.bme680 = init()
        # if self.bme680 is None:
        #     print("<UNK> BME680 not found on I2C bus.")
        #     return

        asyncio.create_task(self.receive_loop())
        asyncio.create_task(self.send_data())


    async def send_data(self):
        try:
            while True:
                # temp = self.bme680.temperature
                # humidity = self.bme680.humidity
                # pressure = self.bme680.pressure
                # altitude = self.bme680.altitude
                # gas = self.bme680.gas
                # quality = interpret_air_quality(gas)

                msg = {
                    "action": "request",
                    "params": {
                        "method": "room.get",
                        "responseVar": "temp"
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

        except websockets.exceptions.ConnectionClosed:
            print("❌ WebSocket connection closed")

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

async def main():
    client = WebSocketClient()
    await client.connect()
    # Keep the client running
    while True:
        await asyncio.sleep(1)

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print("Client stopped by user")