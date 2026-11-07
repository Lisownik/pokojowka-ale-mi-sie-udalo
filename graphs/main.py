from flask import Flask, render_template, request
from urllib.parse import urlparse

app = Flask(__name__)


@app.route('/')
def base():
    o = urlparse(request.base_url)
    host = o.hostname
    return render_template("main/index.html", host=host)

@app.route('/room/<room_identifier>')
def room(room_identifier):
    o = urlparse(request.base_url)
    host = o.hostname
    return render_template("room/index.html", id=room_identifier, host=host)

@app.route('/smart_led/<led_identifier>')
def led(led_identifier):
    o = urlparse(request.base_url)
    host = o.hostname
    return render_template("bulb/index.html", id=led_identifier, host=host)

@app.route("/heater/<heater_identifier>")
def heater(heater_identifier):
    o = urlparse(request.base_url)
    host = o.hostname
    return render_template("heater/index.html", id=heater_identifier, host=host)

@app.route("/window/<window_identifier>")
def window(window_identifier):
    o = urlparse(request.base_url)
    host = o.hostname
    return render_template("window/index.html", id=window_identifier, host=host)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)