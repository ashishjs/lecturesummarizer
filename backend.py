from flask import Flask, request, jsonify
from flask_restful import Resource, Api
from gensim.summarization import summarize, keywords

app = Flask(__name__)
api = Api(app)


@app.route('/api/summarize/', methods=['POST', 'GET'])
def Summarize():
    response = {
        'result': {}
    }

    try:
        data = request.json.get('data')
        response['result']['summary'] = summarize(data, ratio=0.20)
        response['result']['keywords'] = keywords(data, ratio=0.01).split('\n')
    except ValueError as e:
        response['result'] = str(e)

    except AttributeError as e:
        response['result'] = "Missing Text"

    return jsonify(response)


if __name__ == '__main__':
    app.run(host='127.0.0.1', debug=True, threaded=True)
