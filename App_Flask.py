import pymongo
from flask import Flask, jsonify

app = Flask(__name__)

# Mi collego a MongoDB
client_mongo = pymongo.MongoClient("mongodb://localhost:27030/")
db = client_mongo["accelerometer_db"]
collection = db["data"]


@app.route('/data', methods=['GET'])
def get_data():
    def get_history():
        try:
            data = list(collection.find({}, {'_id': 0}))
            return jsonify(data), 200
        except Exception as e:
            print(f"Errore nel recupero dei dati: {e}")
            return jsonify({"error": "Errore nel recupero dei dati"}), 500

    return get_history()


if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5001)
