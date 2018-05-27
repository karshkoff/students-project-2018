FROM python:3.7-rc

ENV DB_URL=sqlite:///foo.db

ADD greetings_app /greetings_app 

RUN pip install -r /greetings_app/requirements.txt

CMD ["python","/greetings_app/app.py"]

