# Football Scores
An app that tracks current and future football matches. App was initially developed by XXX and improved by me for the third stage of Android Nanodegree Program.

Football Scores was evaluated and graded as "Exceeds Specifications" by certified Udacity code reviewer.

## Features

The main features of the app:
* Track current, future and past football matches
* Share match information
* Track match information directly from home screen via collection widget

# football-data API

Football Scores uses [football-data API](http://api.football-data.org/index) to retrieve match data.
In order to be able to launch the app you have to get a valid API key from football-data.org and add the following line to your gradle.properties file:

API_KEY=YOUR_API_KEY

After that just replace YOUR_API_KEY with valid API key, obtained from football-data.org.

## Screenshots

Before:

![screen](../master/screenshots/main_old.png)

After:

![screen](../master/screenshots/main_new.png)

![screen](../master/screenshots/widget_new.png)

## Libraries Used

* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Retrofit](https://github.com/square/retrofit)
* [Glide](https://github.com/bumptech/glide)
* [Ion](https://github.com/koush/ion)
* [Svg](https://github.com/BigBadaboom/androidsvg)
* [OkHttp](https://github.com/square/okhttp)
* [Joda Time](https://github.com/dlew/joda-time-android)
* [Material Dialog](https://github.com/afollestad/material-dialogs)

## Android Developer Nanodegree
[![udacity][1]][2]

[1]: https://github.com/ogasimli/Super-Duo/blob/master/football-scores/screenshots/nanodegree.png
[2]: https://www.udacity.com/course/android-developer-nanodegree--nd801

## License

    Copyright 2015 Orkhan Gasimli

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.