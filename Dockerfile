FROM python:3.7-rc

ENV DB_URL=postgresql://user:password@psql:5432/greetings

ADD greetings_app /greetings_app 

RUN pip install -r /greetings_app/requirements.txt

EXPOSE 5000

CMD ["python","/greetings_app/app.py"]
