from flask import Flask, session, redirect, url_for, escape, request
from twilio.rest import TwilioRestClient
import twilio.twiml
import requests
import json
import httplib         

account_sid = "AC0dfe6ceec792af6b2a31aa508a3664ab"
auth_token  = "f07b6c83085406f4ee3e4f4b6cf72aa4"
client = TwilioRestClient(account_sid, auth_token)
app = Flask(__name__)
@app.route('/', methods=['GET', 'POST'])
def index():
    phonenumber = request.values.get('From', None)
    body = request.values.get('Body', None)
    action = json.loads(body)
    current = action['current']
    desired = action['desired']
    payload={'current':current,'desired':desired}
    url='http://school/index.php'	
    r = requests.post(url,data=payload)
    print(payload)
    print(r.text)
    response=r.text
    message = client.messages.create(body=response,
                            to=phonenumber,
                            from_="+441727260228") 
    return message.sid
# set the secret key.  keep this really secret:
app.secret_key = 'A0Zr98j/3yX R~XHH!jmN]LWX/,?RT'
if __name__ == '__main__':
    app.run(debug=True)
