# test_server.py
from flask import Flask
app = Flask(__name__)

@app.route('/test')
def test():
    return "Hallo von der Test-API!"

app.run(host='0.0.0.0', port=8080)
