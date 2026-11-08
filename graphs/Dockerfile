FROM python
WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir --upgrade pip \
 && pip install --no-cache-dir -r requirements.txt

COPY static static
copy templates templates
copy main.py main.py

CMD ["gunicorn", "--bind", "0.0.0.0:8080", "--workers", "2", "main:app"]