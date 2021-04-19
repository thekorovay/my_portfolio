# Korovay News App
### Android news application for my portfolio. 

You can download latest version on [Releases page](https://github.com/thekorovay/my_portfolio/releases) (compatible with devices running API 21 (Lollipop) and above).

### What components and features were used in this app 
- Android Navigation, ViewModel, LiveData, DataBinding, Coroutines, Material Theming, Localization
- RecyclerView with DiffCallbacks and different ViewHolders  
- Retrofit with Moshi
- Room
- Firebase (Authentication, Realtime Database, Crashlytics)
- Glide
- Java Desugaring library 

### Notes
- [Web Search](https://rapidapi.com/contextualwebsearch/api/web-search) by RapidApi was used as the news search API, so search is only possible in English
- Each fragment has its own Toolbar to support Material Design scrolling mechanics, e.g. saving separate Toolbar scroll position for each fragment 
