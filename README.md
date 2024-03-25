
# Challenge

This is a technical assignment


## API Reference

#### Login

```http
  POST /api/auth
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `username`      | `string` | **Required**. username of user |
| `password`      | `string` | **Required**. password of user |

#### Get info of authenticated user

```http
  GET /api/users/me
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `id` | `int` | id of user |
| `firstName` | `string` | firstname of user |
| `lastName` | `string` | lastName of user |
| `birthDate` | `date` | birthDate of user |
| `city` | `string` | city of user |
| `country` | `string` | country of user |
| `avatar` | `string` | avatar of user |
| `company` | `string` | company of user |
| `jobPosition` | `string` | jobPosition of user |
| `mobile` | `string` | mobile of user |
| `username` | `string` | username of user |
| `email` | `string` | email of user |
| `role` | `string` | role of user |

#### Get info of a specific user

```http
  GET /api/users/${usename}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `username`      | `string` | **Required**. Username of user to fetch |

#### Generate random data of users in a json file

```http
  GET /api/users/generate
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `count`      | `int` | **Required**. number of you users you want to generate in json file |

#### Batch Insert of users into db from json file

```http
  POST /api/users/batch
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `file`      | `file` | **Required**. json file with users data |

