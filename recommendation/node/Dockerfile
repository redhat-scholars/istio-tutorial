FROM node:6.9.2
#FROM rhscl/nodejs-6-rhel7 

# Create directory for application
RUN mkdir -p /usr/src/app 
WORKDIR /usr/src/app

# Dependencies are installed here
COPY package.json /usr/src/app/
RUN npm install

# App sourcd
COPY . /usr/src/app/

EXPOSE 8080
CMD ["node", "recommendation.js"]
