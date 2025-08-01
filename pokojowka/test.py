import asyncio
from conn import WebSocketClient

async def main():
    client = WebSocketClient()
    await client.connect()

    while True:
        method = input("Method: ")
        await client.send(method, {"foo": "bar"})

asyncio.run(main())
