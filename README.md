# DeliverPad
Displays a list of delivered items retrieved from an API with a RecyclerView that has been customised to have paging support.

Instead of retrieving all the  items at once from the API, it retrieves 20 items at a time and upon scrolling to the bottom of the list it will make another request to the API to retrieve the next 20 items and add them to the list. The pagination stops once the API either returns less than 20 items or no items at all. 
The RecyclerView has been customized in a way to support this pagination. All items on the list are displayed using a CardView.

If in case the user is offline, a cache mechanism has been implemented to load data from the cache(Data previously retrieved from API is cached). This will of course only work if the user has been able to retrieve some data while he/she was online before. Otherwise too bad for the user :(
