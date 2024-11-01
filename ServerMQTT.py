import json
import pymongo
from paho.mqtt import client as mqtt_client
from datetime import datetime

# Configura il broker MQTT
BROKER = 'test.mosquitto.org'
PORT = 1883
TOPIC = "accelerometer/prediction"
CLIENT_ID = "test_subscriber"

print("Server MQTT in esecuzione...")

# Connessione a MongoDB
client_mongo = pymongo.MongoClient("mongodb://localhost:27030/")
db = client_mongo["accelerometer_db"]
collection = db["data"]


def on_message(client, userdata, message):

    data_string = message.payload.decode()
    data_json = json.loads(data_string)

    device_id = data_json.get("device_id")
    activity = data_json.get("activity")

    print(f"Device ID: {device_id}, Activity: {activity}")

    data_entry = {
        "Timestamp": datetime.now().strftime("%d-%m-%Y %H:%M:%S"),
        "Device ID": device_id,
        "Predizione": activity
    }

    collection.insert_one(data_entry)
    print(f"Dati salvati: {data_entry}")


def connect_mqtt():
    client = mqtt_client.Client(CLIENT_ID)
    client.connect(BROKER, PORT)
    return client


def subscribe(client):
    client.subscribe(TOPIC)
    client.on_message = on_message


def run():
    client = connect_mqtt()
    subscribe(client)
    client.loop_forever()


if __name__ == "__main__":
    run()
