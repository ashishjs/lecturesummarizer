from flask import Flask, request, jsonify
from flask_restful import Resource, Api
from gensim.summarization import summarize, keywords

app = Flask(__name__)
api = Api(app)


@app.route('/api/summarize/', methods=['POST', 'GET'])
def Summarize():
    status = 200
    response = {
        'result': {}
    }

    try:
        print(request.json.keys())
        data = request.json.get('data')
        response['result']['summary'] = summarize(data, ratio=0.15)
        response['result']['keywords'] = keywords(data, ratio=0.01).split('\n')
    except ValueError as e:
        status = 400
        response['result'] = str(e)

    except AttributeError as e:
        status = 400
        response['result'] = "data field required"

    return jsonify(response), status


if __name__ == '__main__':
    app.run(host='127.0.0.1', debug=True, threaded=True)
