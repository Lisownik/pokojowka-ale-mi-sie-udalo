FROM node:22-alpine

WORKDIR /app

COPY package.json ./
RUN npm install

COPY . .

EXPOSE 1234
EXPOSE 1883

CMD ["npm", "run", "prod"]
