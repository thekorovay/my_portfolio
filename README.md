# Korovay News App
### RU
### Android-приложение для поиска новостей для портфолио 

Последнюю версию вы можете скачать на [странице с релизами](https://github.com/thekorovay/my_portfolio/releases) (приложение совместимо с устройствами под управлением API 21 (Lollipop) и выше)

### Что было использовано в приложении 
- Android Navigation, ViewModel, LiveData, DataBinding, Coroutines, Material Theming, Localization
- RecyclerView с DiffCallback и несколькими ViewHolder
- Retrofit с Moshi
- Room
- Firebase (Authentication, Realtime Database, Crashlytics)
- Koin
- Glide
- Java Desugaring library 

### Заметки
- В качестве новостного API был использован [Web Search](https://rapidapi.com/contextualwebsearch/api/web-search) от RapidApi, поэтому поиск новостей доступен только на английском языке
- Каждый фрагмент содержит собственный тулбар, чтобы реализовать некоторые особенности библиотеки Material Design, н. сохранение позиции прокрутки тулбара отдельно для каждого фрагмента

### EN
### Android news application for my portfolio.

You can download latest version on [Releases page](https://github.com/thekorovay/my_portfolio/releases) (compatible with devices running API 21 (Lollipop) and above)

### What components and features were used in this app 
- Android Navigation, ViewModel, LiveData, DataBinding, Coroutines, Material Theming, Localization
- RecyclerView with DiffCallbacks and different ViewHolders  
- Retrofit with Moshi
- Room
- Firebase (Authentication, Realtime Database, Crashlytics)
- Koin
- Glide
- Java Desugaring library 

### Notes
- [Web Search](https://rapidapi.com/contextualwebsearch/api/web-search) by RapidApi was used as the news search API, so search is only possible in English
- Each fragment has its own Toolbar to support Material Design scrolling mechanics, e.g. saving separate Toolbar scroll position for each fragment 
