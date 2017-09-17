from flask import Flask, request, jsonify
from flask_restful import Resource, Api
from gensim.summarization import summarize, keywords


app = Flask(__name__)
api = Api(app)


@app.route('/api/summarize/', methods=['POST', 'GET'])
def Summarize():
    data = request.json.get('data')
    response = {}
    try:
        response['result'] = {}
        response['result']['summary'] = summarize(data, ratio=0.20)
        response['result']['keywords'] = keywords(data, ratio=0.01).split('\n')
    except ValueError as e:
        response['result'] = str(e)
    return jsonify(response)


if __name__ == '__main__':
    app.run(host='127.0.0.1', debug=True, threaded=True)
