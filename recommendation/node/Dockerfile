FROM bucharestgold/centos7-s2i-nodejs:10.x

# Create directory for application
RUN mkdir -p /opt/app-root/src
WORKDIR /opt/app-root/src

# Dependencies are installed here
COPY package.json .
RUN npm install

# App sourcd
COPY . .

EXPOSE 8080
CMD ["npm", "start"]
