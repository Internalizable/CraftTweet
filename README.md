<p align="center">
  <a href="https://discord.gg/VMCmgx6Sss"><img src="https://i.imgur.com/AioWqUf.png" alt="Discord Icon"/></a>
</p>

## CraftTweet - *Connects Twitter Accounts to Minecraft accounts*

<p align="center">
  <a href="https://discord.gg/VMCmgx6Sss"><img src="https://pbs.twimg.com/media/EttYQYmXAAY5D6B?format=png&name=large" alt="Test Icon"/></a>
</p>

## Overview
CraftTweet links any player's Minecraft account to Twitter using Twitter4J, making it easier for server owners to interact with their players' social media accounts.

## How does it work?
CraftTweet authenticates users based on Twitter's OAuth. You'd have to apply for a developer account in order to build an application for your own server. When authenticated on Twitter, users will then receive a pin to confirm ingame.

## Requirements

* A functioning MySQL database (to store keys).
* A functioning OAuth app (provided by apps.twitter.com)

## Installation
First, apply for a developer account using your network's twitter page. After applying for your developer account and gaining access to build applications, head over to your twitter's [developer dashboard]('https://developer.twitter.com/en/portal/dashboard').

[ATTACH=full]593513[/ATTACH]

Go to your Projects tab, and click Create App under the Standalone apps. Then, configure your app's information and save your API key and API secret key since they will be used in the configuration file (do not share those with anyone!). Assign permissions based on your needs (in most instances it's only Read + Write).

[ATTACH=full]593514[/ATTACH]

Drop CraftTweet.jar into your plugins folder and start your server.

## Configuration
After starting your sever, head over to your plugin's folder and edit the configuration file.

````YAML
twitter:
  pkey: somePublicKey
  skey: somePrivateKey
  useRedisBungee: false
  callback:
    state: false
    url: 127.0.0.1:8000/link/?uuid=%uuid%
    main-server: true
  limit: 5
  useSQL: true
mysql:
  host: localhost
  port: '3306'
  database: twitter
  username: root
messages:
  prefix: '&bTwitterLink &8|'
  noperms: '&cYou do not have the right to execute this command!'
  unknownformat: '&cUnknown format! Use /link to start.'
  general:
    isLinked: '&7Your current Minecraft account is &alinked &7to the Twitter account &a@%account%&7!'
    isNotLinked: '&7Your current Minecraft account is &cnot linked &7to any Twitter account!\n&7Use &a/link create &7to start the linking process.'
  link:
    success: '&aYou have succesfully linked your twitter account with your Minecraft account!'
    url: '&7Please click &a&lhere to authenticate your twitter account with your Minecraft account\n&7Use your &aPIN &7using /link confirm &aPIN &7to initiate the link.'
  error:
    token: '&cThe giving token is wrong, please try again!'
    norequest: '&cYou have to iniate a linkage request before confirming!'
    notlinked: '&cYou do not have a twitter account linked!'
    alreadylinked: '&You already have a twitter account linked!'
````


* %url% is substituted with the authentication URL.
* %account% is substituted with the user's account name.

## Web Server Configuration
````json
{
 "redirectLink": "http://127.0.0.1/link/"
}
````

## Commands
In order to link a Twitter account, the permission link.use should be applied.

* /link - Initial command.
* /link create - Fetches an authorization URL for authentication.
* /link confirm [PIN] - Confirms the PIN given when authenticated.

Additionally, this command can ONLY be executed by console.

* /link tweet [USERNAME] [TWEET] - Tweets the given message of an ONLINE (has to be online) user if he has a Twitter account linked to his twitter account.

## For Developers
CraftTweet has it's own API based on CompletableFutures and it's own queuing system. 
Take a look at [TwitterAPI](https://github.com/Internalizable/CraftTweet/blob/master/src/main/java/me/internalizable/crafttweet/crafttweet-common/api/TwitterAPI.java)'s github page based on CompletableFutures.

## Feedback
If you encounter any errors or issues, or have any feedback please do so by either sending me a direct message or replying to this resource's thread.
