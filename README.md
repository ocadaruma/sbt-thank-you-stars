# sbt-thank-you-stars

[![Build Status](https://travis-ci.org/ocadaruma/sbt-thank-you-stars.svg?branch=master)](https://travis-ci.org/ocadaruma/sbt-thank-you-stars)

Give your dependencies stars on GitHub!

This is a sbt-ported version of [thank-you-stars.](https://github.com/teppeis/thank-you-stars)

## Requirements

sbt 1.4.x or higher (tested only on 1.6.2)

## Setup

### Install sbt plugin

Add following line to `project/plugins.sbt` or `$HOME/.sbt/1.0/plugins/plugins.sbt`:

```scala
addSbtPlugin("com.mayreh" % "sbt-thank-you-stars" % "0.2")
```

### Prepare personal access token

#### Generate a token

1. Open https://github.com/settings/tokens
2. Check `public_repo` scope and Generate a token

#### Prepare environment

Prepare access token by one of following ways:

- Save the token as `$HOME/.thank-you-stars.json`
  - `echo '{"token":"YOUR_TOKEN"}' > ~/.thank-you-stars.json`
- Save the token as `/path/to/token.json`
  - `echo '{"token":"YOUR_TOKEN"}' > /path/to/token.json`
  - `export THANK_YOU_STARS_JSON_FILE=/path/to/token.json`
- Export the token as `THANK_YOU_STARS_GITHUB_TOKEN`
  - `export THANK_YOU_STARS_GITHUB_TOKEN=YOUR_TOKEN`

## Usage

```bash
$ cd /path/to/your-sbt-project
$ sbt thankYouStars
[info] Starred! https://github.com/kamon-io/Kamon
[info] ---- Skipped. No GitHub repo found for org.aspectj:aspectjrt:1.8.7
[info] Starred! https://github.com/spray/spray
```
