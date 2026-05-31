## App

Application starts on:

```text
http://localhost:8080
```

## Endpoint

Returns all non-fork GitHub repositories for a given user together with repository branches and last commit SHA.

```http
GET /api/github/{username}/repositories
```

Example:

```http
GET /api/github/gszczure/repositories
```

### Successful response

```json
[
  {
    "repositoryName": "StockMarket",
    "ownerLogin": "gszczure",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "7fd1a60b01f91b314f59955a4e4d7d"
      }
    ]
  }
]
```

### User not found

```json
{
  "status": 404,
  "message": "GitHub user 'unknown' not found"
}
```

### GithubController

Exposes a REST endpoint for API consumers.

### GithubService

Contains business logic responsible for:

* filtering forked repositories,
* collecting repository branches,
* mapping GitHub responses to API response DTOs.

### GithubClient

Responsible for communication with GitHub REST API using Spring RestClient.

GitHub endpoints used by the application:

```http
GET https://api.github.com/users/{username}/repos
GET https://api.github.com/repos/{owner}/{repository}/branches
```

## Testing

The project contains integration tests only.

Tests use:

* Spring Boot Test,
* WireMock,
* RestAssured.

Covered scenarios:

1. Successful request:

    * repositories are fetched from GitHub,
    * forked repositories are filtered out,
    * branches and last commit SHA are returned.

2. GitHub user does not exist:

    * GitHub returns 404,
    * application returns 404 with expected error response.
