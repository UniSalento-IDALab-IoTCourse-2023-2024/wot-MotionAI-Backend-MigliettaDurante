import pymongo


client_mongo = pymongo.MongoClient("mongodb://localhost:27030/")
db = client_mongo["accelerometer_db"]
collection = db["data"]


def insert_data(data_entry):
    collection.insert_one(data_entry)
    print(f"Dati salvati: {data_entry}")

def delete_data():
    collection.delete_many({})
    print("Dati eliminati")


if __name__ == "__main__":
    data_entry = {"Timestamp": "2024-10-30T12:00:00Z", "Predizione": "test"}
    delete_data()
    insert_data(data_entry)
