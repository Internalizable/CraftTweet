const http = require("http");
const url = require('url');
const querystring = require('querystring');
const axios = require('axios');
const redis = require("redis");
const express = require('express');
const config = require('config');
const app = new express();

const host = 'localhost';
const port = 8000;

var publisher = redis.createClient();

const redirectLink = config.get('redirectLink');

app.get('/api/link', function (req, res) {
	const queryObject = url.parse(req.url,true).query;

	if(queryObject.uuid) {
		if(queryObject.oauth_token) {
			if(queryObject.oauth_verifier) {

				axios.post('https://api.twitter.com/oauth/access_token?oauth_token=' + queryObject.oauth_token + '&oauth_verifier=' + queryObject.oauth_verifier)
					.then(response => {
						var returnData = querystring.parse(response.data);

						returnData.uuid = queryObject.uuid;

						publisher.publish("twitter-oauth-callback", JSON.stringify(returnData));

						res.redirect(redirectLink + '?uuid=' + queryObject.uuid + '&display_name=' + returnData.screen_name);
					})
					.catch(error => {
						if(error.response.status == '401') {
							res.redirect(redirectLink);
						}
					});
			}
		}

	}

});

app.listen(8000);