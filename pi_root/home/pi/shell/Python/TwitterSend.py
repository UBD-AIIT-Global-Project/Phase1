#!/usr/bin/python
# -*-  coding: utf-8 -*-

import twitter

class TwitterSend:
	def __init__(self):
		self.msg=''

	def setMsg(self,msg):
                self.msg=msg

	def sendMsg(self):
                if self.msg == '':
                  self.msg='Warning!! Detect water level exceeded limit!!'
		tweets = []
		tweets.append(self.msg)
		tweet = tweets[0]

		api = twitter.Api(consumer_key ="GF4cNVRecexwrq5k9VBujY1kM",
			consumer_secret = "MRdiWtnPoFWhbmRVit1SHBJyixHgEIULcGNcYLaypGnzdagri0",
			access_token_key = "4634968388-gM2jFD5CCF5aNLgS9EY4uwdXrJQHP3czqf9ILxO",
			access_token_secret = "qzQKUO2poUIJjA2NZN83eDcoAtzJKvLEL9k8svLcNqyKc")
		api.PostUpdate(tweet)
