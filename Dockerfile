FROM python:3.7-rc
ENV DB_URL=sqlite:///foo.db
ADD greetings_app /opt/greetings_app
RUN pip install -r /opt/greetings_app/requirements.txt
EXPOSE 5000
CMD ["python","/opt/greetings_app/app.py"]