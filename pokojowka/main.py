import time
from time import sleep

import board
import busio
import adafruit_bme680

def interpret_air_quality(gas_resistance):
    if gas_resistance > 50000:
        return "Excellent"
    elif gas_resistance > 20000:
        return "Good"
    elif gas_resistance > 10000:
        return "Moderate"
    elif gas_resistance > 5000:
        return "Poor"
    else:
        return "Unhealthy"

def main():
    i2c = busio.I2C(board.SCL, board.SDA)

    for addr in [0x77, 0x76]:
        try:
            bme680 = adafruit_bme680.Adafruit_BME680_I2C(i2c, address=addr)
            print(f"BME680 detected at 0x{addr:02X}")
            break
        except Exception:
            continue
    else:
        print("BME680 not found on I2C bus.")
        return

    bme680.sea_level_pressure = 1013.25

    print("Monitoring environment with DIY air quality estimation...\n")
    while True:
        temp = bme680.temperature
        humidity = bme680.humidity
        pressure = bme680.pressure
        altitude = bme680.altitude
        gas = bme680.gas
        quality = interpret_air_quality(gas)

        print("=" * 40)
        print(f"Temperature     : {temp:.2f} °C")
        print(f"Humidity        : {humidity:.2f} %")
        print(f"Pressure        : {pressure:.2f} hPa")
        print(f"Altitude        : {altitude:.2f} m")
        print(f"Gas Resistance  : {gas:.2f} Ω")
        print(f"Air Quality     : {quality}")
        print("=" * 40)
        time.sleep(2)

if __name__ == "__main__":
    main()
