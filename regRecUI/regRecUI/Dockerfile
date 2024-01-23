FROM ruby:3.2.2 as base

RUN apt-get update && apt-get install -y npm && npm install -g yarn

RUN mkdir -p /var/app
COPY . /var/app
WORKDIR /var/app

RUN bundle install

CMD sudo rm tmp/pids/server.pid

CMD rails s -b 0.0.0.0
