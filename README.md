One nice feature that Atcoder user profiles have is the ability to view a user's global rank across time (see an example [here](https://atcoder.jp/users/ksun48?graph=rank)).
Unfortunately, Codeforces does not support this feature, and even Atcoder only supports this for the top 100 ranks.

This is a simple REST API service that allows you to query any Codeforces user's ranking history.
Currently, it only supports global rankings (i.e., inactive users are still counted), and so its rankings may slightly differ from the current rankings that are shown at https://codeforces.com/ratings.
It can be used, for instance, to power websites or browser extensions that added extra rank information when browsing Codeforces profiles.

Queries are done by making a GET request to the endpoint `/gethandle` with the parameter `handle={handle}`.

For instance, we can access the ranking history of the user [Low-Deny-Cup](https://codeforces.com/profile/Low-Deny-Cup)[^1] using the command

```
curl 'http://{server-name}/gethandle?handle=Low-Deny-Cup'
```

[^1]: This user was chosen simply because it was high-rated user with a relatively low number of rated contests.

<details>
  <summary>Formatted output</summary>

  ```json
  [
  {
    "contest" : {
      "id" : 1821,
      "startTime" : 1682001300
    },
    "rank" : 335577,
    "rating" : 989
  },
  {
    "contest" : {
      "id" : 1822,
      "startTime" : 1682346900
    },
    "rank" : 67930,
    "rating" : 1576
  },
  {
    "contest" : {
      "id" : 1823,
      "startTime" : 1682606100
    },
    "rank" : 11201,
    "rating" : 2068
  },
  {
    "contest" : {
      "id" : 1826,
      "startTime" : 1683297300
    },
    "rank" : 6009,
    "rating" : 2174
  },
  {
    "contest" : {
      "id" : 1824,
      "startTime" : 1683547500
    },
    "rank" : 2126,
    "rating" : 2373
  },
  {
    "contest" : {
      "id" : 1827,
      "startTime" : 1684074900
    },
    "rank" : 503,
    "rating" : 2656
  },
  {
    "contest" : {
      "id" : 1830,
      "startTime" : 1685284500
    },
    "rank" : 528,
    "rating" : 2650
  },
  {
    "contest" : {
      "id" : 1835,
      "startTime" : 1687098900
    },
    "rank" : 215,
    "rating" : 2837
  },
  {
    "contest" : {
      "id" : 1842,
      "startTime" : 1687615500
    },
    "rank" : 227,
    "rating" : 2820
  },
  {
    "contest" : {
      "id" : 1844,
      "startTime" : 1689086100
    },
    "rank" : 197,
    "rating" : 2856
  },
  {
    "contest" : {
      "id" : 1852,
      "startTime" : 1690122900
    },
    "rank" : 129,
    "rating" : 2965
  },
  {
    "contest" : {
      "id" : 1854,
      "startTime" : 1690641300
    },
    "rank" : 142,
    "rating" : 2944
  },
  {
    "contest" : {
      "id" : 1863,
      "startTime" : 1693406100
    },
    "rank" : 167,
    "rating" : 2913
  },
  {
    "contest" : {
      "id" : 1868,
      "startTime" : 1694354700
    },
    "rank" : 125,
    "rating" : 2996
  },
  {
    "contest" : {
      "id" : 1870,
      "startTime" : 1695047700
    },
    "rank" : 74,
    "rating" : 3079
  },
  {
    "contest" : {
      "id" : 1898,
      "startTime" : 1700404500
    },
    "rank" : 66,
    "rating" : 3079
  }
]
  ```

</details>

### Building

To install, just run the gradle wrapper: `./gradlew build`.
You can start a local server on `localhost:8080` with `./gradlew bootRun`. Note that by default, this uses an in-memory database, and does not start with any actual rank data (it needs to query the Codeforces API for each contest, which can take a while).
If you would like to spin up a server with actual Codeforces data, you can run `./gradlew bootRun --args='--spring.profiles.active=prod'`.

You can also check out `INSTALLING.md` for instructions on how to deploy the application.

### Implementation notes

The application uses the Codeforces API to get a list of all rated contests and then downloads the rating changes for each contest in chronological order. This process is slow, as we cannot breach Codeforces' rate limit of 1 request per 2 seconds.
However, it only needs to be done once.
Assuming we're using a persistent database, the API requests don't need to be repeated on server restarts.
The server runs a check every 24 hours to look for new contests and update them as needed.

Answering GET requests does not need to call the Codeforces API at all.
Instead, the application maintains a database of all rating changes, as well a lookup table that stores the ranking of a user with rating X after a contest C.
Since ratings are roughly in the range [-100, 4000] and there are less than 2000 rated contests so far, this is a small enough table to store in memory and do efficient lookups.

The lookup table can also handle non-chronological contest updates, simply by recomputing all contest updates after this point.