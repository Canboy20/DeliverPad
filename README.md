# DeliverPad
Displays a list of delivered items retrieved from an API with a RecyclerView that has been customised to have paging support

A list of delivered items is retrieved from an API and displayed on a RecylerView list using CardViews.
Instead of retrieving all the  items at once from the API, it retrieves 20 items at a time and when the user scrolls to the bottom of the list it will make another request to the API again to retrieve the next 20.
The RecyclerView has been customized in a way to support this pagination.

If in case the user is offline, a cache mechanism has been implemented to load data from the cache.
