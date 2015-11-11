# Booklet (Alexandria)
A book list and barcode scanner app developed by Sascha Jaschke and improved by me for the third stage of Android Nanodegree Program.

Booklet was evaluated and graded as "Exceeds Specifications" by certified Udacity code reviewer.

## Features

The main features of the app:
* Find books via ISBN number or barcode scanning
* Save movies locally to view them even when offline
* Read book summary

## Google Books API

Movie Box uses [Google Books API](https://developers.google.com/books/) to retrieve books.
In order to be able to launch the app you have to get a valid API key from The Movie Database and add the following line to your gradle.properties file:

TheMovieDBAPIKey=YOUR_API_KEY

After that just replace YOUR_API_KEY with valid API key, obtained from The Movie Database.

## Screenshots

Before:

![screen](../blob/master/alexandria/screenshots/main_old.png)

![screen](../blob/master/alexandria/screenshots/detail_old.png)

After:

![screen](../blob/master/alexandria/screenshots/main_new.png)

![screen](../blob/master/alexandria/screenshots/detail_new.png)

## Libraries Used

* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Glide](https://github.com/bumptech/glide)
* [zxing Barcode Scanning](https://github.com/zxing/zxing)
* [Material Dialog](https://github.com/afollestad/material-dialogs)
* [Floating Action Button](https://github.com/Clans/FloatingActionButton)

## Android Developer Nanodegree
[![udacity][1]][2]

[1]: https://github.com/ogasimli/Udacity-SuperDuo/blob/master/alexandria/screenshots/nanodegree.png
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